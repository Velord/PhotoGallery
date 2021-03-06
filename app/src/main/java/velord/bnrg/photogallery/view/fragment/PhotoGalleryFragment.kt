package velord.bnrg.photogallery.view.fragment

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.application.sharedPreferences.QueryPreferences
import velord.bnrg.photogallery.model.photo.Photo
import velord.bnrg.photogallery.model.worker.PollWorker
import velord.bnrg.photogallery.view.activity.PhotoPageActivity
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


private const val TAG = "PhotoGalleryFragment"
private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : VisibleFragment() {

    private val viewModel: PhotoGalleryViewModel by lazy {
        ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
    }

    private lateinit var  photoRV: RecyclerView
    private lateinit var pb: ProgressBar

    private val vtoRv = object :
        ViewTreeObserver.OnGlobalLayoutListener{
        override fun onGlobalLayout() {
            val columnWidthInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                140f,
                activity!!.resources.displayMetrics
            )
            val width = photoRV.width
            val columnNumber = (width / columnWidthInPixels).roundToInt()
            photoRV.layoutManager = GridLayoutManager(activity, columnNumber)
            photoRV.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.photo_gallery_fragment, container, false).apply {
            initViews(this)
            initAdapter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $query")
                    query?.let {
                        viewModel.mutableSearchTerm.value = it
                        changeUIAfterSubmitTextInSearchView(searchItem, searchView)
                        return false
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "QueryTextChange: $newText")
                    return false
                }
            })

            setOnSearchClickListener {
                searchView.setQuery(viewModel.searchTerm, false)
            }

        }

        menu.findItem(R.id.menu_item_toggle_polling).apply {
            val isPolling = QueryPreferences.isPolling(requireContext())
            val toggleItemTitle =
                if (isPolling) R.string.stop_polling
                else R.string.start_polling
            setTitle(toggleItemTitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                viewModel.mutableSearchTerm.value = ""
                changeUIAfterSubmitTextInSearchView(item)
                true
            }
            R.id.menu_item_toggle_polling -> {
                val isPolling = QueryPreferences.isPolling(requireContext())
                if (isPolling) {
                    cancelPollWork()
                    QueryPreferences.setPolling(requireContext(), false)
                } else {
                    initPollWorker()
                    QueryPreferences.setPolling(requireContext(), true)
                }

                activity?.invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cancelPollWork() {
        WorkManager.getInstance().cancelUniqueWork(POLL_WORK)
    }

    private fun initPollWorker() {
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(
                POLL_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
    }

    private fun changeUIAfterSubmitTextInSearchView(searchItem: MenuItem,
                                                    searchView: SearchView = searchItem.actionView as SearchView) {
        //hide the soft keyboard and collapse the SearchView.
        searchItem.collapseActionView()
        searchView.onActionViewCollapsed()
        //show progress bar
        pb.visibility = View.VISIBLE
    }

    private fun observePhotoPagedList(photoAdapter: PhotoAdapter) {
        // Challenge: Observing View LifecycleOwner LiveData
        viewLifecycleOwnerLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if(it != null) {
                    viewModel.photoPagedList.observe(
                        viewLifecycleOwner,
                        Observer { photos ->
                            photoAdapter.submitList(photos)
                        }
                    )
                }
            }
        )
    }

    private fun initAdapter() {
        val photoAdapter = PhotoAdapter()
        observePhotoPagedList(photoAdapter)
        photoRV.adapter = photoAdapter
    }

    private fun initViews(view: View) {
        photoRV = view.findViewById(R.id.photo_recycler_view)
        photoRV.apply {
            viewTreeObserver.addOnGlobalLayoutListener(vtoRv)
        }

        pb = view.findViewById(R.id.photo_progress_bar)
    }

    private fun hideProgressBar() {
        if (pb.visibility == View.VISIBLE) pb.visibility = View.GONE
    }

    private val photoDiffCallback =
        object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo,
                                         newItem: Photo
            ): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: Photo,
                                            newItem: Photo
            ): Boolean =
                oldItem == newItem
        }
    private inner class PhotoAdapter
        : PagedListAdapter<Photo, PhotoAdapter.PhotoHolder>(photoDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false) as ImageView

            hideProgressBar()
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val photo = getItem(position)
            photo?.let {
                holder.bindPhoto(it)
            }
        }

        private inner class PhotoHolder(private val itemImageView: ImageView)
            : RecyclerView.ViewHolder(itemImageView),
            View.OnClickListener {

            private lateinit var photo: Photo

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val intent = PhotoPageActivity
                    .newIntent(requireContext(), photo.photoPageUri)
                startActivity(intent)
            }

            fun bindPhoto(photo: Photo) {
                this.photo = photo

                Glide
                    .with(itemImageView)
                    .load(photo.url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.bill_up_close)
                    .into(itemImageView)
            }
        }

    }
}

package velord.bnrg.photogallery.view.fragment

import android.os.Bundle
import android.os.StrictMode
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.model.Photo


private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }

    private val viewModel: PhotoGalleryViewModel by lazy {
        ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
    }

    private lateinit var  photoRV: RecyclerView

    private val vtoRv = object :
        ViewTreeObserver.OnGlobalLayoutListener{
        override fun onGlobalLayout() {
            val columnWidthInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                140f,
                activity!!.resources.displayMetrics
            )
            val width = photoRV.getWidth()
            val columnNumber = Math.round(width / columnWidthInPixels)
            photoRV.setLayoutManager(GridLayoutManager(activity, columnNumber))
            photoRV.getViewTreeObserver().removeOnGlobalLayoutListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()
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

    private fun observePhotoPagedList(photoAdapter: PhotoAdapter) {
        // Challenge: Observing View LifecycleOwner LiveData
        viewLifecycleOwnerLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if(it != null) {
                    viewModel.photoPagedList.observe(
                        viewLifecycleOwner,
                        Observer {
                            photoAdapter.submitList(it)
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
    }

    private inner class PhotoHolder(private val itemImageView: ImageView)
        : RecyclerView.ViewHolder(itemImageView) {

        fun bindGalleryItem(photo: Photo) {
            Glide
                .with(itemImageView)
                .load(photo.url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.bill_up_close)
                .into(itemImageView)
        }
    }


    private val PhotoDiffCallback =
        object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo,
                                         newItem: Photo): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: Photo,
                                            newItem: Photo): Boolean =
                oldItem == newItem
        }
    private inner class PhotoAdapter
        : PagedListAdapter<Photo, PhotoHolder>(PhotoDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false) as ImageView

            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val photo = getItem(position)
            photo?.let {
                holder.bindGalleryItem(it)
            }
        }
    }
}

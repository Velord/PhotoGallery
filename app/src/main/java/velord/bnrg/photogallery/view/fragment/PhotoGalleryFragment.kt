package velord.bnrg.photogallery.view.fragment

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.ThumbnailDownloader
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
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.photo_gallery_fragment, container, false).apply {
            viewLifecycleOwner.lifecycle.addObserver(
                thumbnailDownloader.viewLifecycleObserver
            )
            initViews(this)
            initAdapter()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        val responseHandler = Handler()
        thumbnailDownloader =
            ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
                val drawable = BitmapDrawable(resources, bitmap)
                photoHolder.bindDrawable(drawable)
            }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    private fun initAdapter() {
        val photoAdapter = PhotoAdapter()
        viewModel.photoPagedList.observe(
            viewLifecycleOwner,
            Observer {
            photoAdapter.submitList(it)
        })
        photoRV.adapter = photoAdapter
    }

    private fun initViews(view: View) {
        photoRV = view.findViewById(R.id.photo_recycler_view)
        photoRV.apply {
            viewTreeObserver.addOnGlobalLayoutListener(vtoRv)
        }
    }

    private inner class PhotoHolder(view: ImageView)
        : RecyclerView.ViewHolder(view) {

        val bindDrawable: (Drawable) -> Unit = view::setImageDrawable
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
                thumbnailDownloader.queueThumbnail(holder, photo.url)

                val placeHolder: Drawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bill_up_close
                ) ?: ColorDrawable()
                holder.bindDrawable(placeHolder)
            }
        }
    }
}

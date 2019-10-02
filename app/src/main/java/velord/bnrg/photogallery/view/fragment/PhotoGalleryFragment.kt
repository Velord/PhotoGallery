package velord.bnrg.photogallery.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.model.GalleryItem

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }

    private val viewModel: PhotoGalleryViewModel by lazy {
        ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
    }

    private lateinit var  photoRV: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.photo_gallery_fragment, container, false).apply {
            initViews(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModel.photosWithObserver(
            viewLifecycleOwner,
            Observer { galleryItems ->
                photoRV.adapter =
                    PhotoAdapter(
                        galleryItems
                    )
            }
        )
    }

    private fun initViews(view: View) {
        photoRV = view.findViewById(R.id.photo_recycler_view)
        photoRV.layoutManager = GridLayoutManager(context, 3)
    }

    private class PhotoHolder(itemTextView: TextView)
        : RecyclerView.ViewHolder(itemTextView) {

        val bindTitle: (CharSequence) -> Unit = itemTextView::setText
    }

    private class PhotoAdapter(private val galleryItems: List<GalleryItem>)
        : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val textView = TextView(parent.context)
            return PhotoHolder(textView)
        }

        override fun getItemCount(): Int  = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            galleryItems[position].also {
                holder.bindTitle(it.title)
            }
        }
    }
}

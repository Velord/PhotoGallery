package velord.bnrg.photogallery.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.photo_item.view.*
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
        photoRV.layoutManager = GridLayoutManager(context, 3)
    }

    private class PhotoHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        fun bind(photo: Photo?) {
            photo?.let {
                itemView.title.text = photo.title
            }
        }
    }

    private class PhotoAdapter
        : PagedListAdapter<Photo, PhotoHolder>(PhotoDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item, parent, false)

            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val photo = getItem(position)
            photo?.let { holder.bind(photo) }
        }

        companion object {
            val PhotoDiffCallback = object : DiffUtil.ItemCallback<Photo>() {
                override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}

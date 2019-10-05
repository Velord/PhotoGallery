package velord.bnrg.photogallery.view.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import velord.bnrg.photogallery.model.Photo
import velord.bnrg.photogallery.model.PhotoDataSource
import velord.bnrg.photogallery.model.PhotoDataSourceFactory


private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {

    private val pageSize = 100
    
    private var liveDataSource: LiveData<PhotoDataSource>
    var photoPagedList: LiveData<PagedList<Photo>>

    init {
        val itemDataSourceFactory = PhotoDataSourceFactory()
        liveDataSource = itemDataSourceFactory.photoDataSource

        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 3)
            .setPrefetchDistance(pageSize)
            .setEnablePlaceholders(false)
            .build()

        photoPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
            .setInitialLoadKey(0)
            .build()

    }
}
package velord.bnrg.photogallery.view.fragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.runBlocking
import velord.bnrg.photogallery.application.sharedPreferences.QueryPreferences
import velord.bnrg.photogallery.model.pagedListDataSource.InterestingnessPhotoDataSource
import velord.bnrg.photogallery.model.pagedListDataSource.PhotoDataSource
import velord.bnrg.photogallery.model.pagedListDataSource.PhotoDataSourceFactory
import velord.bnrg.photogallery.model.photo.Photo
import velord.bnrg.photogallery.repository.FlickrRepository
import velord.bnrg.photogallery.repository.api.FlickrApi


private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {

    private val flickrRepository = FlickrRepository(FlickrApi.invoke())

    private val pageSize = 30
    private val config = PagedList.Config.Builder()
        .setPageSize(pageSize)
        .setInitialLoadSizeHint(pageSize * 3)
        .setPrefetchDistance(pageSize)
        .setEnablePlaceholders(false)
        .build()

    private var itemDataSourceFactory =
        PhotoDataSourceFactory()
    private val liveDataSource = itemDataSourceFactory.photoDataSource

    var photoPagedList: LiveData<PagedList<Photo>>
    val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        photoPagedList = Transformations.switchMap(mutableSearchTerm) {
            changeDataSourceToFetchSearchPhotos(it)
            itemDataSourceFactory =
                if (it.isBlank()) {
                    PhotoDataSourceFactory(InterestingnessPhotoDataSource())
                } else {
                    val newDataSource = liveDataSource.value
                    PhotoDataSourceFactory(
                        PhotoDataSource(
                            newDataSource!!.f
                        )
                    )
                }
            LivePagedListBuilder(itemDataSourceFactory, config).build()
        }
    }

    private fun changeDataSourceToFetchSearchPhotos(query: String) {
        Log.i(TAG, "Change data source")
        QueryPreferences.setStoredQuery(app, query)
        liveDataSource.value = PhotoDataSource {
            runBlocking { flickrRepository.fetchSearchPhotos(query) }
        }
    }
}
package velord.bnrg.photogallery.view.fragment

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import velord.bnrg.photogallery.api.FlickrFetchr
import velord.bnrg.photogallery.model.GalleryItem

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {

    private val flickrFetch = FlickrFetchr()

    val photos = flickrFetch.fetchPhotos()
    val contest = flickrFetch.fetchContest()

    private fun <T> fetchContentWithObserver(owner: LifecycleOwner,
                                    obs: Observer<T>,
                                    fetchContent: () -> LiveData<T> ): LiveData<T> {
        return fetchContent().apply { observe(owner, obs) }
    }

    val contestWithObserver: (LifecycleOwner, Observer<String>)
    -> LiveData<String> = { owner, observer ->
        fetchContentWithObserver(owner, observer, { contest })
    }

    val photosWithObserver: (LifecycleOwner, Observer<List<GalleryItem>>)
    -> LiveData<List<GalleryItem>> = { owner, observer ->
        fetchContentWithObserver(owner, observer, { photos })
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetch
    }
}

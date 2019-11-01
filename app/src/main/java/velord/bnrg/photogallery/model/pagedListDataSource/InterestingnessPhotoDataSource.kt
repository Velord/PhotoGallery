package velord.bnrg.photogallery.model.pagedListDataSource

import kotlinx.coroutines.launch
import velord.bnrg.photogallery.model.photo.Photo
import velord.bnrg.photogallery.repository.FlickrRepository
import velord.bnrg.photogallery.repository.api.FlickrApi
import velord.bnrg.photogallery.utils.scope

class InterestingnessPhotoDataSource : PhotoDataSource() {
    private val repository =
        FlickrRepository(FlickrApi.invoke())
    private var page = 1

    override fun loadInitial(params: LoadInitialParams,
                             callback: LoadInitialCallback<Photo>) {
        scope().launch {
            val data = repository.fetchInterestingnessPhotos(page)
            callback.onResult(data, data.size)
        }
    }

    override fun loadRange(params: LoadRangeParams,
                           callback: LoadRangeCallback<Photo>) {
        scope().launch {
            val data = repository.fetchInterestingnessPhotos(++page)
            callback.onResult(data)
        }
    }
}
package velord.bnrg.photogallery.model

import androidx.paging.PositionalDataSource
import kotlinx.coroutines.*
import velord.bnrg.photogallery.repository.FlickrRepository
import velord.bnrg.photogallery.repository.api.FlickrApi
import kotlin.coroutines.CoroutineContext

private const val TAG = "PhotoDataSource"

class PhotoDataSource : PositionalDataSource<Photo>() {
    private val repository =
        FlickrRepository(FlickrApi.invoke())
    private var page = 1

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    override fun loadInitial(params: LoadInitialParams,
                             callback: LoadInitialCallback<Photo>) {
        scope.launch {
            val data = repository.fetchInterestingnessPhotos(page)
            callback.onResult(data, data.size)
        }
    }

    override fun loadRange(params: LoadRangeParams,
                           callback: LoadRangeCallback<Photo>) {
        scope.launch {
            val data = repository.fetchInterestingnessPhotos(++page)
            callback.onResult(data)
        }
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}
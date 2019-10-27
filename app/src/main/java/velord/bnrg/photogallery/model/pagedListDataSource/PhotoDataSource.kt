package velord.bnrg.photogallery.model.pagedListDataSource

import androidx.paging.PositionalDataSource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import velord.bnrg.photogallery.model.Photo
import velord.bnrg.photogallery.utils.coroutineContext
import velord.bnrg.photogallery.utils.scope

open class PhotoDataSource(
    val f: () -> List<Photo> = { listOf() } ) : PositionalDataSource<Photo>() {

    override fun loadInitial(params: LoadInitialParams,
                             callback: LoadInitialCallback<Photo>) {
        scope().launch {
            val data = f()
            callback.onResult(data, data.size)
        }
    }

    override fun loadRange(params: LoadRangeParams,
                           callback: LoadRangeCallback<Photo>) {
    }

    fun cancelAllRequests() = coroutineContext().cancel()
}
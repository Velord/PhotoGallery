package velord.bnrg.photogallery.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Deferred
import retrofit2.Response
import velord.bnrg.photogallery.model.Photo
import velord.bnrg.photogallery.repository.api.FlickrApi
import velord.bnrg.photogallery.repository.api.PhotoResponse

private const val TAG = "FlickrRepository"

class FlickrRepository(private val flickrApi: FlickrApi) : BaseRepository() {

    @WorkerThread
    suspend fun fetchInterestingnessPhotos(page: Int = 1): List<Photo>  =
        fetchPhotoList(flickrApi.fetchPhotosAsync(page),
            "${TAG}: Error Fetching photos by page $page")

    @WorkerThread
    suspend fun fetchMainPageContent(): String =
        safeApiCall({ flickrApi.fetchContentsAsync().await() },
            "${TAG}: Error Fetching Main Page Content") ?: ""

    @WorkerThread
    suspend fun fetchPhoto(url: String): Bitmap? {
        val response = safeApiCall(
            { flickrApi.fetchUrlBytesAsync(url).await() },
            "${TAG}: Error fetching photo by url"
        )
        val bitmap = response?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "${TAG}: decoded bitmap=$bitmap from Response=$response")

        return bitmap
    }

    suspend fun fetchSearchPhotos(query: String): List<Photo>  =
        fetchPhotoList( flickrApi.fetchSearchPhotosAsync(query),
            "${TAG}: Error while fetching photos by keyword search" )

    private suspend fun fetchPhotoList(
        flickrRequest: Deferred<Response<PhotoResponse>>,
        errorMessage: String): List<Photo>  =
        safeApiCall({ flickrRequest.await() }, errorMessage)?.galleryItems ?: listOf()

}
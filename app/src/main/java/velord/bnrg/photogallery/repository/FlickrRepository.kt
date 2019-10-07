package velord.bnrg.photogallery.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import velord.bnrg.photogallery.model.Photo
import velord.bnrg.photogallery.repository.api.FlickrApi

private const val TAG = "FlickrRepository"

class FlickrRepository(private val flickrApi: FlickrApi) : BaseRepository() {

    @WorkerThread
    suspend fun fetchInterestingnessPhotos(page: Int = 1): List<Photo> {
        val photoResponse = safeApiCall(
            call = { flickrApi.fetchPhotos(page).await() },
            errorMessage = "Error Fetching photos"
        )

        return photoResponse?.galleryItems ?: listOf()
    }

    @WorkerThread
    suspend fun fetchMainPageContent(): String {
        val response = safeApiCall(
            { flickrApi.fetchContents().await() },
            "Error Fetching Main Page Content"
        )

        return response ?: ""
    }

    @WorkerThread
    suspend fun fetchPhoto(url: String): Bitmap? {
        val response = safeApiCall(
            { flickrApi.fetchUrlBytes(url).await() },
            "Error fetching photo by url"
        )
        val bitmap = response?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "decoded bitmap=$bitmap from Response=$response")

        return bitmap
    }
}
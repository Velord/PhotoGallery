package velord.bnrg.photogallery.repository

import velord.bnrg.photogallery.model.Photo
import velord.bnrg.photogallery.repository.api.FlickrApi

private const val TAG = "FlickrRepository"

class FlickrRepository(private val flickrApi: FlickrApi) : BaseRepository() {

    suspend fun fetchInterestingnessPhotos(page: Int = 1): List<Photo> {
        val photoResponse = safeApiCall(
            call = { flickrApi.fetchPhotos(page).await() },
            errorMessage = "Error Fetching photos"
        )

        return photoResponse?.galleryItems ?: listOf()
    }


    suspend fun fetchMainPageContent(): String {
        val response = safeApiCall(
            { flickrApi.fetchContents().await() },
            "Error Fetching Main Page Content"
        )

        return response ?: ""
    }
}
package velord.bnrg.photogallery.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import velord.bnrg.photogallery.model.GalleryItem

private const val TAG = "FlickrFetchr"

class FlickrFetchr(private val flickrApi: FlickrApi) {

    private fun <T, U> fetchContent(apiCall: () -> Call<T>,
                                    callbackOnFailure: (Call<T> , Throwable)
                                    -> Any,
                                    callbackOnResponse: (Call<T>, Response<T>)
                                    -> U): LiveData<U> {
        val responseLiveData: MutableLiveData<U> = MutableLiveData()
        val flickrRequest: Call<T> = apiCall()
        val flickrCallBack = object : Callback<T> {

            override fun onFailure(call: Call<T>, t: Throwable) {
                callbackOnFailure(call, t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                responseLiveData.value = callbackOnResponse(call, response)
            }
        }

        flickrRequest.enqueue(flickrCallBack)
        return responseLiveData
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val flickrRequest:() -> Call<FlickrResponse> = {
            flickrApi.fetchPhotos()
        }
        val callbackOnFailure: (Call<FlickrResponse> , Throwable)
        -> Any = { call, t ->
            Log.e(TAG, "Failed to fetch photos", t)
        }
        val callbackOnResponse: (Call<FlickrResponse>,
                                 Response<FlickrResponse>)
        -> List<GalleryItem> = { call , response ->
            Log.d(TAG, "Response received: ${response.body()}")
            val flickrResponse: FlickrResponse? = response.body()
            val photoResponse: PhotoResponse? = flickrResponse?.photos
            var galleryItems: List<GalleryItem> =
                photoResponse?.galleryItems ?: mutableListOf()
            galleryItems = galleryItems.filterNot {
                it.url.isBlank()
            }
            galleryItems
        }
        return fetchContent(flickrRequest, callbackOnFailure, callbackOnResponse)
    }

    fun fetchContest(): LiveData<String> {
        val flickrRequest:() -> Call<String> = { flickrApi.fetchContents() }
        val callbackOnFailure: (Call<String> , Throwable) -> Any = { call, t ->
            Log.e(TAG, "Failed to fetch contest", t)
        }
        val callbackOnResponse: (Call<String>,
                                 Response<String>)
        -> String = { call , response ->
            Log.d(TAG, "Response received: ${response.body()}")
            response.body() ?: ""
        }
        return fetchContent(flickrRequest, callbackOnFailure, callbackOnResponse)
    }
}
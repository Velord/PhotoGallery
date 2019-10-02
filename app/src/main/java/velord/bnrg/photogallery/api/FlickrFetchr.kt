package velord.bnrg.photogallery.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import velord.bnrg.photogallery.model.GalleryItem

private const val TAG = "FlickrFetchr"

class FlickrFetchr {

    private val flickrApi: FlickrApi
    init {
        val gson = GsonBuilder()
            .registerTypeAdapter(PhotoResponse::class.java, PhotoDeserializer())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

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
        val flickrRequest:() -> Call<PhotoResponse> = {
            flickrApi.fetchPhotos()
        }
        val callbackOnFailure: (Call<PhotoResponse> , Throwable)
        -> Any = { call, t ->
            Log.e(TAG, "Failed to fetch photos", t)
        }
        val callbackOnResponse: (Call<PhotoResponse>,
                                 Response<PhotoResponse>)
        -> List<GalleryItem> = { call , response ->
            Log.d(TAG, "Response received: ${response.body()}")
            val photoResponse: PhotoResponse? = response.body()
            val galleryItems: List<GalleryItem> =
                (photoResponse?.galleryItems ?: mutableListOf())
                    .filterNot { it.url.isBlank() }
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
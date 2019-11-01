package velord.bnrg.photogallery.repository.api

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import velord.bnrg.photogallery.model.photo.PhotoResponse

interface FlickrApi {

    @GET("/")
    fun fetchContentsAsync(): Deferred<Response<String>>

    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhotosAsync(@Query("page") page: Int = 1):
            Deferred<Response<PhotoResponse>>

    @GET
    fun fetchUrlBytesAsync(@Url url: String):
            Deferred<Response<ResponseBody>>


    @GET("https://api.flickr.com/services/rest/?method=flickr.photos.search")
    fun fetchSearchPhotosAsync(@Query("text") query: String):
            Deferred<Response<PhotoResponse>>

    companion object {
        fun invoke() = FlickrApiBuilder().invoke()
    }
}
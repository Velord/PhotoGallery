package velord.bnrg.photogallery.repository.api

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private const val apiKey = "4fe3fb6d32a99cd3c4a5c4a94290202d"

interface FlickrApi {

    @GET("/")
    fun fetchContents(): Deferred<Response<String>>

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=$apiKey" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(@Query("page")page: Int = 1):
            Deferred<Response<PhotoResponse>>

    @GET
    fun fetchUrlBytes(@Url url: String): Deferred<Response<ResponseBody>>

    companion object {
        fun invoke(): FlickrApi {
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    PhotoResponse::class.java,
                    PhotoDeserializer()
                )
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.flickr.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

            return retrofit.create(FlickrApi::class.java)
        }
    }
}
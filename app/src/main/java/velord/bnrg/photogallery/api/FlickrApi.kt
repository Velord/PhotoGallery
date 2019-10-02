package velord.bnrg.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

private const val apiKey = "4fe3fb6d32a99cd3c4a5c4a94290202d"

interface FlickrApi {

    @GET("/")
    fun fetchContents(): Call<String>

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=4fe3fb6d32a99cd3c4a5c4a94290202d" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>
}
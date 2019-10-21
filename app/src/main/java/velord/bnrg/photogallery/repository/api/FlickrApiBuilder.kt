package velord.bnrg.photogallery.repository.api

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class FlickrApiBuilder {
    fun invoke(): FlickrApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

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
            .client(client)
            .build()

        return retrofit.create(FlickrApi::class.java)
    }
}
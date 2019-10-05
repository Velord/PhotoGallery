package velord.bnrg.photogallery.repository.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import velord.bnrg.photogallery.model.Photo
import java.lang.reflect.Type

class PhotoDeserializer : JsonDeserializer<PhotoResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {
        val jsonObject = json?.asJsonObject
        val photos = jsonObject?.get("photos")
            ?.asJsonObject?.get("photo")
            ?.asJsonArray?.map { jsonPhoto ->
            val photo: Photo
            jsonPhoto.asJsonObject.apply {
                val id = get("id").asString
                val title = get("title").asString
                val url = get("url_s").asString
                val height = get("height_s").asInt
                val width = get("width_s").asInt
                photo = Photo(id, title, url, height, width)
            }
            photo
        }

        return PhotoResponse().apply {
            galleryItems = photos ?: listOf()
        }
    }
}
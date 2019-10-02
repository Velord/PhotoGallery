package velord.bnrg.photogallery.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import velord.bnrg.photogallery.model.GalleryItem
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
            val photo: GalleryItem
            jsonPhoto.asJsonObject.apply {
                val id = get("id").asString
                val title = get("title").asString
                val url = get("url_s").asString
                val height = get("height_s").asInt
                val width = get("width_s").asInt
                photo = GalleryItem(id, title, url, height, width)
            }
            photo
        }

        return PhotoResponse().apply {
            galleryItems = photos ?: listOf()
        }
    }
}
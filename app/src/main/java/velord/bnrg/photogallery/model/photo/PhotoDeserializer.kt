package velord.bnrg.photogallery.model.photo

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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
            var photo = Photo("", "", "", "", -1, -1)
            jsonPhoto?.asJsonObject?.apply {
                photo = getPhotoFromJsonObject(this)
            }
            photo
        }

        return PhotoResponse().apply {
            galleryItems = photos ?: listOf()
        }
    }

    private fun getPhotoFromJsonObject(obj: JsonObject): Photo {
        val id = getIfHas("id", obj)?.asString ?: ""
        val title = getIfHas("title", obj)?.asString ?: ""
        val url = getIfHas("url_s", obj)?.asString ?: ""
        val owner = getIfHas("owner", obj)?.asString ?: ""
        val height = getIfHas("height_s", obj)?.asInt ?: -1
        val width = getIfHas("width_s", obj)?.asInt ?: -1

        return Photo(id, title, url, owner, height ,width)
    }

    private fun getIfHas(str: String, obj: JsonObject): JsonElement? =
        if (obj.has(str)) obj.get(str)
        else null
}
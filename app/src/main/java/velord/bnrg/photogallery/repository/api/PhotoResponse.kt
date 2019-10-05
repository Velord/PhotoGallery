package velord.bnrg.photogallery.repository.api

import com.google.gson.annotations.SerializedName
import velord.bnrg.photogallery.model.Photo

class PhotoResponse {
    @SerializedName("photos")
    lateinit var galleryItems: List<Photo>
}
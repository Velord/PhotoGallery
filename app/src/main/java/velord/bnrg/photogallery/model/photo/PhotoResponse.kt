package velord.bnrg.photogallery.model.photo

import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photos")
    lateinit var galleryItems: List<Photo>
}
package velord.bnrg.photogallery.api

import com.google.gson.annotations.SerializedName
import velord.bnrg.photogallery.model.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}
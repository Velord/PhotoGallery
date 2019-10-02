package velord.bnrg.photogallery.api

import com.google.gson.annotations.SerializedName
import velord.bnrg.photogallery.model.GalleryItem

class PhotoResponse {
    @SerializedName("photos")
    lateinit var galleryItems: List<GalleryItem>
}
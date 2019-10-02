package velord.bnrg.photogallery.model

data class GalleryItem(
  var id: String = "",
  var title: String = "",
  var url: String = "",
  var height: Int,
  var width: Int
)
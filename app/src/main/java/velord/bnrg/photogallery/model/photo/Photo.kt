package velord.bnrg.photogallery.model.photo

import android.net.Uri

data class Photo(
  var id: String = "",
  var title: String = "",
  var url: String = "",
  var owner: String = "",
  var height: Int,
  var width: Int
) {
  val photoPageUri: Uri
          get() {
            return Uri.parse("https://www.flickr.com/photos/")
              .buildUpon()
              .appendPath(owner)
              .appendPath(id)
              .build()
          }
}
package velord.bnrg.photogallery.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.utils.initFragment
import velord.bnrg.photogallery.view.fragment.PhotoPageFragment

class PhotoPageActivity : AppCompatActivity() {

    private val sf = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)
        initPhotoGPageFragment()

    }

    private fun initPhotoGPageFragment() =
        initFragment(sf,
            PhotoPageFragment.newInstance(intent.data ?: Uri.EMPTY),
            R.id.fragment_container
        )

    companion object {
        fun newIntent(context: Context, photoPageUri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).apply {
                data = photoPageUri
            }
        }
    }
}
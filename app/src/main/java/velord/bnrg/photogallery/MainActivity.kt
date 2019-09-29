package velord.bnrg.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import velord.bnrg.photogallery.utils.initFragment

class MainActivity : AppCompatActivity() {

    private val sf = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPhotoGalleryFragment()
    }

    private fun initPhotoGalleryFragment() =
        initFragment(sf, PhotoGalleryFragment(), R.id.fragment_container)
}

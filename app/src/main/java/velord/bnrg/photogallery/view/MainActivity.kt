package velord.bnrg.photogallery.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.utils.initFragment
import velord.bnrg.photogallery.view.fragment.PhotoGalleryFragment

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

package velord.bnrg.photogallery.view

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.utils.initFragment
import velord.bnrg.photogallery.view.fragment.PhotoGalleryFragment

class MainActivity : AppCompatActivity() {

    private val sf = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //you will hear about the following violations in Logcat:
        //networking on the main thread
        //disk reads and writes on the main thread
        //activities kept alive beyond their natural lifecycle (also known as an
        //“activity leak”)
        //unclosed SQLite database cursors
        //cleartext network traffic not wrapped in SSL/TLS
        StrictMode.enableDefaults()

        initPhotoGalleryFragment()
    }

    private fun initPhotoGalleryFragment() =
        initFragment(sf, PhotoGalleryFragment(), R.id.fragment_container)
}

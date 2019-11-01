package velord.bnrg.photogallery.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.utils.initFragment
import velord.bnrg.photogallery.view.fragment.PhotoPageFragment

class PhotoPageActivity : AppCompatActivity(), PhotoPageFragment.Callbacks {

    private val sf = supportFragmentManager

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)
        initPhotoGPageFragment()

    }

    override fun pressBack(webView: WebView) {
        this.webView = webView
    }

    override fun onBackPressed() {
        if (::webView.isInitialized)
            webView.apply {
                if (canGoBack()) goBack()
                else super.onBackPressed()
            }
        else
            super.onBackPressed()
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
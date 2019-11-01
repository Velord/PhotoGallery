package velord.bnrg.photogallery.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import velord.bnrg.photogallery.R

private const val ARG_URI = "photo_page_url"

class PhotoPageFragment : VisibleFragment() {

    interface Callbacks {
        fun pressBack(webView: WebView)
    }

    private var callback: Callbacks? = null

    private lateinit var uri: Uri
    private lateinit var webView: WebView
    private lateinit var pb: ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_page, container, false).apply {
            initViews(this)
        }
    }

    private fun initViews(view: View) {
        initProgressBar(view)
        initWebView(view)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(view: View) {
        webView = view.findViewById(R.id.web_view)
        webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100)
                        pb.visibility = View.GONE
                    else {
                        pb.visibility = View.VISIBLE
                        pb.progress = newProgress
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    (activity as AppCompatActivity).supportActionBar?.subtitle = title
                }
            }
            webViewClient =  WebViewClient()
            loadUrl(uri.toString())
        }
        callback?.pressBack(webView)
    }

    private fun initProgressBar(view: View) {
        pb = view.findViewById(R.id.pb_photo_page)
        pb.max = 100
    }

    companion object {
        fun newInstance(uri: Uri): PhotoPageFragment =
            PhotoPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
    }
}
package com.example.vmedvediev.redditapp.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.vmedvediev.redditapp.R
import kotlinx.android.synthetic.main.webview_activity.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)

        val url =  intent.getStringExtra("url")
        webView.apply {
            settings.javaScriptEnabled = true
            loadUrl(url)
            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    webviewLoadingProgressBar?.visibility = View.GONE
                    webviewProgressTextView?.visibility = View.GONE
                }
            }
        }
    }
}
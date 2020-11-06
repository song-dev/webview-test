package com.song.webview.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.*

class GTWebView : WebView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet,defStyle:Int) : super(context, attrs,defStyle) {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        settings.domStorageEnabled = false
        settings.javaScriptEnabled = true
        settings.blockNetworkImage = false
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadsImagesAutomatically = true
        settings.allowFileAccess = false
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.savePassword = false
        settings.setGeolocationEnabled(false)
        settings.allowContentAccess = false
        settings.setSupportZoom(true)
        settings.textZoom = 100
        settings.allowUniversalAccessFromFileURLs = false
        settings.allowFileAccess = false

        overScrollMode = View.OVER_SCROLL_NEVER
        isScrollContainer = false
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        webChromeClient = MyWebChromeClient()
        webViewClient = MyWebViewClient()

        setWebContentsDebuggingEnabled(true)
    }

    internal class MyWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.e("WebView", "onPageStarted: $url")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.e("WebView", "onPageFinished: $url")
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            Log.e("WebView", "onLoadResource: $url")
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(
                    "WebView",
                    "WebViewClient.onReceivedError: URL: " + request.url +
                            ", Method: " + request.method
                )
                Log.e(
                    "WebView",
                    "WebViewClient.onReceivedError: ErrorCode: " + error.errorCode +
                            ", Description: " + error.description.toString()
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e(
                    "WebView",
                    "WebViewClient.onReceivedError LOLLIPOP: URL: " + request.url +
                            ", Method: " + request.method
                )
            }
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.e("WebView", "WebViewClient.onReceivedError(Deprecated): URL: $failingUrl")
                Log.e(
                    "WebView",
                    "WebViewClient.onReceivedError(Deprecated): ErrorCode: " + errorCode +
                            ", Description: " + description
                )
            }
        }

        override fun onReceivedSslError(
            view: WebView,
            handler: SslErrorHandler,
            error: SslError
        ) {
            super.onReceivedSslError(view, handler, error)
            Log.e("WebView", "WebViewClient.onReceivedSslError: URL: " + error.url)
            Log.e(
                "WebView",
                "WebViewClient.onReceivedSslError: ErrorCode: " + error.primaryError +
                        ", Description: " + error.toString()
            )
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            Log.e("WebView", "WebViewClient.onReceivedHttpError: $request")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e(
                    "WebView",
                    "WebViewClient.onReceivedHttpError: URL: " + request.url +
                            ", Code: " + errorResponse.statusCode +
                            ", Message: " + errorResponse.reasonPhrase
                )
                Log.e("WebView", "WebViewClient.onReceivedHttpError: " + errorResponse.statusCode)
            }
        }
    }

    internal class MyWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            Log.e("WebView", "onReceivedTitle: $title")
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            Log.e("WebView", "onProgressChanged: $newProgress")
        }
    }

}
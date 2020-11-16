package com.song.webview.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.song.webview.R

class CustomWebView : WebView {

    init {
        init()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        // 基础设置项
        settings.javaScriptEnabled = true
        settings.blockNetworkImage = false // 设置从网络加载图片
        settings.loadsImagesAutomatically = true // 设置可否加载图片
        settings.javaScriptCanOpenWindowsAutomatically = false

        // 安全设置项
        var security = true
        val set: Set<String> = preferences.getStringSet("web_view_settings", setOf<String>()) as Set<String>
        if ("web_view_security" in set) {
            security = false
        }
        settings.allowFileAccess = security
        settings.savePassword = security
        settings.allowContentAccess = security
        settings.allowUniversalAccessFromFileURLs = security
        settings.allowFileAccessFromFileURLs = security
        settings.setGeolocationEnabled(security) // 设置定位

        // UI 设置项
        settings.setSupportZoom(true)
        settings.textZoom = 100
        val adaptation = "web_view_content_adaptation" in set
        settings.loadWithOverviewMode = adaptation
        settings.useWideViewPort = adaptation
        val scroll = "web_view_scroll" in set
        isHorizontalScrollBarEnabled = scroll
        isVerticalScrollBarEnabled = scroll
        isScrollContainer = scroll
        overScrollMode = if (scroll) View.OVER_SCROLL_IF_CONTENT_SCROLLS else View.OVER_SCROLL_NEVER
        val background = preferences.getBoolean("background", context.resources.getBoolean(R.bool.background_default))
        if (background) {
            val color = preferences.getInt("background_color", context.resources.getInteger(R.integer.background_color_default))
            setBackgroundColor(Color.rgb(color, color, color))
        } else {
            setBackgroundColor(Color.TRANSPARENT)
        }

        // 缓存设置项
        settings.domStorageEnabled = true
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        // 调试设置项
        val debug = "web_view_debug" in set
        setWebContentsDebuggingEnabled(debug)
        webChromeClient = MyWebChromeClient()
        webViewClient = MyWebViewClient()

        val buffer = StringBuilder()
        buffer.append("debug: $debug").append('\n')
        buffer.append("security: ${!security}").append('\n')
        buffer.append("scroll: $scroll").append('\n')
        buffer.append("adaptation: $adaptation").append('\n')
        buffer.append("background: $background")
        Toast.makeText(context, buffer.toString(), Toast.LENGTH_SHORT).show()
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
            handler.proceed()
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
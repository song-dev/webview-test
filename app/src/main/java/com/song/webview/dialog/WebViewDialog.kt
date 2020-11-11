package com.song.webview.dialog

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.song.webview.R
import com.song.webview.views.CustomWebView

class WebViewDialog : DialogFragment() {

    private lateinit var preferenceManager: SharedPreferences
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var url: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        width = preferenceManager.getInt("width", view.context.resources.getInteger(R.integer.width_default))
        height = preferenceManager.getInt("height", view.context.resources.getInteger(R.integer.height_default))
        url = preferenceManager.getString("url", view.context.resources.getString(R.string.web_url_default)).toString()
        val webView = view.findViewById<CustomWebView>(R.id.webView)
        val buffer = StringBuilder()
        buffer.append("width: $width").append('\n').append("height: $height").append('\n').append("url: $url")
        Toast.makeText(context, buffer.toString(), Toast.LENGTH_SHORT).show()
        webView.loadUrl(url)
    }

    override fun onStart() {
        val params = dialog!!.window!!.attributes
        params.width = (context?.resources?.displayMetrics?.widthPixels ?: 0) * width / 100
        params.height = (context?.resources?.displayMetrics?.heightPixels ?: 0) * height / 100
        params.gravity = Gravity.CENTER
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        dialog!!.setCanceledOnTouchOutside(preferenceManager.getBoolean("touch_cancel",
                dialog!!.context.resources.getBoolean(R.bool.touch_cancel_default)))
        super.onStart()
    }
}
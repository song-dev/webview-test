package com.song.webview.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.song.webview.R
import com.song.webview.dialog.WebViewDialog

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val button = root.findViewById<AppCompatButton>(R.id.btn_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            button.text = it
        })
        button.setOnClickListener(this)
        root.findViewById<AppCompatButton>(R.id.btn_jump).setOnClickListener(this)
        return root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_home -> WebViewDialog().show(parentFragmentManager, R.id.btn_home.toString())
            R.id.btn_jump -> {
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", v.context.packageName, null)
                startActivity(intent)
            }
        }
    }
}
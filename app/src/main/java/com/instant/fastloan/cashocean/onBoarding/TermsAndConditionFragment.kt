package com.toploans.cashOcean.onBoarding

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentTermsAndConditionBinding


class TermsAndConditionFragment : Fragment() {
    private lateinit var binding: FragmentTermsAndConditionBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_terms_and_condition,
            container,
            false
        )

        val webView = binding.webView
        webView.settings.javaScriptEnabled = true

        val url = "https://sites.google.com/view/cashoceanloan/home"

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                view.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                view.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }

        }
        webView.settings.javaScriptEnabled = true

        val settings = webView.settings
        settings.domStorageEnabled = true

        webView.loadUrl(url)

        binding.goBack.setOnClickListener {
            findNavController().navigate(R.id.policyFragment)
        }
        return binding.root
    }


}
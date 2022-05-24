package com.toploans.cashOcean.onBoarding

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentPolicyBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils


class PolicyFragment : Fragment() {
    private lateinit var binding: FragmentPolicyBinding
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var checkBox: CheckBox

    @SuppressLint("SetJavaScriptEnabled", "ShowToast")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        Handler().postDelayed({
//            sharedPreferenceUtils = SharedPreferenceUtils(requireActivity())
//            if (sharedPreferenceUtils.getOnBoarding()) {
//
//                if (sharedPreferenceUtils.getRegistered()) {
//                    if (!sharedPreferenceUtils.getMessage()) {
//                        findNavController().navigate(R.id.tillFragment)
//                    } else {
//                        findNavController().navigate(R.id.homeFragment)
//                    }
//                } else {
//                    findNavController().navigate(R.id.registerFragment)
//                }
//
//            } else {
//                findNavController().navigate(R.id.introFragment)
//            }


        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_policy, container, false)

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

        binding.viewTerms.setOnClickListener {
            findNavController().navigate(R.id.termsAndConditionFragment)
        }

        checkBox = binding.acceptTerms

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.acceptTermsError.visibility = View.INVISIBLE
                binding.continueReg.isEnabled = true
                Toast.makeText(this.requireContext(), "Hallo there", Toast.LENGTH_LONG).show()
            }

            if (!isChecked) {
                binding.continueReg.isEnabled = false
                binding.acceptTermsError.visibility = View.VISIBLE
            }
        }

        binding.progressBar.visibility = View.VISIBLE



        binding.continueReg.setOnClickListener {
            if (checkBox.isChecked) {
                binding.acceptTermsError.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                val sharedPreferenceManagerDetails = SharedPreferenceUtils(requireContext())
                sharedPreferenceManagerDetails.saveAcceptedPolicy(true)
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.phoneFragment)
                }, 3000)
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.acceptTermsError.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Accept our terms and conditions to continue",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return binding.root
    }


}
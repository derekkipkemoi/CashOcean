package com.toploans.cashOcean

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.toploans.cashOcean.databinding.FragmentHomeBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(), MaxAdListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0

    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewApp()
        createInterstitialAd()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        sharedPreferenceUtils = SharedPreferenceUtils(requireContext())

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.personalInformationFragment)
        }

        val sharedPreferenceUtils = SharedPreferenceUtils(this.requireContext())
        if (sharedPreferenceUtils.getApplicationComplete()) {
            binding.profile.visibility = View.VISIBLE
        }

        binding.profile.setOnClickListener {
            if  ( interstitialAd.isReady )
            {
                interstitialAd.showAd()
            }
            findNavController().navigate(R.id.dashboardFragment)
        }
        return view
    }

    private fun createInterstitialAd() {
        interstitialAd = MaxInterstitialAd(resources.getString(R.string.interstitial_id), this.requireActivity())
        interstitialAd.setListener(this)
        interstitialAd.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd?) {
        retryAttempt = 0.0
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        interstitialAd.loadAd()
    }

    override fun onAdHidden(ad: MaxAd?) {
        interstitialAd.loadAd()
    }

    override fun onAdClicked(ad: MaxAd?) {}

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis( Math.pow( 2.0, Math.min( 6.0, retryAttempt ) ).toLong() )
        Handler(Looper.getMainLooper()).postDelayed( { interstitialAd.loadAd() }, delayMillis )
    }
    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }

    private fun reviewApp(){
        reviewManager = ReviewManagerFactory.create(requireContext())
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                reviewInfo = request.result
                reviewInfo?.let {
                    val flow = reviewManager.launchReviewFlow(requireActivity(),it)
                    flow.addOnSuccessListener {}
                    flow.addOnFailureListener {}
                    flow.addOnCompleteListener {}
                }
            }
            else {
                reviewInfo = null
            }
        }
    }

}
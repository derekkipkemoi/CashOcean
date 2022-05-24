package com.toploans.cashOcean.onBoarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.toploans.cashOcean.MainActivity
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentPhoneBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import java.util.concurrent.TimeUnit

class PhoneFragment : Fragment(), MaxAdListener {
    private lateinit var binding: FragmentPhoneBinding

    private lateinit var interstitialAd: MaxInterstitialAd
    private var retryAttempt = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createInterstitialAd()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_phone,
            container,
            false
        )
        binding.buttonVerify.setOnClickListener {
            confirmCode()
        }

        return binding.root
    }


    private fun confirmCode() {
        binding.phoneTextInputLayout.error = null
        val phoneNumber = binding.textViewPhone.text.trim().toString()

        if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            binding.phoneTextInputLayout.error = "Valid Phone Number Required"
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            val verificationCode = (3463..4311).random()
            verificationCode(verificationCode)
            val sharedPreferencesUtils = SharedPreferenceUtils(this.requireContext())
            sharedPreferencesUtils.saveVerificationCode(verificationCode.toString())
            sharedPreferencesUtils.savePhoneNumber(phoneNumber)
            binding.progressBar.visibility = View.INVISIBLE
            if  ( interstitialAd.isReady )
            {
                interstitialAd.showAd()
            }
            findNavController().navigate(R.id.verifyCodeFragment)
        }, 3000)
    }


    private fun verificationCode(verificationCode: Int) {
        val pendingIntent = NavDeepLinkBuilder(this.requireContext())
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.verifyCodeFragment)
            .createPendingIntent()
        val builder =
            NotificationCompat.Builder(this.requireContext(), getString(R.string.channelID))
                .setSmallIcon(R.drawable.ic_code_message_24)
                .setContentTitle("Cash Ocean Verification Code")
                .setContentText("Your verification code is $verificationCode")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        builder.setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(this.requireContext())) {
            notify(12, builder.build())
        }
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

}
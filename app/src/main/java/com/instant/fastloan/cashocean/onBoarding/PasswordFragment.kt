package com.toploans.cashOcean.onBoarding

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
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentPasswordBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import java.util.concurrent.TimeUnit

class PasswordFragment : Fragment(), MaxAdListener {
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var binding: FragmentPasswordBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password, container, false)
        sharedPreferenceUtils = SharedPreferenceUtils(requireContext())

        binding.goBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonContinue.setOnClickListener {
            passwordConfirm()
        }
        return binding.root
    }

    private fun passwordConfirm() {
        binding.passwordTextInputLayout.error = null
        binding.passwordConfirmTextInputLayout.error = null
        val passwordValue = binding.textViewPassword.text.toString().trim()
        val passwordConfirmValue = binding.textViewPasswordConfirm.text.toString().trim()
        if (passwordValue.length < 6) {
            binding.passwordTextInputLayout.error = "Password should be longer than 6 digits"
            return
        }
        if (passwordConfirmValue != passwordValue) {
            binding.passwordConfirmTextInputLayout.error = "Your password do not match"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferenceUtils = SharedPreferenceUtils(this.requireContext())
            sharedPreferenceUtils.savePassword(passwordValue)
            sharedPreferenceUtils.saveRegistered(true)
            binding.progressBar.visibility = View.INVISIBLE
            if  ( interstitialAd.isReady )
            {
                interstitialAd.showAd()
            }
            findNavController().navigate(R.id.homeFragment)
        }, 3000)

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
        val delayMillis =
            TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6.0, retryAttempt)).toLong())
        Handler(Looper.getMainLooper()).postDelayed({ interstitialAd.loadAd() }, delayMillis)
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }

}
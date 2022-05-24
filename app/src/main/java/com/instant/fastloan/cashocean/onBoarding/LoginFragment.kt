package com.toploans.cashOcean.onBoarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentLoginBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment(), MaxAdListener {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var phoneNumber: String
    private lateinit var phoneNumberSaved: String
    private lateinit var password: String
    private lateinit var passwordSaved: String

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)


        binding.buttonLogIn.setOnClickListener {
            onClickLogInButton()
        }

        return binding.root
    }

    private fun onClickLogInButton() {
        val sharedPreferenceUtils = SharedPreferenceUtils(requireContext())
        phoneNumberSaved = sharedPreferenceUtils.getPhoneNumber()!!
        passwordSaved = sharedPreferenceUtils.getPassword()!!
        phoneNumber = binding.textViewPhone.text.toString().trim()
        password = binding.textViewPassword.text.toString().trim()
        binding.phoneTextInputLayout.error = null
        binding.passwordTextInputLayout.error = null



        if (phoneNumber != phoneNumberSaved) {
            Toast.makeText(
                this.requireContext(),
                "$phoneNumberSaved, $phoneNumber",
                Toast.LENGTH_LONG
            ).show()
            binding.phoneTextInputLayout.error = "Incorrect phone number"
            return
        }
        if (password != passwordSaved) {
            binding.passwordTextInputLayout.error = "Incorrect password"
            return
        }
        binding.progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
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
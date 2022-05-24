package com.toploans.cashOcean.onBoarding

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentLoanInformationBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import java.util.concurrent.TimeUnit

class LoanInformationFragment : Fragment(), MaxAdListener {
    private lateinit var binding: FragmentLoanInformationBinding
    private lateinit var sharedPreferenceUtil: SharedPreferenceUtils
    private var repaymentTime = "notSet"
    private var loanPurpose = "notSet"

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

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_loan_information, container, false)
        val view = binding.root
        sharedPreferenceUtil = SharedPreferenceUtils(requireContext())

        binding.goBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonSelectRepaymentTime.setOnClickListener {
            alertDialog(
                resources.getStringArray(R.array.repayment_time),
                "Select repayment time",
                "repaymentTime"
            )
        }

        binding.buttonSelectLoanPurpose.setOnClickListener {
            alertDialog(
                resources.getStringArray(R.array.loan_purpose),
                "Select loan purpose",
                "loanPurpose"
            )
        }


        binding.buttonAmountNext.setOnClickListener {
            userAmount()
        }

        return view
    }

    private fun userAmount() {
        binding.errorInput.visibility = View.INVISIBLE
        if (repaymentTime == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Repayment Time Required"
            return
        }
        if (loanPurpose == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Loan Purpose Required"
            return
        }
        binding.progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
            sharedPreferenceUtil.saveApplicationComplete(true)
            if  ( interstitialAd.isReady )
            {
                interstitialAd.showAd()
            }
            findNavController().navigate(R.id.tillFragment)
        }, 3000)
    }

    private fun alertDialog(list: Array<String>, listName: String, inputType: String) {
        val alertDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString(listName)
        alertDialog.setTitle(title)
        val selectedValueIndex = -1
        alertDialog.setSingleChoiceItems(list, selectedValueIndex) { _, which ->
            when (inputType) {
                "repaymentTime" -> {
                    binding.textViewRepaymentTime.text = list[which]
                    repaymentTime = list[which]
                }
                "loanPurpose" -> {
                    binding.textViewLoanPurpose.text = list[which]
                    loanPurpose = list[which]
                }
            }
            Toast.makeText(requireContext(), list[which], Toast.LENGTH_LONG).show()
        }
        alertDialog.setPositiveButton("Select",
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })
        alertDialog.create()
        alertDialog.show()
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
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
import com.toploans.cashOcean.databinding.FragmentWorkDetailsBinding
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class WorkDetailsFragment : Fragment(), MaxAdListener {

    private lateinit var binding: FragmentWorkDetailsBinding
    private var checkedValueIndex = -1
    private var workStatus = "notSet"
    private var monthlyIncome = "notSet"
    private var salaryFrequency = "notSet"
    private var salaryPayDay = "notSet"
    private var companyName = "notSet"

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_work_details, container, false)


        binding.buttonSelectWorkStatus.setOnClickListener {
            alertDialog(
                resources.getStringArray(R.array.work_status),
                "Select Work Status",
                "workStatus"
            )
        }
        binding.buttonSelectSalaryFrequency.setOnClickListener {
            alertDialog(
                resources.getStringArray(R.array.salary_frequency),
                "Select Salary Frequency",
                "salaryFrequency"
            )
        }
        binding.buttonSelectMontlyIncome.setOnClickListener {
            alertDialog(
                resources.getStringArray(R.array.monthly_income),
                "Select Monthly Income",
                "monthlyIncome"
            )
        }
        binding.buttonSelectSalaryPayDay.setOnClickListener {
            alertDialog(
                resources.getStringArray(R.array.salary_payday),
                "Select Salary Payday",
                "salaryPayDay"
            )
        }
        binding.buttonSubmit.setOnClickListener {
            checkValidation()
        }



        return binding.root
    }


    private fun alertDialog(list: Array<String>, listName: String, inputType: String) {
        val alertDialog = AlertDialog.Builder(this.requireContext())
        val title = SpannableString(listName)
        alertDialog.setTitle(title)
        alertDialog.setSingleChoiceItems(list, checkedValueIndex) { _, which ->
            checkedValueIndex = which
            when (inputType) {
                "workStatus" -> {
                    binding.workStatus.text = list[which]
                    workStatus = list[which]
                }
                "salaryFrequency" -> {
                    binding.salaryFrequency.text = list[which]
                    salaryFrequency = list[which]
                }
                "monthlyIncome" -> {
                    binding.monthlyIncome.text = list[which]
                    monthlyIncome = list[which]
                }
                "salaryPayDay" -> {
                    binding.salaryPayDay.text = list[which]
                    salaryPayDay = list[which]
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

    private fun checkValidation() {
        binding.companyNameTextInputLayout.error = null
        binding.errorInput.visibility = View.INVISIBLE
        companyName = binding.textViewCompanyName.text.toString().trim()

        val regx = "^[A-Za-z\\s]+[\\.]{0,1}[A-Za-z\\s]{0,}\$"
        val pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE)
        val companyNameMatcher: Matcher = pattern.matcher(companyName)

        if (!companyNameMatcher.matches()) {
            binding.companyNameTextInputLayout.error = "Valid Company Name Required"
            return
        }


        if (workStatus == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Work Status Required"
            return
        }

        if (salaryFrequency == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Salary Frequency Required"
            return
        }

        if (monthlyIncome == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Monthly Income Required"
            return
        }

        if (salaryPayDay == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Salary PayDay Required"
            return
        }



        binding.errorInput.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.INVISIBLE
            if  ( interstitialAd.isReady )
            {
                interstitialAd.showAd()
            }
            findNavController().navigate(R.id.loanAmountFragment)
        }, 3000)


    }

    private fun createInterstitialAd()
    {
        interstitialAd = MaxInterstitialAd( resources.getString(R.string.interstitial_id), this.requireActivity() )
        interstitialAd.setListener( this )
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
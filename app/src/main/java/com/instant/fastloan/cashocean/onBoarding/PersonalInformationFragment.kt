package com.toploans.cashOcean.onBoarding

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentPersonalInformationBinding
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.properties.Delegates

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class PersonalInformationFragment : Fragment(), MaxAdListener {

    private lateinit var binding: FragmentPersonalInformationBinding
    private lateinit var selectedDateOfBirth: String
    private var today = Calendar.getInstance()
    private var yearOfBirth = Calendar.getInstance().get(Calendar.YEAR)
    private var haveOutStandingLoan = "notSet"
    private var monthPicked by Delegates.notNull<Int>()
    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +
                //"(?=.*[0-9])" +  /
                // /at least 1 digit
                // "(?=.*[a-z])" +       //at least 1 lower case letter
                //"(?=.*[A-Z])" +       //at least 1 upper case letter
                //"(?=.*[a-zA-Z])" +    //any letter
                //"(?=.*[@#$%^&+=])" +  //at least 1 special character
                //"(?=\\S+$)" +         //no white spaces
                ".{6,}" +               //at least 4 characters
                "$"
    )

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
            R.layout.fragment_personal_information,
            container,
            false
        )
        val view = binding.root

        binding.buttonSignUp.setOnClickListener {
            userSignUp()
        }

        binding.goBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonSelectDob.setOnClickListener {
            val bottomSheetDialogDOB = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
            val bottomSheetViewDOB = LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.fragment_bottom_sheet_age_picker,
                    (requireActivity().findViewById(R.id.linear_layout_dob))
                )
            val datePicker = bottomSheetViewDOB.findViewById<DatePicker>(R.id.date_Picker)
            selectedDateOfBirth =
                "${today.get(Calendar.DAY_OF_MONTH)}/${today.get(Calendar.MONTH)}/${
                    today.get(Calendar.YEAR)
                }"
            datePicker.init(
                today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)

            ) { _, year, month, day ->
                monthPicked = month + 1
                yearOfBirth = year
                selectedDateOfBirth = "$day/$monthPicked/$year"

                Toast.makeText(this.requireContext(), selectedDateOfBirth, Toast.LENGTH_SHORT)
                    .show()
            }
            bottomSheetViewDOB.findViewById<Button>(R.id.button_set_dob).setOnClickListener {
                binding.dateOfBirth.text = selectedDateOfBirth
                bottomSheetDialogDOB.dismiss()
            }

            bottomSheetDialogDOB.setContentView(bottomSheetViewDOB)
            bottomSheetDialogDOB.show()
        }

        binding.buttonSelectOutstandingLoan.setOnClickListener {
            val builder = AlertDialog.Builder(this.activity)
            val list = resources.getStringArray(R.array.YesNo)
            builder.setTitle("Outstanding Loan")
                .setItems(R.array.YesNo,
                    DialogInterface.OnClickListener { dialog, which ->
                        binding.outstandingLoan.text = list[which]
                        haveOutStandingLoan = list[which]
                        Toast.makeText(requireContext(), list[which], Toast.LENGTH_LONG).show()
                    })
            builder.create()
            builder.show()
        }


        return view
    }

    private fun userSignUp() {
        val firstName = binding.textViewFirstName.text.toString().trim()
        val lastName = binding.textViewLastName.text.toString().trim()
        val email = binding.textViewEmail.text.toString().trim()
        val idNumber = binding.textViewId.text.toString().trim()

        binding.firstNameTextInputLayout.error = null
        binding.lastNameTextInputLayout.error = null
        binding.emailTextInputLayout.error = null
        binding.idTextInputLayout.error = null
        binding.errorInput.visibility = View.INVISIBLE

        val regx = "^[A-Za-z\\s]+[\\.]{0,1}[A-Za-z\\s]{0,}\$"
        val pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE)
        val firstNameMatcher: Matcher = pattern.matcher(firstName)
        val lastNameMatcher: Matcher = pattern.matcher(lastName)

        if (!firstNameMatcher.matches()) {
            binding.firstNameTextInputLayout.error = "Valid first Name Required!!"
            return
        }
        if (!lastNameMatcher.matches()) {
            binding.lastNameTextInputLayout.error = "Valid last Name Required!!"
            return
        }

        if (idNumber.length < 8) {
            binding.idTextInputLayout.error = "Valid ID Required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTextInputLayout.error = "Valid Email Address Required!!"
            return
        }
        if (today.get(Calendar.YEAR) - yearOfBirth < 18) {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = resources.getString(R.string.under18)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailTextInputLayout.error = "Valid Email Address Required!!"
            return
        }

        if (haveOutStandingLoan == "notSet") {
            binding.errorInput.visibility = View.VISIBLE
            binding.errorInput.text = "Outstanding Loan Required"
            return
        }


        val name = firstName.plus(" ").plus(lastName)
        binding.progressBar.visibility = View.VISIBLE





        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.visibility = View.INVISIBLE
            if  ( interstitialAd.isReady )
            {
                interstitialAd.showAd()
            }
            findNavController().navigate(R.id.workDetailsFragment)
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
        val delayMillis = TimeUnit.SECONDS.toMillis( Math.pow( 2.0, Math.min( 6.0, retryAttempt ) ).toLong() )
        Handler(Looper.getMainLooper()).postDelayed( { interstitialAd.loadAd() }, delayMillis )
    }
    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }
}


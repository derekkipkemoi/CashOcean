package com.toploans.cashOcean.onBoarding

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentCheckLoanLimitBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils

class CheckLoanLimitFragment : Fragment() {

    private lateinit var binding: FragmentCheckLoanLimitBinding
    private lateinit var sharedPreferenceUtil: SharedPreferenceUtils
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_check_loan_limit, container, false)
        val view = binding.root
        sharedPreferenceUtil = SharedPreferenceUtils(requireContext())


        val loanLimit = (4500..7500).random()

        binding.checkLoanLimit.setOnClickListener {
            Toast.makeText(requireContext(), "Calculating your loan limit", Toast.LENGTH_LONG)
                .show()
            binding.progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                binding.progressBar.visibility = View.INVISIBLE
                val builder = AlertDialog.Builder(requireContext())

                builder.setTitle("Loan Limit is")
                builder.setMessage("Ksh: $loanLimit")

                builder.setPositiveButton("Ok") { dialog: DialogInterface?, _: Int ->
                    dialog?.dismiss()
                }
                builder.create()
                builder.show()
            }, 3000)

        }

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.loanAmountFragment)
        }
        return view
    }

}
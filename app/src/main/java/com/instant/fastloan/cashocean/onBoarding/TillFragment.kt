package com.toploans.cashOcean.onBoarding

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
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
import com.toploans.cashOcean.databinding.FragmentTillBinding
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import java.util.*


class TillFragment : Fragment() {
    private lateinit var binding: FragmentTillBinding
    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var myClipboard: ClipboardManager
    private var myClip: ClipData? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_till, container, false)
        val view = binding.root

        val serviceFee = (100..150).random()

        binding.serviceFee.text = "Ksh $serviceFee"

        binding.buttonValidateMessage.setOnClickListener {
            validateMessage()
        }

        binding.buttonCopyTill.setOnClickListener {
            myClipboard = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val text = "5204479"
            val myClip = ClipData.newPlainText("text", text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(requireActivity(), "Till Number Copied Successfully", Toast.LENGTH_LONG)
                .show()
        }


        binding.pasteMessage.setOnClickListener {
            val textView = binding.textViewMessage
            myClipboard = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData = myClipboard.primaryClip!!
            if (clipData.getItemAt(0) == null) {
                binding.messageTextInputLayout.error = "No Message"
            } else {
                val item = clipData.getItemAt(0)
                textView.setText(item.text.toString())
            }

        }
        return view

    }

    private fun validateMessage() {

        val sentence = binding.textViewMessage.text.toString()

        val validateMessage = "Football Highway"

        binding.messageTextInputLayout.error = null

        if (sentence.lowercase(Locale.ROOT)
                .indexOf(validateMessage.lowercase(Locale.ROOT)) == -1
        ) {
            binding.messageTextInputLayout.error = "Please enter valid M-pesa Message"
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), "Message Validated", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.homeFragment)

        }, 3000)

    }


}
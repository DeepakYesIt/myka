package com.yesitlabs.mykaapp.fragment.authfragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.databinding.FragmentVerificationBinding
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import java.util.Locale

class VerificationFragment : Fragment() {
    private var binding: FragmentVerificationBinding? = null
    private var screenType:String?=null
    private var chooseType:String?=""
    private var value:String?=""
    private lateinit var commonWorkUtils: CommonWorkUtils
    private val START_TIME_IN_MILLIS: Long = 120000
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(inflater, container, false)

        if (arguments!=null){
            screenType= requireArguments().getString("screenType","")
            chooseType= requireArguments().getString("chooseType","")
            value= requireArguments().getString("value","")
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        commonWorkUtils= CommonWorkUtils(requireActivity())
        initialize()

        return binding!!.root
    }

    private fun initialize() {

        if (value!!.contains("@")){
            binding!!.tvCodeSent.text="we have sent the code to "+value
            binding!!.tvLogInType.text=" email"
        }else{
            binding!!.tvLogInType.text=" phone"

            binding!!.tvCodeSent.text="**********"

        }

        binding!!.imgBackVerification.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.textResend.setOnClickListener{
            binding!!.relResendVerificationTimer.visibility = View.VISIBLE
            binding!!.textResend.isEnabled = false
            startTime()
        }

        binding!!.rlVerificationVerify.setOnClickListener{
            if (validate()) {
                if (screenType == "signup") {
                    successDialog()
                } else  {
                    findNavController().navigate(R.id.resetPasswordFragment)
                }
            }
        }
    }

    private fun startTime() {
        object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                binding!!.textResend.setTextColor(Color.parseColor("#828282"))
                updateCountDownText()
            }

            override fun onFinish() {
                mTimeLeftInMillis = 120000
                binding!!.textResend.setTextColor(Color.parseColor("#06C169"))
                binding!!.relResendVerificationTimer.visibility = View.GONE
                binding!!.textResend.isEnabled = true
            }
        }.start()
    }

    private fun updateCountDownText() {
        val minutes = mTimeLeftInMillis.toInt() / 1000 / 60
        val seconds = mTimeLeftInMillis.toInt() / 1000 % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), " %02d:%02d", minutes, seconds)
        binding!!.tvTimer.text = timeLeftFormatted + " sec"
    }

    private fun validate(): Boolean {
        if (binding!!.otpView.otp.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.otp, false)
            return false
        } else if (binding!!.otpView.otp!!.length != 6) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.correctOtp, false)
            return false
        }
        return true
    }

    private fun successDialog() {
        val dialogSuccess: Dialog = context?.let { Dialog(it) }!!
        dialogSuccess.setContentView(R.layout.alert_dialog_singup_success)
        dialogSuccess.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dialogSuccess.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogSuccess.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogSuccess.show()
        dialogSuccess.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            dialogSuccess.dismiss()
            findNavController().navigate(R.id.turnOnLocationFragment)
        }

    }

}
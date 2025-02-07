package com.mykaimeal.planner.fragment.authfragment.verification

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.EnterYourNameActivity
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentVerificationBinding
import com.mykaimeal.planner.fragment.authfragment.forgotpassword.model.ForgotPasswordModel
import com.mykaimeal.planner.fragment.authfragment.verification.model.ForgotVerificationModel
import com.mykaimeal.planner.fragment.authfragment.verification.model.ResendSignUpOtpModel
import com.mykaimeal.planner.fragment.authfragment.verification.model.SignUpVerificationModel
import com.mykaimeal.planner.fragment.authfragment.verification.model.SignUpVerificationModelData
import com.mykaimeal.planner.fragment.authfragment.verification.viewmodel.VerificationViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class VerificationFragment : Fragment() {

    private var binding: FragmentVerificationBinding? = null
    private var screenType: String? = null
    private var chooseType: String? = ""
    private var value: String? = ""
    private lateinit var commonWorkUtils: CommonWorkUtils
    private val START_TIME_IN_MILLIS: Long = 120000
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    private lateinit var verificationViewModel: VerificationViewModel
    private lateinit var sessionManagement: SessionManagement

    private var userID: String? = ""
    private var userName: String? = ""
    private var cookingFor: String? = ""
    private var userGender: String? = ""
    private var partnerName: String? = ""
    private var partnerAge: String? = ""
    private var partnerGender: String? = ""
    private var familyMemName: String? = ""
    private var familyMemAge: String? = ""
    private var familyMemStatus: String? = ""
    private var bodyGoals: String? = ""
    private var dietarySelectedId = mutableListOf<String>()
    private var favouriteSelectedId = mutableListOf<String>()
    private var dislikeSelectedId = mutableListOf<String>()
    private var allergenSelectedId = mutableListOf<String>()
    private var mealRoutineSelectedId = mutableListOf<String>()
    private var cookingFrequency: String? = ""
    private var spendingAmount: String? = ""
    private var spendingDuration: String? = ""
    private var eatingOut: String? = ""
    private var reasonTakeAway: String? = ""
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(inflater, container, false)

        verificationViewModel = ViewModelProvider(this)[VerificationViewModel::class.java]

        /// argument part for check screen type & check value
        if (arguments != null) {
            screenType = requireArguments().getString("screenType", "")
            chooseType = requireArguments().getString("chooseType", "")
            value = requireArguments().getString("value", "")
        }

        /// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireActivity())

        ///main function using all triggered of this screen
        initialize()

        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialize() {

        /// check value is contains email aur phone
        if (value!!.contains("@")) {
            binding!!.tvCodeSent.text = "we have sent the code to $value"
            binding!!.tvLogInType.text = " email"
        } else {
            binding!!.tvLogInType.text = " phone"
            binding!!.tvCodeSent.text = "**********"
        }

        /// screen type value for signup screen
        if (screenType == "signup") {
            userID = requireArguments().getString("userId", "")

            if (sessionManagement.getCookingFor() == "Myself") {
                cookingFor = "1"
            } else if (sessionManagement.getCookingFor() == "MyPartner") {
                cookingFor = "2"
            } else {
                cookingFor = "3"
            }

            if (sessionManagement.getUserName() != "") {
                userName = sessionManagement.getUserName()
            }

            if (sessionManagement.getGender() != "") {
                userGender = sessionManagement.getGender()
            }

            if (sessionManagement.getPartnerName() != "") {
                partnerName = sessionManagement.getPartnerName()
            }

            if (sessionManagement.getPartnerAge() != "") {
                partnerAge = sessionManagement.getPartnerAge()
            }

            if (sessionManagement.getPartnerGender() != "") {
                partnerGender = sessionManagement.getPartnerGender()
            }

            if (sessionManagement.getFamilyMemName() != "") {
                familyMemName = sessionManagement.getFamilyMemName()
            }

            if (sessionManagement.getFamilyMemAge() != "") {
                familyMemAge = sessionManagement.getFamilyMemAge()
            }

            if (sessionManagement.getFamilyStatus() != "") {
                familyMemStatus = sessionManagement.getFamilyStatus()
            }

            if (sessionManagement.getBodyGoal() != "") {
                bodyGoals = sessionManagement.getBodyGoal()
            }

            if (sessionManagement.getDietaryRestrictionList() != null) {
                dietarySelectedId = sessionManagement.getDietaryRestrictionList()!!
            }

            if (sessionManagement.getFavouriteCuisineList() != null) {
                favouriteSelectedId = sessionManagement.getFavouriteCuisineList()!!
            }

            if (sessionManagement.getDislikeIngredientList() != null) {
                dislikeSelectedId = sessionManagement.getDislikeIngredientList()!!
            }

            if (sessionManagement.getAllergenIngredientList() != null) {
                allergenSelectedId = sessionManagement.getAllergenIngredientList()!!
            }

            if (sessionManagement.getMealRoutineList() != null) {
                mealRoutineSelectedId = sessionManagement.getMealRoutineList()!!
                if (mealRoutineSelectedId.contains("-1")){
                    mealRoutineSelectedId.remove("-1")
                }
            }

            if (sessionManagement.getCookingFrequency() != "") {
                cookingFrequency = sessionManagement.getCookingFrequency()
            }

            if (sessionManagement.getSpendingAmount() != "") {
                spendingAmount = sessionManagement.getSpendingAmount()
            }

            if (sessionManagement.getSpendingDuration() != "") {
                spendingDuration = sessionManagement.getSpendingDuration()
            }

            if (sessionManagement.getEatingOut() != "") {
                eatingOut = sessionManagement.getEatingOut()
            }

            if (sessionManagement.getReasonTakeAway() != "") {
                reasonTakeAway = sessionManagement.getReasonTakeAway()
            }
        }

        //// handle on back pressed
        binding!!.imgBackVerification.setOnClickListener {
            findNavController().navigateUp()
        }

        /// handle click event for resend password timer and api
        binding!!.textResend.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                if (screenType == "signup") {
                    signUpResendOtp()
                } else {
                    forgotPasswordApi()
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        //// handle click event for verify otp is valid or not
        binding!!.rlVerificationVerify.setOnClickListener {
            ///checking the device of mobile data in online and offline(show network error message) sign up otp verify api
            if (BaseApplication.isOnline(requireActivity())) {
                if (validate()) {
                    if (screenType == "signup") {
                        signUpOtpVerify()
                    } else {
                        forgotOtpVerifyApi()
                    }
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

    }

    private fun signUpResendOtp() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            verificationViewModel.resendSignUpModel({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val resendSignUpModel =
                                gson.fromJson(it.data, ResendSignUpOtpModel::class.java)
                            if (resendSignUpModel.code == 200 && resendSignUpModel.success) {
                                binding!!.relResendVerificationTimer.visibility = View.VISIBLE
                                binding!!.textResend.isEnabled = false
                                startTime()
                            } else {
                                if (resendSignUpModel.code == ErrorMessage.code) {
                                    showAlertFunction(resendSignUpModel.message, true)
                                } else {
                                    showAlertFunction(resendSignUpModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("verification@@","message"+e.message)
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }, value.toString())
        }


    }

    private fun forgotPasswordApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            verificationViewModel.forgotPassword({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val forgotModel = gson.fromJson(it.data, ForgotPasswordModel::class.java)
                            if (forgotModel.code == 200 && forgotModel.success) {
                                binding!!.relResendVerificationTimer.visibility = View.VISIBLE
                                binding!!.textResend.isEnabled = false
                                startTime()
                            } else {
                                if (forgotModel.code == ErrorMessage.code) {
                                    showAlertFunction(forgotModel.message, true)
                                } else {
                                    showAlertFunction(forgotModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("verification@@@","message"+e.message)

                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }, value.toString())
        }
    }


    private fun signUpOtpVerify() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            verificationViewModel.signUpOtpVerify(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            try {
                                val gson = Gson()
                                val signUpVerificationModel = gson.fromJson(it.data, SignUpVerificationModel::class.java)
                                if (signUpVerificationModel.code == 200 && signUpVerificationModel.success) {
                                    showDataInSession(signUpVerificationModel.data)
                                } else {
                                    if (signUpVerificationModel.code == ErrorMessage.code) {
                                        showAlertFunction(signUpVerificationModel.message, true)
                                    } else {
                                        showAlertFunction(signUpVerificationModel.message, false)
                                    }
                                }
                            }catch (e:Exception){
                                Log.d("verification@@@","message"+e.message)
                            }
                        }

                        is NetworkResult.Error -> {
                            showAlertFunction(it.message, false)
                        }

                        else -> {
                            showAlertFunction(it.message, false)
                        }
                    }
                },
                userID,
                binding!!.otpView.otp.toString(),
                userName,
                userGender,
                bodyGoals,
                cookingFrequency,
                eatingOut,
                reasonTakeAway,
                cookingFor,
                partnerName,
                partnerAge,
                partnerGender,
                familyMemName,
                familyMemAge,
                familyMemStatus,
                mealRoutineSelectedId,
                spendingAmount,
                spendingDuration,
                dietarySelectedId,
                favouriteSelectedId,
                allergenSelectedId,
                dislikeSelectedId,
                "Android",
                token
            )
        }
    }

    private fun showDataInSession(signUpVerificationModelData: SignUpVerificationModelData) {
        try {
            if (value!!.contains("@")) {
                sessionManagement.setEmail(value.toString())
            } else {
                sessionManagement.setPhone(value.toString())
            }

            if (signUpVerificationModelData.name != null) {
                sessionManagement.setUserName(signUpVerificationModelData.name)
            }

            if (signUpVerificationModelData.user_type == 1) {
                sessionManagement.setCookingFor("Myself")
            }

            if (signUpVerificationModelData.user_type == 2) {
                sessionManagement.setCookingFor("MyPartner")
            }

            if (signUpVerificationModelData.user_type == 3) {
                sessionManagement.setCookingFor("MyFamily")
            }

            if (signUpVerificationModelData.profile_img != null) {
                sessionManagement.setImage(signUpVerificationModelData.profile_img.toString())
            }

            if (signUpVerificationModelData.token != null) {
                sessionManagement.setAuthToken(signUpVerificationModelData.token.toString())
            }

            if (signUpVerificationModelData.id != null) {
                sessionManagement.setId(signUpVerificationModelData.id.toString())
            }

            if (signUpVerificationModelData.is_cooking_complete==0){
                val intent = Intent(requireActivity(), EnterYourNameActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                sessionManagement.setPreferences(true)
            }else{
                sessionManagement.setLoginSession(true)

                successDialog()
            }

        }catch (e:Exception){
            Log.d("verification","message"+e.message)
        }
    }

    /// implement forgot password verify api
    private fun forgotOtpVerifyApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            verificationViewModel.forgotOtpVerify(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            try {
                                val gson = Gson()
                                val forgotOtpModel =
                                    gson.fromJson(it.data, ForgotVerificationModel::class.java)
                                if (forgotOtpModel.code == 200 && forgotOtpModel.success) {
                                    try {
                                        val bundle = Bundle()
                                        bundle.putString("value", value)
                                        findNavController().navigate(R.id.resetPasswordFragment, bundle)
                                    }catch (e:Exception){
                                        Log.d("Verification","message"+e.message)
                                    }
                                } else {
                                    if (forgotOtpModel.code == ErrorMessage.code) {
                                        showAlertFunction(forgotOtpModel.message, true)
                                    } else {
                                        showAlertFunction(forgotOtpModel.message, false)
                                    }
                                }
                            }catch (e:Exception){
                                Log.d("Verification@@@","message"+e.message)

                            }
                        }

                        is NetworkResult.Error -> {
                            showAlertFunction(it.message, false)
                        }

                        else -> {
                            showAlertFunction(it.message, false)
                        }
                    }
                }, value.toString(), binding!!.otpView.otp.toString()
            )
        }
    }

    //// show error message
    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    /// start timer for counting 2 minutes
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

    /// update count timer
    private fun updateCountDownText() {
        val minutes = mTimeLeftInMillis.toInt() / 1000 / 60
        val seconds = mTimeLeftInMillis.toInt() / 1000 % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), " %02d:%02d", minutes, seconds)
        binding!!.tvTimer.text = timeLeftFormatted + " sec"
    }

    /// validation part
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

    //// this function is used for success password match & redirect location permission screen
    private fun successDialog() {
        val dialogSuccess: Dialog = context?.let { Dialog(it) }!!
        dialogSuccess.setContentView(R.layout.alert_dialog_singup_success)
        dialogSuccess.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogSuccess.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogSuccess.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogSuccess.show()
        dialogSuccess.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            dialogSuccess.dismiss()
            findNavController().navigate(R.id.turnOnLocationFragment)
        }

    }

    override fun onStart() {
        super.onStart()
        getFcmToken()
    }

    private fun getFcmToken() {
        lifecycleScope.launch {
            token = BaseApplication.fetchFcmToken()
        }
    }

}
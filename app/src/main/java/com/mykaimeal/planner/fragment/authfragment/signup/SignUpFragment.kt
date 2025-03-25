package com.mykaimeal.planner.fragment.authfragment.signup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.EnterYourNameActivity
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentSignUpBinding
import com.mykaimeal.planner.fragment.authfragment.signup.model.SignUpModel
import com.mykaimeal.planner.fragment.authfragment.signup.viewmodel.SignUpViewModel
import com.mykaimeal.planner.fragment.authfragment.verification.model.SignUpVerificationModel
import com.mykaimeal.planner.fragment.authfragment.verification.model.SignUpVerificationModelData
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var binding: FragmentSignUpBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private var chooseType: String = ""
    private lateinit var signUpViewModel: SignUpViewModel
    private val googleLogin = 100
    private lateinit var sessionManagement: SessionManagement
    private var mGoogleSignInClient: GoogleSignInClient? = null

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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        //// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginFragment)
                }
            })

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireActivity())

        ///main function using all triggered of this screen
        initialize()

        return binding!!.root
    }

    private fun initialize() {

        /// value get for social login
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

        logOutGoogle()

        //// handle click event for next login screen
        binding!!.tvLogIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        //// handle on back pressed
        binding!!.imagesBackSignUp.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding!!.googleImages.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, googleLogin)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        binding!!.imgEye.setOnClickListener {
            if (binding!!.etSignUpPassword.transformationMethod === PasswordTransformationMethod.getInstance()) {
                binding!!.etSignUpPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding!!.imgEye.setImageDrawable(resources.getDrawable(R.drawable.ic_password_eye))
                binding!!.etSignUpPassword.setSelection(binding!!.etSignUpPassword.text.length)
            } else {
                binding!!.etSignUpPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding!!.imgEye.setImageDrawable(resources.getDrawable(R.drawable.hide_pass))
                binding!!.etSignUpPassword.setSelection(binding!!.etSignUpPassword.text.length)
            }
        }

        //// add validation based  on email or phone & password
        ///checking the device of mobile data in online and offline(show network error message)
        //// implement signup api and redirection
        binding!!.rlSignUp.setOnClickListener {
            if (validate()) {
                if (BaseApplication.isOnline(requireActivity())) {
                    signUpApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }
    }

    private fun logOutGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient!!.signOut()
    }

    /// validation part
    private fun validate(): Boolean {

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding!!.etSignUpEmailPhone.text.toString().trim())
        val password = binding!!.etSignUpPassword.text.toString().trim()

        // Password Validation Conditions
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { "!@#\$%^&*()-_=+[{]};:'\",<.>/?".contains(it) }
        val isValidLength = password.length >= 6

        // Check if email/phone is empty
        if (binding!!.etSignUpEmailPhone.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.emailPhone, false)
            return false
        }
        // Check if email or phone is valid
        else if (!emailMatcher.find() && !validNumber()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.validEmailPhone, false)
            return false
        }
        // Check if password is empty
        else if (password.isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.password, false)
            return false
        }
        // Check password conditions individually and show specific errors
        else if (!isValidLength) {
            commonWorkUtils.alertDialog(
                requireActivity(),
                "Password must be at least 6 characters long.",
                false
            )
            return false
        } else if (!hasDigit) {
            commonWorkUtils.alertDialog(
                requireActivity(),
                "Password must contain at least one digit.",
                false
            )
            return false
        } else if (!hasUpperCase) {
            commonWorkUtils.alertDialog(
                requireActivity(),
                "Password must contain at least one uppercase letter.",
                false
            )
            return false
        } else if (!hasSpecialChar) {
            commonWorkUtils.alertDialog(
                requireActivity(),
                "Password must contain at least one special character.",
                false
            )
            return false
        }

        return true
    }

    //// signup api implement & redirection
    private fun signUpApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            signUpViewModel.signUpModel(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            try {
                                val gson = Gson()
                                val signUpModel = gson.fromJson(it.data, SignUpModel::class.java)
                                if (signUpModel.code == 200 && signUpModel.success) {
                                    try {
                                        val bundle = Bundle()
                                        bundle.putString("screenType", "signup")
                                        bundle.putString("chooseType", chooseType)
                                        bundle.putString("userId", signUpModel.data.id.toString())
                                        bundle.putString(
                                            "value",
                                            binding!!.etSignUpEmailPhone.text.toString().trim()
                                        )
                                        findNavController().navigate(R.id.verificationFragment, bundle)
                                    }catch (e:Exception){
                                        Log.d("signup","message:---"+e.message)
                                    }
                                } else {
                                    if (signUpModel.code == ErrorMessage.code) {
                                        showAlertFunction(signUpModel.message, true)
                                    } else {
                                        showAlertFunction(signUpModel.message, false)
                                    }
                                }
                            }catch (e:Exception){
                                Log.d("signup","message:---"+e.message)
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
                binding!!.etSignUpEmailPhone.text.toString().trim(),
                binding!!.etSignUpPassword.text.toString().trim()
            )
        }
    }

    //// show error message
    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    /// validation based on valid phone number
    private fun validNumber(): Boolean {
        val email: String = binding!!.etSignUpEmailPhone.text.toString().trim()
        if (email.length != 10) {
            return false
        }
        var onlyDigits = true
        for (i in 0 until email.length) {
            if (!Character.isDigit(email[i])) {
                onlyDigits = false
                break
            }
        }
        chooseType = "phone"
        return onlyDigits
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googleLogin) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val personEmail = account.email
                val personId = account.id
                val personPhoto = account.photoUrl
                Log.d("personId", "data....$personId")
                Log.d("personEmail", "data....$personEmail")
                Log.d("personPhoto", "data....$personPhoto")
                logOutGoogle()
                socialApi(personId, personEmail)
            } else {
                logOutGoogle()
                commonWorkUtils.alertDialog(requireActivity(), "Account not present.", false)
            }
        } catch (e: ApiException) {
            logOutGoogle()
            Log.d("*******", "Error :-" + e.message)
        }
    }

    private fun socialApi(personId: String?, personEmail: String?) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            signUpViewModel.socialLogin(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            Log.d("Api Response ","@@@@@@"+it.data)
                            val gson = Gson()
                            val signUpVerificationModel = gson.fromJson(it.data, SignUpVerificationModel::class.java)
                            if (signUpVerificationModel.code == 200 && signUpVerificationModel.success) {
                                showDataInSession(signUpVerificationModel.data, personEmail)
                            } else {
                                if (signUpVerificationModel.code == ErrorMessage.code) {
                                    showAlertFunction(signUpVerificationModel.message, true)
                                } else {
                                    showAlertFunction(signUpVerificationModel.message, false)
                                }
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
                personEmail,
                personId,
                userName,
                userGender,
                bodyGoals,
                cookingFrequency,
                eatingOut,
                reasonTakeAway,"",
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

    private fun showDataInSession(signUpVerificationModelData: SignUpVerificationModelData, personEmail: String?) {
        try {
            sessionManagement.setEmail(personEmail!!)

            if (signUpVerificationModelData.name != null) {
                sessionManagement.setUserName(signUpVerificationModelData.name)
            }

            if (signUpVerificationModelData.profile_img != null) {
                sessionManagement.setImage(signUpVerificationModelData.profile_img.toString())
            }

            val cookingFor = if (signUpVerificationModelData.cooking_for_type != null) {
                when (signUpVerificationModelData.cooking_for_type) {
                    1 -> "Myself"
                    2 -> "MyPartner"
                    3 -> "MyFamily"
                    else -> {
                        "Not Select"
                    }
                }
            } else {
                "Not Select"
            }

            sessionManagement.setCookingFor(cookingFor)

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
                sessionManagement.setPreferences(true)
            }else {
                sessionManagement.setLoginSession(true)

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }catch (e:Exception){
            Log.d("Signup","message:---"+e.message)
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
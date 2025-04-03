package com.mykaimeal.planner.fragment.authfragment.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.AuthActivity
import com.mykaimeal.planner.activity.EnterYourNameActivity
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.RememberMeAdapter
import com.mykaimeal.planner.adapter.RememberSelect
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentLoginBinding
import com.mykaimeal.planner.fragment.authfragment.login.model.LoginModel
import com.mykaimeal.planner.fragment.authfragment.login.model.LoginModelData
import com.mykaimeal.planner.fragment.authfragment.login.model.RememberMe
import com.mykaimeal.planner.fragment.authfragment.login.viewmodel.LoginViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var commonWorkUtils: CommonWorkUtils
    private var checkStatus: Boolean? = false
    private lateinit var loginViewModel: LoginViewModel
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
    private var reasonTakeAwayDesc: String? = ""
    private var token: String = ""
    private var isFirstTimeTouched = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as AuthActivity).type

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireContext())

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        /// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })


        ///main function using all triggered of this screen
        initialize()

        return binding.root
    }

    private fun getFcmToken() {
        lifecycleScope.launch {
            token = BaseApplication.fetchFcmToken()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initialize() {

        /// value get for social login
        cookingFor = when (sessionManagement.getCookingFor()) {
            "Myself" -> "1"
            "MyPartner" -> "2"
            else -> "3"
        }

        getValueFromSession()

        logOutGoogle()

        binding.etSignEmailPhone.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && isFirstTimeTouched) { // Only trigger on first touch
                val data: String = sessionManagement.getRememberMe()
                if (data.isNotEmpty()) {
                    showRememberDialog()
                }
                isFirstTimeTouched = false // Set flag to false so it doesn't trigger again
            }
        }


        binding.googleImages.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, googleLogin)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        // handle click event remember me login credentials
        binding.checkBoxImages.setOnClickListener {
            if (checkStatus == true) {
                binding.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                checkStatus = false
            } else {
                binding.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                checkStatus = true
            }
        }

        /// handle click event on signup & redirection Signup screen
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        binding.imgEye.setOnClickListener {
            if (binding.etSignPassword.transformationMethod === PasswordTransformationMethod.getInstance()) {
                binding.etSignPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgEye.setImageDrawable(resources.getDrawable(R.drawable.ic_password_eye))
                binding.etSignPassword.setSelection(binding.etSignPassword.text.length)
            } else {
                binding.etSignPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgEye.setImageDrawable(resources.getDrawable(R.drawable.hide_pass))
                binding.etSignPassword.setSelection(binding.etSignPassword.text.length)
            }
        }

        /// handle on back pressed
        binding.imagesBackLogin.setOnClickListener {
            requireActivity().finish()
        }

        /// handle click event on forgot password & redirection
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        /// handle click event on forgot password & redirection
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        /// handle click event login
        //// add validation based on valid email or phone and password
        ///checking the device of mobile data in online and offline(show network error message)
        binding.rlLogIn.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                if (validate()) {
                    loginApi()
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

    }

    private fun getValueFromSession() {
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

        if (sessionManagement.getReasonTakeAwayDesc() != "") {
            reasonTakeAwayDesc = sessionManagement.getReasonTakeAwayDesc()
        }
    }

    private fun showRememberDialog() {
        val filterList: MutableList<RememberMe> = ArrayList()
        var value: RememberMe? = null

        val dialogRemember: Dialog = context?.let { Dialog(it) }!!
        dialogRemember.setContentView(R.layout.dialog_remember)
        dialogRemember.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogRemember.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rvRememberData = dialogRemember.findViewById<RecyclerView>(R.id.rvRememberData)
        val rlOkayBtn = dialogRemember.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogRemember.show()
        dialogRemember.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        val data: String = sessionManagement.getRememberMe()

        if (data.isNotEmpty()) {
            val objectList = Gson().fromJson(data, Array<RememberMe>::class.java).asList()
            // Populate the filterList with the entire objectList (since type is not used anymore)
            filterList.addAll(objectList)
            val adapter = RememberMeAdapter(filterList, requireContext(), object : RememberSelect {
                override fun selectRemember(remember: RememberMe) {
                    value = remember
                }
            })
            rvRememberData.adapter = adapter
        }
        rlOkayBtn.setOnClickListener {
            if (value != null) {
                dialogRemember.dismiss()
                binding.etSignEmailPhone.setText(value?.email)
                binding.etSignPassword.setText(value?.pass)
                checkStatus = true
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

    /// login api implement and redirection
    private fun loginApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            loginViewModel.userLogin({
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            try {
                                val gson = Gson()
                                val loginModel = gson.fromJson(it.data, LoginModel::class.java)
                                if (loginModel.code == 200 && loginModel.success) {
                                    if (checkStatus == true) {
                                        saveRemember()
                                    }
                                    loginModel.data?.let {
                                        showDataInUi(loginModel.data)
                                    }?: run {
                                        showAlertFunction(loginModel.message, false)
                                    }
                                } else {
                                   handleCommon(loginModel.code,loginModel.message)
                                }
                            } catch (e: Exception) {
                                Log.d("Login", "message:---" + e.message)
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
                binding.etSignEmailPhone.text.toString().trim(),
                binding.etSignPassword.text.toString().trim(),
                ErrorMessage.deviceType, token
            )
        }
    }

    private fun saveRemember() {
        val data: String = sessionManagement.getRememberMe()
        var mutableList: MutableList<RememberMe> = ArrayList()
        if (!data.isNullOrEmpty()) {
            val objectList: List<RememberMe> = Gson().fromJson(data, Array<RememberMe>::class.java).asList()
            mutableList = objectList.toMutableList()
        }
        val emailOrPhone = binding.etSignEmailPhone.text.toString().trim()
        val password = binding.etSignPassword.text.toString().trim()
        var found = false
        for (item in mutableList) {
            if (item.email.equals(emailOrPhone,true)) {
                item.pass = password  // Update password if email exists
                found = true
                break
            }
        }
        if (!found) {
            mutableList.add(RememberMe(emailOrPhone, password)) // Add new entry if not found
        }
        sessionManagement.setRememberMe(mutableList)
    }

    /// handle set session and redirection implement
    private fun showDataInUi(loginModelData: LoginModelData) {

        try {

            sessionManagement.setEmail(loginModelData.email ?: "")

            sessionManagement.setUserName(loginModelData.name ?: "")

            sessionManagement.setReferralCode(loginModelData.referral_code ?: "")

            val cookingFor = when (loginModelData.cooking_for_type ?: -1) {
                1 -> "Myself"
                2 -> "MyPartner"
                3 -> "MyFamily"
                else -> "Not Select"
            }

            sessionManagement.setCookingFor(cookingFor)

            sessionManagement.setImage(loginModelData.profile_img?:"")

            sessionManagement.setAuthToken(loginModelData.token?:"")

            val id= loginModelData.id?:0
            sessionManagement.setId(id.toString())

            loginModelData.is_cooking_complete?.let {
                if (it == 0) {
                    sessionManagement.setPreferences(true)
                    val intent = Intent(requireActivity(), EnterYourNameActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    sessionManagement.setLoginSession(true)
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }

        } catch (e: Exception) {
            Log.d("Login", "message:---" + e.message)
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    /// add validation based on valid email or phone
    private fun validate(): Boolean {
        val emailPattern =ErrorMessage.emailPatter
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding.etSignEmailPhone.text.toString().trim())
        val password = binding.etSignPassword.text.toString().trim()

        // Password Validation Conditions
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { "!@#\$%^&*()-_=+[{]};:'\",<.>/?".contains(it) }
        val isValidLength = password.length >= 6

        // Check if email/phone is empty
        if (binding.etSignEmailPhone.text.toString().trim().isEmpty()) {
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
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.charactersPassword, false)
            return false
        } else if (!hasDigit) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.oneDigitPassword, false)
            return false
        } else if (!hasUpperCase) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.uppercaseLetterPassword, false)
            return false
        } else if (!hasSpecialChar) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.specialLetterPassword, false)
            return false
        }
        return true
    }

    /// add validation based on valid phone number
    private fun validNumber(): Boolean {
        val email: String = binding.etSignEmailPhone.text.toString().trim()
        if (email.length != 10) {
            return false
        }
        var onlyDigits = true
        for (element in email) {
            if (!Character.isDigit(element)) {
                onlyDigits = false
                break
            }
        }
        return onlyDigits
    }

    @Deprecated("Deprecated in Java")
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
                commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.googleError, false)
            }
        } catch (e: ApiException) {
            logOutGoogle()
            Log.d("*******", "Error :-" + e.message)
        }
    }

    private fun socialApi(personId: String?, personEmail: String?) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            loginViewModel.socialLogin(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            try {
                                val apiModel = Gson().fromJson(it.data, LoginModel::class.java)
                                if (apiModel.code == 200 && apiModel.success) {
                                    apiModel.data?.let {
                                        showDataInUi(apiModel.data)
                                    }?: run {
                                        showAlertFunction(apiModel.message, false)
                                    }
                                } else {
                                    handleCommon(apiModel.code,apiModel.message)
                                }
                            }catch (e:Exception){
                                Log.d("login", "message:---" + e.message)
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
                reasonTakeAway,reasonTakeAwayDesc,
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
                ErrorMessage.deviceType,
                token,sessionManagement.getReferralCode()
            )
        }
    }

    private fun handleCommon(code: Int, message: String) {
        if (code == ErrorMessage.code) {
            showAlertFunction(message, true)
        } else {
            showAlertFunction(message, false)
        }
    }


    override fun onStart() {
        super.onStart()
        getFcmToken()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
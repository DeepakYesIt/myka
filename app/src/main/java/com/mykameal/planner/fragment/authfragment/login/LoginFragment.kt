package com.mykameal.planner.fragment.authfragment.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.R
import com.mykameal.planner.activity.AuthActivity
import com.mykameal.planner.activity.LetsStartOptionActivity
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterAllergensIngItem
import com.mykameal.planner.adapter.RememberMeAdapter
import com.mykameal.planner.adapter.RememberSelect
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentLoginBinding
import com.mykameal.planner.commonworkutils.CommonWorkUtils
import com.mykameal.planner.fragment.authfragment.login.model.LoginModel
import com.mykameal.planner.fragment.authfragment.login.model.LoginModelData
import com.mykameal.planner.fragment.authfragment.login.model.RememberMe
import com.mykameal.planner.fragment.authfragment.login.model.SocialLoginModel
import com.mykameal.planner.fragment.authfragment.login.model.SocialLoginModelData
import com.mykameal.planner.fragment.authfragment.login.viewmodel.LoginViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
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
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)


        (activity as AuthActivity).type

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireContext())

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        /// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()*/
                    requireActivity().finish()
                }
            })


        ///main function using all triggered of this screen
        initialize()

        return binding!!.root
    }

    private fun getFcmToken() {
        lifecycleScope.launch {
            token = BaseApplication.fetchFcmToken()
        }
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

        binding!!.etSignEmailPhone.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            val data: String = sessionManagement.getRememberMe()
            if (data.isNotEmpty()){
                showRememberDialog()
            }
        }

        binding!!.googleImages.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, googleLogin)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        // handle click event remember me login credentials
        binding!!.checkBoxImages.setOnClickListener {
            if (checkStatus == true) {
                binding!!.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                checkStatus = false
            } else {
                binding!!.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                checkStatus = true
            }
        }

        /// handle click event on signup & redirection Signup screen
        binding!!.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        /// handle on back pressed
        binding!!.imagesBackLogin.setOnClickListener {
            /*val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
            startActivity(intent)
            requireActivity().finish()*/
            requireActivity().finish()
        }

        /// handle click event on forgot password & redirection
        binding!!.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        /// handle click event on forgot password & redirection
        binding!!.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        /// handle click event login
        //// add validation based on valid email or phone and password
        ///checking the device of mobile data in online and offline(show network error message)
        binding!!.rlLogIn.setOnClickListener {
            if (validate()) {
                if (BaseApplication.isOnline(requireActivity())) {
                    loginApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
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
                binding!!.etSignEmailPhone.setText(value?.email)
                binding!!.etSignPassword.setText(value?.pass)
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
            loginViewModel.userLogin(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            val gson = Gson()
                            val loginModel = gson.fromJson(it.data, LoginModel::class.java)
                            if (loginModel.code == 200 && loginModel.success) {
                                if (checkStatus == true) {
                                    saveRemember(
                                        binding!!.etSignEmailPhone.text.toString().trim(),
                                        binding!!.etSignPassword.text.toString().trim()
                                    )
                                }
                                showDataInUi(loginModel.data)
                            } else {
                                if (loginModel.code == ErrorMessage.code) {
                                    showAlertFunction(loginModel.message, true)
                                } else {
                                    showAlertFunction(loginModel.message, false)
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
                binding!!.etSignEmailPhone.text.toString().trim(),
                binding!!.etSignPassword.text.toString().trim(),
                "Android", token
            )
        }
    }

    private fun saveRemember(emailOrPhone: String, password: String) {
        val data: String = sessionManagement.getRememberMe()
        var checkDuplicate = false
        var mutableList: MutableList<RememberMe>? = ArrayList()
        if (data != null && data != "") {
            val objectList: List<RememberMe> = Gson().fromJson(data, Array<RememberMe>::class.java).asList()
            mutableList = objectList.toMutableList()
            Log.e("*****", objectList.toString())
            for (item in objectList) {
                if (item.email == emailOrPhone) {
                    checkDuplicate = true
                    break
                }
            }
        } else {
            mutableList?.add(RememberMe(emailOrPhone, password))
        }
        if (mutableList != null) {
            if (!checkDuplicate) {
                mutableList.add(RememberMe(emailOrPhone, password))
            }
            sessionManagement.setRememberMe(mutableList)
        }
    }

    /// handle set session and redirection implement
    private fun showDataInUi(loginModelData: LoginModelData) {
        sessionManagement.setLoginSession(true)

        if (loginModelData.email != null) {
            sessionManagement.setEmail(loginModelData.email)
        }

        if (loginModelData.name != null) {
            sessionManagement.setUserName(loginModelData.name)
        }

        val cookingFor = if (loginModelData.cooking_for_type != null) {
            when (loginModelData.cooking_for_type) {
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

        if (loginModelData.profile_img != null) {
            sessionManagement.setImage(loginModelData.profile_img.toString())
        }


        if (loginModelData.token != null) {
            sessionManagement.setAuthToken(loginModelData.token.toString())
        }

        if (loginModelData.id != null) {
            sessionManagement.setId(loginModelData.id.toString())
        }

        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()


    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    /// add validation based on valid email or phone
    private fun validate(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding!!.etSignEmailPhone.text.toString().trim())
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{6,}\$"
        val pattern = Pattern.compile(passwordPattern)
        val passMatchers = pattern.matcher(binding!!.etSignPassword.text.toString().trim())
        if (binding!!.etSignEmailPhone.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.emailPhone, false)
            return false
        } else if (!emailMatcher.find() && !validNumber()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.validEmailPhone, false)
            return false
        } else if (binding!!.etSignPassword.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.password, false)
            return false
        } else if (!passMatchers.find()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.passwordMatch, false)
            return false
        }
        return true
    }

    /// add validation based on valid phone number
    private fun validNumber(): Boolean {
        val email: String = binding!!.etSignEmailPhone.text.toString().trim()
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
        return onlyDigits
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager?.onActivityResult(requestCode, resultCode, data)
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
            loginViewModel.socialLogin(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            val apiModel =
                                Gson().fromJson(it.data, SocialLoginModel::class.java)
                            if (apiModel.code == 200 && apiModel.success) {
                                showDataInSession(apiModel.data)
                            } else {
                                if (apiModel.code == ErrorMessage.code) {
                                    showAlertFunction(apiModel.message, true)
                                } else {
                                    showAlertFunction(apiModel.message, false)
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

    private fun showDataInSession(signUpVerificationModelData: SocialLoginModelData) {

        sessionManagement.setLoginSession(true)

        if (signUpVerificationModelData.email != null) {
            sessionManagement.setEmail(signUpVerificationModelData.email)
        }

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

        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }


    override fun onStart() {
        super.onStart()
        getFcmToken()
    }

}
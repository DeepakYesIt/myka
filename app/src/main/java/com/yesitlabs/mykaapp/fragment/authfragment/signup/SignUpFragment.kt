package com.yesitlabs.mykaapp.fragment.authfragment.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.databinding.FragmentSignUpBinding
import com.yesitlabs.mykaapp.fragment.authfragment.signup.model.SignUpModel
import com.yesitlabs.mykaapp.fragment.authfragment.signup.viewmodel.SignUpViewModel
import com.yesitlabs.mykaapp.fragment.authfragment.verification.model.SignUpVerificationModel
import com.yesitlabs.mykaapp.fragment.authfragment.verification.model.SignUpVerificationModelData
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
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

    private var userName:String?=""
    private var cookingFor:String?=""
    private var userGender:String?=""
    private var partnerName:String?=""
    private var partnerAge:String?=""
    private var partnerGender:String?=""
    private var familyMemName:String?=""
    private var familyMemAge:String?=""
    private var familyMemStatus:String?=""
    private var bodyGoals:String?=""
    private var dietarySelectedId = mutableListOf<String>()
    private var favouriteSelectedId = mutableListOf<String>()
    private var dislikeSelectedId = mutableListOf<String>()
    private var allergenSelectedId = mutableListOf<String>()
    private var mealRoutineSelectedId = mutableListOf<String>()
    private var cookingFrequency:String?=""
    private var spendingAmount:String?=""
    private var spendingDuration:String?=""
    private var eatingOut:String?=""
    private var reasonTakeAway:String?=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]


        //// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
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
        if (sessionManagement.getUserName()!=""){
            userName=sessionManagement.getUserName()
        }

        if (sessionManagement.getGender()!=""){
            userGender=sessionManagement.getGender()
        }

        if (sessionManagement.getPartnerName()!=""){
            partnerName=sessionManagement.getPartnerName()
        }


        if (sessionManagement.getPartnerAge()!=""){
            partnerAge=sessionManagement.getPartnerAge()
        }

        if (sessionManagement.getPartnerGender()!=""){
            partnerGender=sessionManagement.getPartnerGender()
        }

        if (sessionManagement.getFamilyMemName()!=""){
            familyMemName=sessionManagement.getFamilyMemName()
        }

        if (sessionManagement.getFamilyMemAge()!=""){
            familyMemAge=sessionManagement.getFamilyMemAge()
        }

        if (sessionManagement.getFamilyStatus()!=""){
            familyMemStatus=sessionManagement.getFamilyStatus()
        }

        if (sessionManagement.getBodyGoal()!=""){
            bodyGoals=sessionManagement.getBodyGoal()
        }

        if (sessionManagement.getDietaryRestrictionList()!=null){
            dietarySelectedId= sessionManagement.getDietaryRestrictionList()!!
        }

        if (sessionManagement.getFavouriteCuisineList()!=null){
            favouriteSelectedId= sessionManagement.getFavouriteCuisineList()!!
        }

        if (sessionManagement.getDislikeIngredientList()!=null){
            dislikeSelectedId= sessionManagement.getDislikeIngredientList()!!
        }

        if (sessionManagement.getAllergenIngredientList()!=null){
            allergenSelectedId= sessionManagement.getAllergenIngredientList()!!
        }

        if (sessionManagement.getMealRoutineList()!=null){
            mealRoutineSelectedId= sessionManagement.getMealRoutineList()!!
        }

        if (sessionManagement.getCookingFrequency()!=""){
            cookingFrequency=sessionManagement.getCookingFrequency()
        }

        if (sessionManagement.getSpendingAmount()!=""){
            spendingAmount=sessionManagement.getSpendingAmount()
        }

        if (sessionManagement.getSpendingDuration()!=""){
            spendingDuration=sessionManagement.getSpendingDuration()
        }

        if (sessionManagement.getEatingOut()!=""){
            eatingOut=sessionManagement.getEatingOut()
        }

        if (sessionManagement.getReasonTakeAway()!=""){
            reasonTakeAway=sessionManagement.getReasonTakeAway()
        }

        logOutGoogle()

        //// handle click event for next login screen
        binding!!.tvLogIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        //// handle on back pressed
        binding!!.imagesBackSignUp.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.googleImages.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, googleLogin)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
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

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient!!.signOut()
    }

    /// validation part
    private fun validate(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding!!.etSignUpEmailPhone.text.toString().trim())
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{6,}\$"
        chooseType = "email"
        val pattern = Pattern.compile(passwordPattern)
        val passMatchers = pattern.matcher(binding!!.etSignUpPassword.text.toString().trim())
        if (binding!!.etSignUpEmailPhone.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.emailPhone, false)
            return false
        } else if (!emailMatcher.find() && !validNumber()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.validEmailPhone, false)
            return false
        } else if (binding!!.etSignUpPassword.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.password, false)
            return false
        } else if (!passMatchers.find()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.passwordMatch, false)
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
                            val gson = Gson()
                            val signUpModel = gson.fromJson(it.data, SignUpModel::class.java)
                            if (signUpModel.code == 200 && signUpModel.success) {
                                val bundle = Bundle()
                                bundle.putString("screenType", "signup")
                                bundle.putString("chooseType", chooseType)
                                bundle.putString("userId", signUpModel.data.id.toString())
                                bundle.putString(
                                    "value",
                                    binding!!.etSignUpEmailPhone.text.toString().trim()
                                )
                                findNavController().navigate(R.id.verificationFragment, bundle)

                            } else {
                                if (signUpModel.code == ErrorMessage.code) {
                                    showAlertFunction(signUpModel.message, true)
                                } else {
                                    showAlertFunction(signUpModel.message, false)
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
            signUpViewModel.socialLogin(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            val gson = Gson()
                            val signUpVerificationModel =
                                gson.fromJson(it.data, SignUpVerificationModel::class.java)
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
                reasonTakeAway,
                cookingFor,
                partnerName,
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
                ""
            )
        }
    }

    private fun showDataInSession(
        signUpVerificationModelData: SignUpVerificationModelData,
        personEmail: String?
    ) {

        sessionManagement.setLoginSession(true)
        sessionManagement.setEmail(personEmail!!)

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

        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)

    }

}
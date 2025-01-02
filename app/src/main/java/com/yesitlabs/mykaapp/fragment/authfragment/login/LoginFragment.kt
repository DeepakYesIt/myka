package com.yesitlabs.mykaapp.fragment.authfragment.login

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
import com.google.gson.Gson
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentLoginBinding
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.fragment.authfragment.login.model.LoginModel
import com.yesitlabs.mykaapp.fragment.authfragment.login.model.LoginModelData
import com.yesitlabs.mykaapp.fragment.authfragment.login.viewmodel.LoginViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private var checkStatus: Boolean? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sessionManagement: SessionManagement


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireContext())

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.checkBoxImages.setOnClickListener {
            if (checkStatus == true) {
                binding!!.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                checkStatus = false
            } else {
                binding!!.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                checkStatus = true
            }
        }

        binding!!.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        binding!!.imagesBackLogin.setOnClickListener {
//            val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
//            startActivity(intent)
            findNavController().navigateUp()
        }

        binding!!.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding!!.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

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

    private fun loginApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            loginViewModel.userLogin({
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            val gson = Gson()
                            val loginModel = gson.fromJson(it.data, LoginModel::class.java)
                            Log.d("@@@ Response profile", "message :- ${it.data}")
                            if (loginModel.code == 200 && loginModel.success) {
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
                        }else -> {
                            showAlertFunction(it.message, false)
                        }
                    }
                },
                binding!!.etSignEmailPhone.text.toString().trim(),
                binding!!.etSignPassword.text.toString().trim(),
                "Android",
                BaseApplication.getToken()
            )
        }
    }

    private fun showDataInUi(loginModelData: LoginModelData) {

        sessionManagement.setLoginSession(true)
        sessionManagement.setEmail(binding!!.etSignEmailPhone.text.toString().trim())

        if (loginModelData.name != null) {
            sessionManagement.setUserName(loginModelData.name)
        }

        if (loginModelData.profile_img != null) {
            sessionManagement.setImage(loginModelData.profile_img.toString())
        }

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
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun validate(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding!!.etSignEmailPhone.text.toString().trim())
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{6,}\$"
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

}
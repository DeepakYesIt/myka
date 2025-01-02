package com.yesitlabs.mykaapp.fragment.authfragment.forgotpassword

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
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.databinding.FragmentForgotPasswordBinding
import com.yesitlabs.mykaapp.fragment.authfragment.forgotpassword.model.ForgotPasswordModel
import com.yesitlabs.mykaapp.fragment.authfragment.forgotpassword.viewmodel.ForgotPasswordViewModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.viewmodel.BodyGoalViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var binding: FragmentForgotPasswordBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private var chooseType: String? = ""
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        commonWorkUtils = CommonWorkUtils(requireActivity())

        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]

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

        binding!!.imagesBackForgot.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.rlSubmit.setOnClickListener {
            if (validate()) {
                if (BaseApplication.isOnline(requireActivity())) {
                    forgotPasswordApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }
    }

    private fun forgotPasswordApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            forgotPasswordViewModel.forgotPassword({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val forgotModel = gson.fromJson(it.data, ForgotPasswordModel::class.java)
                        Log.d("@@@ Response profile", "message :- ${it.data}")
                        if (forgotModel.code == 200 && forgotModel.success) {
                            val bundle = Bundle()
                            bundle.putString("screenType", "forgot")
                            bundle.putString("chooseType", chooseType)
                            bundle.putString(
                                "value",
                                binding!!.etRegEmailPhone.text.toString().trim()
                            )
                            findNavController().navigate(R.id.verificationFragment, bundle)
                        } else {
                            if (forgotModel.code == ErrorMessage.code) {
                                showAlertFunction(forgotModel.message, true)
                            } else {
                                showAlertFunction(forgotModel.message, false)
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
            }, binding!!.etRegEmailPhone.text.toString().trim())
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun validate(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding!!.etRegEmailPhone.text.toString().trim())
        chooseType = "email"
        if (binding!!.etRegEmailPhone.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.registeredEmailPhone, false)
            return false
        } else if (!emailMatcher.find() && !validNumber()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.validEmailPhone, false)
            return false
        }
        return true
    }

    private fun validNumber(): Boolean {
        val phone: String = binding!!.etRegEmailPhone.text.toString().trim()
        if (phone.length != 10) {
            return false
        }
        var onlyDigits = true
        for (i in 0 until phone.length) {
            if (!Character.isDigit(phone[i])) {
                onlyDigits = false
                break
            }
        }
        chooseType = "phone"
        return onlyDigits
    }


}
package com.mykaimeal.planner.fragment.authfragment.resetpassword

import android.app.Dialog
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
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentResetPasswordBinding
import com.mykaimeal.planner.fragment.authfragment.resetpassword.model.ResetPasswordModel
import com.mykaimeal.planner.fragment.authfragment.resetpassword.viewmodel.ResetPasswordViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private var binding: FragmentResetPasswordBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel
    private var value: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        commonWorkUtils = CommonWorkUtils(requireActivity())

        resetPasswordViewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]

        ///get arguments value of previous screen
        if (arguments != null) {
            value = requireArguments().getString("value", "")
        }

        /// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        ///main function using all triggered of this screen
        initialize()

        return binding!!.root
    }

    private fun initialize() {

        //// handle on back pressed
        binding!!.imagesBackReset.setOnClickListener {
            findNavController().navigateUp()
        }

        /// add validation based on password & confirm password
        ///checking the device of mobile data in online and offline(show network error message)
        /// reset password api implement and redirection on login screen
        binding!!.rlResetSubmit.setOnClickListener {
            if (validate()) {
                if (BaseApplication.isOnline(requireActivity())) {
                    resetPasswordApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }
    }

    //// implement reset password api
    private fun resetPasswordApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            resetPasswordViewModel.resetPassword(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            try {
                                val gson = Gson()
                                val resetModel = gson.fromJson(it.data, ResetPasswordModel::class.java)
                                if (resetModel.code == 200 && resetModel.success) {
                                    resetDialog()
                                } else {
                                    if (resetModel.code == ErrorMessage.code) {
                                        showAlertFunction(resetModel.message, true)
                                    } else {
                                        showAlertFunction(resetModel.message, false)
                                    }
                                }
                            }catch (e:Exception){
                                Log.d("Reset Password","message:---"+e.message)
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
                value.toString(),
                binding!!.etCreatePassword.text.toString().trim(),
                binding!!.etConfirmPassword.text.toString().trim()
            )
        }
    }

    //// show error message
    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    /// show dialog for password change successfully
    private fun resetDialog() {
        val dialogPasswordChange: Dialog = context?.let { Dialog(it) }!!
        dialogPasswordChange.setContentView(R.layout.alert_dialog_password_changed)
        dialogPasswordChange.setCancelable(false)
        dialogPasswordChange.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogPasswordChange.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogPasswordChange.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogPasswordChange.show()
        dialogPasswordChange.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            dialogPasswordChange.dismiss()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    //// validation part
    private fun validate(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{6,}\$"
        val pattern = Pattern.compile(passwordPattern)
        val passMatcher = pattern.matcher(binding!!.etCreatePassword.text.toString().trim())
        if (binding!!.etCreatePassword.text.toString().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.password, false)
            return false
        } else if (binding!!.etConfirmPassword.text.toString().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.confirmPassword, false)
            return false
        } else if (!passMatcher.find()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.passwordMatch, false)
            return false
        } else if (binding!!.etCreatePassword.text.toString()
                .trim() != binding!!.etConfirmPassword.text.toString().trim()
        ) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.passwordSame, false)
            return false
        }
        return true
    }


}
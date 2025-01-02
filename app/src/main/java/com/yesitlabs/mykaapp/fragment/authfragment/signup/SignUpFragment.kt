package com.yesitlabs.mykaapp.fragment.authfragment.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.databinding.FragmentSignUpBinding
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var binding: FragmentSignUpBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private var chooseType:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

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

        binding!!.tvLogIn.setOnClickListener{
            findNavController().navigate(R.id.loginFragment)
        }


        binding!!.imagesBackSignUp.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.rlSignUp.setOnClickListener{
            if (validate()) {
                val bundle = Bundle()
                bundle.putString("screenType", "signup")
                bundle.putString("chooseType", chooseType)
                bundle.putString("value", binding!!.etSignUpEmailPhone.text.toString().trim())
                findNavController().navigate(R.id.verificationFragment,bundle)
            }
        }
    }

    private fun validate(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"
        val emaPattern = Pattern.compile(emailPattern)
        val emailMatcher = emaPattern.matcher(binding!!.etSignUpEmailPhone.text.toString().trim())
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{6,}\$"
        chooseType="email"
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
        chooseType="phone"
        return onlyDigits
    }
}
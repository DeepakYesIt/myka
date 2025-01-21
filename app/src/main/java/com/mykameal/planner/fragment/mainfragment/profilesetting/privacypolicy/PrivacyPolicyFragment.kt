package com.mykameal.planner.fragment.mainfragment.profilesetting.privacypolicy

import android.os.Build
import android.os.Bundle
import android.text.Html
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
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterTermsCondition
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentPrivacyPolicyBinding
import com.mykameal.planner.fragment.mainfragment.profilesetting.privacypolicy.viewmodel.PrivacyPolicyViewModel
import com.mykameal.planner.fragment.mainfragment.profilesetting.terms_condition.model.TermsConditionModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivacyPolicyFragment : Fragment() {

    private var binding:FragmentPrivacyPolicyBinding?=null
    private var adapterPrivacyPolicy: AdapterTermsCondition? = null
    private lateinit var privacyPolicyViewModel: PrivacyPolicyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPrivacyPolicyBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        privacyPolicyViewModel = ViewModelProvider(this)[PrivacyPolicyViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()

            }
        })

        binding!!.imgBackPrivacyPolicy.setOnClickListener{
            findNavController().navigateUp()
        }

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        if (BaseApplication.isOnline(requireActivity())) {
            privacyPolicyApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    /// Privacy policy api
    private fun privacyPolicyApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            privacyPolicyViewModel.getPrivacyPolicy {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val termConditionModel = gson.fromJson(it.data, TermsConditionModel::class.java)
                            if (termConditionModel.code == 200 && termConditionModel.success) {
                                val termsText = termConditionModel.data.description
                                binding!!.descText.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(termsText, Html.FROM_HTML_MODE_LEGACY)
                                } else {
                                    Html.fromHtml(termsText)
                                }
                            } else {
                                if (termConditionModel.code == ErrorMessage.code) {
                                    showAlertFunction(termConditionModel.message, true)
                                } else {
                                    showAlertFunction(termConditionModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("PrivacyPolicy","message:--"+e.message)
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }
        }
    }

    /// show error message
    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

}
package com.mykameal.planner.fragment.mainfragment.profilesetting.privacypolicy

import android.os.Build
import android.os.Bundle
import android.text.Html
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

//        privacyPolicyModel()

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

    /*   private fun privacyPolicyModel() {
           val dataList = ArrayList<DataModel>()
           val data1 = DataModel()
           val data2 = DataModel()
           val data3 = DataModel()

           data1.title = "1. Introduction"
           data1.description ="We take the security of your data seriously. We use industry-standard encryption and security measures to protect your information. However, no method of transmission over the internet or electronic storage is completely secure, and we cannot guarantee absolute security"
           data1.isOpen = false
           data1.type = "PrivacyPolicy"

           data2.title = ""
           data2.description="We take the security of your data seriously. We use industry-standard encryption and security measures to protect your information. However, no method of transmission over the internet or electronic storage is completely secure, and we cannot guarantee absolute security"
           data2.isOpen = false
           data2.type = "PrivacyPolicy"

           data3.title = ""
           data3.description="We take the security of your data seriously. We use industry-standard encryption and security measures to protect your information. However, no method of transmission over the internet or electronic storage is completely secure, and we cannot guarantee absolute security"
           data3.isOpen = false
           data3.type = "PrivacyPolicy"


           dataList.add(data1)
           dataList.add(data2)
           dataList.add(data3)

           adapterPrivacyPolicy = AdapterTermsCondition(dataList, requireActivity())
           binding!!.rcyPrivacyPolicy.adapter = adapterPrivacyPolicy

       }*/

}
package com.mykameal.planner.fragment.mainfragment.profilesetting.terms_condition

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterTermsCondition
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentTermsConditionBinding
import com.mykameal.planner.fragment.mainfragment.profilesetting.terms_condition.model.TermsConditionModel
import com.mykameal.planner.fragment.mainfragment.profilesetting.terms_condition.viewmodel.TermsConditionViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint

class TermsConditionFragment : Fragment() {

    private var binding: FragmentTermsConditionBinding? = null
    private var adapterTermsCondition: AdapterTermsCondition? = null
    private lateinit var termsConditionViewModel: TermsConditionViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTermsConditionBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        termsConditionViewModel = ViewModelProvider(this)[TermsConditionViewModel::class.java]


        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()

                }
            })

        binding!!.imgBackTermsAndCondition.setOnClickListener {
            findNavController().navigateUp()
        }

        initialize()




        return binding!!.root
    }

    private fun initialize() {

        if (BaseApplication.isOnline(requireActivity())) {
            termsConditionApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

    }


    /// Terms Condition Api
    private fun termsConditionApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            termsConditionViewModel.getTermCondition {
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


}
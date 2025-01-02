package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterTermsCondition
import com.yesitlabs.mykaapp.databinding.FragmentPrivacyPolicyBinding
import com.yesitlabs.mykaapp.model.DataModel

class PrivacyPolicyFragment : Fragment() {

    private var binding:FragmentPrivacyPolicyBinding?=null
    private var adapterPrivacyPolicy: AdapterTermsCondition? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPrivacyPolicyBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()

            }
        })

        binding!!.imgBackPrivacyPolicy.setOnClickListener{
            findNavController().navigateUp()
        }

        privacyPolicyModel()

        return binding!!.root
    }

    private fun privacyPolicyModel() {
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

    }

}
package com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen

import android.annotation.SuppressLint
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
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentPaymentCreditDebitBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen.viewmodel.PaymentCreditDebitViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentCreditDebitFragment : Fragment() {

    private lateinit var binding: FragmentPaymentCreditDebitBinding
    private var checkUnchecked:Boolean?=false

//    getOrderProductUrl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentCreditDebitBinding.inflate(layoutInflater, container, false)

        setupBackNavigation()
        initialize()

        return binding.root

    }

    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun initialize() {

        binding.imgCreditDebit.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.imgCheckUncheck.setOnClickListener{
            if (checkUnchecked == true){
                binding.imgCheckUncheck.setImageResource(R.drawable.uncheck_box_images)
            }else{
                binding.imgCheckUncheck.setImageResource(R.drawable.tick_ckeckbox_images)
            }
        }

        binding.tvSaveCreditDebitCard.setOnClickListener{

        }
    }

}
package com.mykaimeal.planner.fragment.mainfragment.commonscreen.addtipscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentAddTipScreenBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.addtipscreen.model.OrderProductTrackModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.addtipscreen.model.Response
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.addtipscreen.viewmodel.AddTipScreenViewModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen.viewmodel.PaymentCreditDebitViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import kotlinx.coroutines.launch

class AddTipScreenFragment : Fragment() {

    private var binding: FragmentAddTipScreenBinding? = null
    private lateinit var addTipScreenViewModel: AddTipScreenViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTipScreenBinding.inflate(layoutInflater, container, false)

        addTipScreenViewModel =
            ViewModelProvider(requireActivity())[AddTipScreenViewModel::class.java]

        setupBackNavigation()

        initialize()

        return binding!!.root
    }

    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun initialize() {

        binding!!.relBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.linearNotNow.setOnClickListener {
            binding!!.linearNotNow.setBackgroundResource(R.drawable.outline_green_border_bg)
            binding!!.llSevenDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llNineDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llTwelveDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llFifteenDollar.setBackgroundResource(R.drawable.edittext_bg)
        }

        binding!!.llSevenDollar.setOnClickListener {
            binding!!.linearNotNow.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llSevenDollar.setBackgroundResource(R.drawable.outline_green_border_bg)
            binding!!.llNineDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llTwelveDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llFifteenDollar.setBackgroundResource(R.drawable.edittext_bg)
        }

        binding!!.llNineDollar.setOnClickListener {
            binding!!.linearNotNow.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llSevenDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llNineDollar.setBackgroundResource(R.drawable.outline_green_border_bg)
            binding!!.llTwelveDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llFifteenDollar.setBackgroundResource(R.drawable.edittext_bg)

        }

        binding!!.llTwelveDollar.setOnClickListener {
            binding!!.linearNotNow.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llSevenDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llNineDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llTwelveDollar.setBackgroundResource(R.drawable.outline_green_border_bg)
            binding!!.llFifteenDollar.setBackgroundResource(R.drawable.edittext_bg)
        }

        binding!!.llFifteenDollar.setOnClickListener {
            binding!!.linearNotNow.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llSevenDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llNineDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llTwelveDollar.setBackgroundResource(R.drawable.edittext_bg)
            binding!!.llFifteenDollar.setBackgroundResource(R.drawable.outline_green_border_bg)

        }

        binding!!.rlProceedAndPay.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())){
                paymentCreditDebitApi()
            }else{
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
//            findNavController().navigate(R.id.paymentCreditDebitFragment)
        }
    }

    private fun paymentCreditDebitApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            addTipScreenViewModel.getOrderProductUrl{
                BaseApplication.dismissMe()
                handleApiOrderResponse(it)
            }
        }
    }

    private fun handleApiOrderResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessOrderResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessOrderResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, OrderProductTrackModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                if (apiModel.response != null) {
                    showDataInUI(apiModel.response)
                }
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }
 
    private fun showDataInUI(response: Response) {

        Toast.makeText(requireContext(),"Payment successful", Toast.LENGTH_SHORT).show()

        if (response.tracking_link!=null){
            val bundle = Bundle().apply {
                putString("tracking",response.tracking_link)
            }
            findNavController().navigate(R.id.trackOrderScreenFragment,bundle)
        }

    }

}
package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.skydoves.powerspinner.PowerSpinnerView
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.MyWalletAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentWalletBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.WalletViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponsecard.CradApiResponse
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponsetransfer.TransferModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch



@AndroidEntryPoint
class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding

    private lateinit var viewModel: WalletViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[WalletViewModel::class.java]

        (activity as MainActivity).binding?.apply {
            llIndicator.visibility = View.VISIBLE
            llBottomNavigation.visibility = View.VISIBLE
        }

        setupBackNavigation()
        setupUI()

        // When screen load then api call
        fetchWalletLoad()


        return binding.root
    }

    private fun fetchWalletLoad() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchWalletData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchWalletData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.getWalletRequest { result ->
                BaseApplication.dismissMe()
                handleApiResponse(result)
            }
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> processSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun processSuccessResponse(response: String) {
        try {
            val apiModel = Gson().fromJson(response, TransferModel::class.java)
            Log.d("@@@ Response wallet ", "message :- $response")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data!=null){

                    binding.textCurrentBalance.text="$ "+apiModel.data.walletbalance


                    if (apiModel.data.name!=null){
                        binding.textWalletHolderName.text=apiModel.data.name
                    }


                    if (apiModel.data.date!=null){
                        binding.textOnDateMonthYear.text="On "+apiModel.data.date
                    }

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




    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun setupUI() {


        binding.imgWallet.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlWithdrawAmountButton.setOnClickListener {
            if (binding.textCurrentBalance.text.toString().equals("$ 0",true)){
                BaseApplication.alertError(requireContext(), ErrorMessage.amountNoError, false)
            }else{
                val bundle=Bundle()
                bundle.putString("amount",binding.textCurrentBalance.text.toString())
                findNavController().navigate(R.id.paymentMethodFragment,bundle)
            }
        }

    }

}

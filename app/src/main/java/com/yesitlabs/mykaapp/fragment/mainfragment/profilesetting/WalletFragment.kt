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
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Objects

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
//        setupAdapters()
        setupSpinners()





        return binding.root
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
            findNavController().navigate(R.id.paymentMethodFragment)
        }

        binding.textAddBank.setOnClickListener {
            toggleAddDetailsVisibility(true)
        }

        /*binding.textAddCardDebitCard.setOnClickListener {
            toggleAddDetailsVisibility(true)
        }*/

        binding.textBankAccountToggle.setOnClickListener {
            toggleBankAndCardView(true)
        }

        binding.textDebitCardToggle.setOnClickListener {
            toggleBankAndCardView(false)
        }
    }


    /*private fun setupAdapters() {
        val dataList = generateSampleData()
        adapterBank = MyWalletAdapter(requireActivity(), dataList)
        binding.rcvBankAccounts.adapter = adapterBank

        adapterCard = MyWalletAdapter(requireContext(), dataList)
        binding.rcvCardNumber.adapter = adapterCard
    }*/



    private fun setupSpinners() {
        setupSpinner(binding.spinnerSelectIDType, listOf("Driver license", "Passport"))
        setupSpinner(binding.spinnerSelectCountry, listOf("USA", "UK", "INDIA", "BRAZIL", "RUSSIA", "CHINA"))
        setupSpinner(binding.spinnerSelectState, listOf("UP", "MP", "HARYANA", "PUNJAB", "ODISHA"))
        setupSpinner(binding.spinnerSelectCity, listOf("NEW DELHI", "MUMBAI", "KANPUR", "NOIDA"))
       /* setupSpinner(binding.spinnermonth, listOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"))*/

       /* // Generate a list of years (e.g., 1900 to the current year)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1900..currentYear).toList().map { it.toString() }

        setupSpinner(binding.spinneryear, years)*/
        setupSpinner(binding.spinnerSelectOption, listOf("Bank account statement", "Voided cheque", "Bank letterhead"))

    }

    private fun setupSpinner(spinner: PowerSpinnerView, items: List<String>) {
        spinner.setItems(items)
        spinner.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) spinner.show() else spinner.dismiss()
        }
        spinner.setIsFocusable(true)
    }

    private fun toggleAddDetailsVisibility(showDetails: Boolean) {
        binding.cvBankAccount2.visibility = if (showDetails) View.GONE else View.VISIBLE
        binding.cvDebitCard3.visibility = if (showDetails) View.GONE else View.VISIBLE
        binding.llBankAccount.visibility = if (showDetails) View.GONE else View.VISIBLE
        binding.llSavedBankAccountDetails4.visibility = if (showDetails) View.VISIBLE else View.GONE
    }

    private fun toggleBankAndCardView(showBank: Boolean) {
        binding.textBankAccountToggle.setBackgroundResource(
            if (showBank) R.drawable.selected_green_toogle_bg else 0
        )
        binding.textDebitCardToggle.setBackgroundResource(
            if (!showBank) R.drawable.selected_green_toogle_bg else 0
        )
        binding.textBankAccountToggle.setTextColor(
            Color.parseColor(if (showBank) "#FFFFFF" else "#06C169")
        )
        binding.textDebitCardToggle.setTextColor(
            Color.parseColor(if (!showBank) "#FFFFFF" else "#06C169")
        )
        binding.cvBankAccount2.visibility = if (showBank) View.VISIBLE else View.GONE
        binding.cvDebitCard3.visibility = if (showBank) View.GONE else View.VISIBLE
    }


}

package com.mykaimeal.planner.fragment.commonfragmentscreen.spendingOnGroceries

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentSpendingOnGroceriesBinding
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.GrocereisExpenses
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykaimeal.planner.fragment.commonfragmentscreen.spendingOnGroceries.viewmodel.SpendingGroceriesViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpendingOnGroceriesFragment : Fragment() {

    private var binding: FragmentSpendingOnGroceriesBinding? = null
    private var isOpen:Boolean=true
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String=""
    private lateinit var spendingGroceriesViewModel: SpendingGroceriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpendingOnGroceriesBinding.inflate(inflater, container, false)

        spendingGroceriesViewModel = ViewModelProvider(this)[SpendingGroceriesViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.tvSpendGroceries.text="How much do you typically spend on groceries per week/month?"
            binding!!.progressBar9.max=10
            totalProgressValue=10
            updateProgress(8)
        } else {
            binding!!.tvSpendGroceries.text="How much do you normally spend on groceries each week or month?"
            binding!!.progressBar9.max=11
            totalProgressValue=11
            updateProgress(9)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateSpendingGroc.visibility=View.VISIBLE
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                spendingGroceriesApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateSpendingGroc.visibility=View.GONE

            if (spendingGroceriesViewModel.getGroceriesData()!=null){
                showDataInUi(spendingGroceriesViewModel.getGroceriesData()!!)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()
        return binding!!.root
    }

    private fun spendingGroceriesApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            spendingGroceriesViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                            if (bodyModel.code == 200 && bodyModel.success) {
                                showDataInUi(bodyModel.data.grocereisExpenses)
                            } else {
                                if (bodyModel.code == ErrorMessage.code) {
                                    showAlertFunction(bodyModel.message, true)
                                }else{
                                    showAlertFunction(bodyModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("SpendingGroceries@@@","message:--"+e.message)
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

    private fun showDataInUi(groceriesExercise: GrocereisExpenses) {
        try {
            if (groceriesExercise!=null){
                if (groceriesExercise.amount!=null){
                    binding!!.etSpendingAmount.setText(groceriesExercise.amount.toString())
                }

                if (groceriesExercise.duration!=null){
                    binding!!.tvChooseDuration.text = groceriesExercise.duration.toString()
                }

            }
        }catch (e:Exception){
            Log.d("SpendingGroceries","message:--"+e.message)
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar9.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imgBackSpendGroceries.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            if (status=="2"){
                val groceriesLocalData = GrocereisExpenses(
                    amount = "",
                    created_at = "",
                    deleted_at = null,
                    duration = "",
                    id = 0,         // Default or appropriate ID
                    updated_at = "",
                    user_id = 0  // Default or appropriate user ID
                )
                groceriesLocalData.amount=binding!!.etSpendingAmount.text.toString().trim()
                groceriesLocalData.duration=binding!!.tvChooseDuration.text.toString().trim().toLowerCase()
                spendingGroceriesViewModel.setGroceriesData(groceriesLocalData)

                sessionManagement.setSpendingAmount(binding!!.etSpendingAmount.text.toString().trim())
                sessionManagement.setSpendingDuration(binding!!.tvChooseDuration.text.toString().trim().toLowerCase())
                findNavController().navigate(R.id.eatingOutFragment)
            }
        }

        binding!!.etSpendingAmount.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })


        binding!!.rlSelectDuration.setOnClickListener{
            if (isOpen){
                isOpen=false
                val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectWeekMonthly.visibility=View.VISIBLE
            }else{
                isOpen=true
                val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectWeekMonthly.visibility=View.GONE
            }
        }

        binding!!.rlSelectWeek.setOnClickListener{
            binding!!.tvChooseDuration.text="Weekly"
            binding!!.relSelectWeekMonthly.visibility=View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
            isOpen=true
            searchable()
        }

        binding!!.rlSelectMonthly.setOnClickListener{
            binding!!.tvChooseDuration.text="Monthly"
            binding!!.relSelectWeekMonthly.visibility=View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
            isOpen=true
            searchable()
        }

        binding!!.rlUpdateSpendingGroc.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())) {
                updateSpendingGrocApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateSpendingGrocApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            spendingGroceriesViewModel.updateSpendingGroceriesApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val updateModel = gson.fromJson(it.data, UpdatePreferenceSuccessfully::class.java)
                        if (updateModel.code == 200 && updateModel.success) {
                            findNavController().navigateUp()
                        } else {
                            if (updateModel.code == ErrorMessage.code) {
                                showAlertFunction(updateModel.message, true)
                            }else{
                                showAlertFunction(updateModel.message, false)
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
            },binding!!.etSpendingAmount.text.toString().trim(),binding!!.tvChooseDuration.text.toString().trim())
        }
    }

    private fun searchable() {
        if (binding!!.etSpendingAmount.text.isNotEmpty()){
            if (binding!!.tvChooseDuration.text.isNotEmpty()){
                status="2"
                binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            }else{
                status="1"
                binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
            }
        }else{
            status="1"
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }

    }

    private fun stillSkipDialog() {
        val dialogStillSkip: Dialog = context?.let { Dialog(it) }!!
        dialogStillSkip.setContentView(R.layout.alert_dialog_still_skip)
        dialogStillSkip.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogCancelBtn = dialogStillSkip.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogSkipBtn = dialogStillSkip.findViewById<TextView>(R.id.tvDialogSkipBtn)
        dialogStillSkip.show()
        dialogStillSkip.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogStillSkip.dismiss()
        }

        tvDialogSkipBtn.setOnClickListener {
            sessionManagement.setSpendingAmount("")
            sessionManagement.setSpendingDuration("")
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.eatingOutFragment)
        }
    }

}
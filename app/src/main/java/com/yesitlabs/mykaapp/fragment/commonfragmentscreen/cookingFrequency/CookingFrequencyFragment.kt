package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.cookingFrequency

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.adapter.BodyGoalAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentCookingFrequencyBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.cookingFrequency.viewmodel.CookingFrequencyViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CookingFrequencyFragment : Fragment(),OnItemClickListener {

    private var binding: FragmentCookingFrequencyBinding? = null
    private var bodyGoalAdapter: BodyGoalAdapter? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String?=null
    private var cookingSelect: String? = null
    private lateinit var cookingFrequencyViewModel: CookingFrequencyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCookingFrequencyBinding.inflate(inflater, container, false)

        cookingFrequencyViewModel = ViewModelProvider(this)[CookingFrequencyViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.tvCookFreqDesc.text="How often do you cook meals at home?"
            binding!!.progressBar7.max=10
            totalProgressValue=10
            updateProgress(7)
        } else if (sessionManagement.getCookingFor().equals("MyPartner")){
            binding!!.tvCookFreqDesc.text="How often do you cook meals at home?"
            binding!!.progressBar7.max=11
            totalProgressValue=11
            updateProgress(8)
        } else {
            binding!!.tvCookFreqDesc.text="How often do you cook meals for your family?"
            binding!!.progressBar7.max=11
            totalProgressValue=11
            updateProgress(8)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateCookingFrequency.visibility=View.VISIBLE
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateCookingFrequency.visibility=View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })


        if (sessionManagement.getCookingScreen()!="Profile"){
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireActivity())) {
                cookingFrequencyApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            if (BaseApplication.isOnline(requireActivity())) {
                cookingFrequencySelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        initialize()

        return binding!!.root
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar7.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imgBackCookingFreq.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            if (status=="2"){
                sessionManagement.setCookingFrequency(cookingSelect.toString())
                if (sessionManagement.getCookingFor().equals("Myself")){
                    findNavController().navigate(R.id.spendingOnGroceriesFragment)
//                    findNavController().navigate(R.id.cookingScheduleFragment)
                } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                    findNavController().navigate(R.id.spendingOnGroceriesFragment)
                } else {
                    findNavController().navigate(R.id.spendingOnGroceriesFragment)
//                    findNavController().navigate(R.id.cookingScheduleFragment)
                }
            }
        }

        binding!!.rlUpdateCookingFrequency.setOnClickListener{
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireActivity())) {
                updateCookFrequencyApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateCookFrequencyApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            cookingFrequencyViewModel.updateCookingFrequencyApi({
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
            }, cookingSelect.toString())
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
            dialogStillSkip.dismiss()
            sessionManagement.setCookingFrequency("")
            if (sessionManagement.getCookingFor().equals("Myself")){
                findNavController().navigate(R.id.cookingScheduleFragment)
            } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                findNavController().navigate(R.id.spendingOnGroceriesFragment)
            } else {
                findNavController().navigate(R.id.cookingScheduleFragment)
            }
        }
    }

    private fun cookingFrequencySelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            cookingFrequencyViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.cookingfrequency)
                        } else {
                            if (bodyModel.code == ErrorMessage.code) {
                                showAlertFunction(bodyModel.message, true)
                            }else{
                                showAlertFunction(bodyModel.message, false)
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

    private fun cookingFrequencyApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            cookingFrequencyViewModel.getCookingFrequency {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyGoalModel = gson.fromJson(it.data, BodyGoalModel::class.java)
                        if (bodyGoalModel.code == 200 && bodyGoalModel.success) {
                            showDataInUi(bodyGoalModel.data)
                        } else {
                            if (bodyGoalModel.code == ErrorMessage.code) {
                                showAlertFunction(bodyGoalModel.message, true)
                            }else{
                                showAlertFunction(bodyGoalModel.message, false)
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

    private fun showDataInUi(bogyGoalModelData: List<BodyGoalModelData>) {

        if (bogyGoalModelData!=null && bogyGoalModelData.size>0){
            bodyGoalAdapter = BodyGoalAdapter(bogyGoalModelData, requireActivity(), this)
            binding!!.rcyCookingFreq.adapter = bodyGoalAdapter
        }

    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    override fun itemClick(selectItem: Int?, status1: String?, type: String?) {
        if (status1 == "1") {
            status=""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            status="2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            cookingSelect=selectItem.toString()

        }
    }

}
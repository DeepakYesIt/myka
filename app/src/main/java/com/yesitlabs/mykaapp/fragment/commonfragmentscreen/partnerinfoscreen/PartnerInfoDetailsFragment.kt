package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.partnerinfoscreen

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.CookingForMyselfActivity
import com.yesitlabs.mykaapp.adapter.MealRoutineAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.databinding.FragmentPartnerInfoDetailsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.commonModel.PartnerDetail
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.partnerinfoscreen.viewmodel.PartnerInfoViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartnerInfoDetailsFragment : Fragment() {

    private var binding: FragmentPartnerInfoDetailsBinding? = null
    private var statusCheck: Boolean = true
    private var status: String = ""
    private lateinit var sessionManagement: SessionManagement
    private lateinit var partnerInfoViewModel: PartnerInfoViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPartnerInfoDetailsBinding.inflate(inflater, container, false)

        sessionManagement = SessionManagement(requireContext())

        partnerInfoViewModel = ViewModelProvider(this)[PartnerInfoViewModel::class.java]

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdatePartInfo.visibility=View.VISIBLE
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdatePartInfo.visibility=View.GONE
        }

        if (sessionManagement.getCookingScreen()=="Profile"){
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                partnerInfoApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (sessionManagement.getCookingScreen()=="Profile"){
                        findNavController().navigateUp()
                    }else{
                        val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
                        startActivity(intent)
                    }
                }
            })

        initialize()
        return binding!!.root
    }

    private fun partnerInfoApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            partnerInfoViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.partnerDetail)
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

    private fun showDataInUi(partnerModelData: PartnerDetail) {

        if (partnerModelData!=null){

            if (partnerModelData.name!=null){
                binding!!.etPartnerName.setText(partnerModelData.name.toString())
            }

            if (partnerModelData.age!=null){
                binding!!.etPartnerAge.setText(partnerModelData.age.toString())
            }

            if (partnerModelData.gender!=null){
                binding!!.tvChooseGender.text=partnerModelData.gender.toString()
            }

        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun initialize() {

        binding!!.etPartnerName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.etPartnerAge.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.imgBackPartnerInfo.setOnClickListener {
            if (sessionManagement.getCookingScreen()=="Profile"){
                findNavController().navigateUp()
            }else{
                val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
                startActivity(intent)
            }
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            if (status=="2"){
                sessionManagement.setPartnerName(binding!!.etPartnerName.text.toString().trim())
                sessionManagement.setPartnerAge(binding!!.etPartnerAge.text.toString().trim())
                sessionManagement.setPartnerGender(binding!!.tvChooseGender.text.toString().trim())
                findNavController().navigate(R.id.bodyGoalsFragment)
            }
        }

        binding!!.rlSelectGender.setOnClickListener {
            if (statusCheck) {
                statusCheck = false
                val drawableEnd =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(
                    0,
                    0,
                    drawableEnd.intrinsicWidth,
                    drawableEnd.intrinsicHeight
                )
                binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility = View.VISIBLE
            } else {
                statusCheck = true
                val drawableEnd =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(
                    0,
                    0,
                    drawableEnd.intrinsicWidth,
                    drawableEnd.intrinsicHeight
                )
                binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility = View.GONE
            }
        }

        binding!!.rlSelectMale.setOnClickListener {
            binding!!.tvChooseGender.text = "Male"
            binding!!.relSelectedGender.visibility = View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
            statusCheck = true
            searchable()
        }

        binding!!.rlSelectFemale.setOnClickListener {
            binding!!.tvChooseGender.text = "Female"
            binding!!.relSelectedGender.visibility = View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
            statusCheck = true
            searchable()
        }

        binding!!.rlUpdatePartInfo.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())) {
                updatePartnerInfoApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updatePartnerInfoApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            partnerInfoViewModel.updatePartnerInfoApi({
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
            }, binding!!.etPartnerName.text.toString().trim(),binding!!.etPartnerAge.text.toString().trim(),binding!!.tvChooseGender.text.toString().trim())
        }
    }

    private fun searchable() {
        if (binding!!.etPartnerName.text.isNotEmpty()) {
            if (binding!!.etPartnerAge.text.isNotEmpty()) {
                if (binding!!.tvChooseGender.text.toString().isNotEmpty()) {
                    status = "2"
                    binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
                } else {
                    status = "1"
                    binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
                }
            } else {
                status = "1"
                binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
            }
        } else {
            status = "1"
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
            sessionManagement.setPartnerName("")
            sessionManagement.setPartnerAge("")
            sessionManagement.setPartnerGender("")
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.bodyGoalsFragment)
        }
    }



}
package com.mykameal.planner.fragment.commonfragmentscreen.familyinfoscreen

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.R
import com.mykameal.planner.activity.CookingForScreenActivity
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentFamilyMembersBinding
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.FamilyDetail
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykameal.planner.fragment.commonfragmentscreen.familyinfoscreen.viewmodel.FamilyMemberInfoViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FamilyMembersFragment : Fragment() {
    private var binding: FragmentFamilyMembersBinding? = null
    private var isChecked: Boolean? = null
    private var status: String? = ""
    private var childFriendlyStatus: String? = ""
    private lateinit var sessionManagement: SessionManagement
    private lateinit var familyMemberInfoViewModel: FamilyMemberInfoViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentFamilyMembersBinding.inflate(inflater, container, false)
        sessionManagement = SessionManagement(requireActivity())

        familyMemberInfoViewModel = ViewModelProvider(this)[FamilyMemberInfoViewModel::class.java]

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateFamMem.visibility=View.VISIBLE
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                familyMemApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateFamMem.visibility=View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (sessionManagement.getCookingScreen()=="Profile"){
                        findNavController().navigateUp()
                    }else{
                       /* val intent = Intent(requireActivity(), CookingForScreenActivity::class.java)
                        startActivity(intent)*/
                        requireActivity().finish()
                    }

                }
            })

        initialize()

        return binding!!.root
    }

    private fun familyMemApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            familyMemberInfoViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.familyDetail)
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

    private fun showDataInUi(familyModelData: FamilyDetail) {

        if (familyModelData!=null){

            if (familyModelData.name!=null){
                binding!!.etMembersName.setText(familyModelData.name.toString())
            }

            if (familyModelData.age!=null){
                binding!!.etMemberAge.setText(familyModelData.age.toString())
            }

            if (familyModelData.status!=null){
                if (familyModelData.status=="1"){
                    childFriendlyStatus = "1"
                    binding!!.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                }else{
                    childFriendlyStatus = "0"
                    binding!!.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                }
            }

        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun initialize() {

        binding!!.etMembersName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.etMemberAge.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.imgBackFamilyMember.setOnClickListener {
            if (sessionManagement.getCookingScreen()=="Profile"){
                findNavController().navigateUp()
            }else{
                /*val intent = Intent(requireActivity(), CookingForScreenActivity::class.java)
                startActivity(intent)*/
                requireActivity().finish()
            }
        }

        binding!!.checkBoxImages.setOnClickListener {
            if (isChecked == true) {
                childFriendlyStatus = "1"
                binding!!.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                isChecked = false
            } else {
                childFriendlyStatus = "0"
                binding!!.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                isChecked = true
            }
            searchable()
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            if (status == "2") {
                sessionManagement.setFamilyMemName(binding!!.etMembersName.text.toString().trim())
                sessionManagement.setFamilyMemAge(binding!!.etMemberAge.text.toString().trim())
                sessionManagement.setFamilyStatus(childFriendlyStatus.toString())
                findNavController().navigate(R.id.bodyGoalsFragment)
            }
        }

        binding!!.rlUpdateFamMem.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())) {
                updateFamilyMemInfoApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateFamilyMemInfoApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            familyMemberInfoViewModel.updatePartnerInfoApi({
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
            }, binding!!.etMembersName.text.toString().trim(),binding!!.etMemberAge.text.toString().trim(),childFriendlyStatus)
        }
    }

    private fun searchable() {
        if (binding!!.etMembersName.text.isNotEmpty()) {
            if (binding!!.etMemberAge.text.isNotEmpty()) {
                if (childFriendlyStatus == "1") {
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
            sessionManagement.setFamilyMemName("")
            sessionManagement.setFamilyMemAge("")
            sessionManagement.setFamilyStatus("")
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.bodyGoalsFragment)
        }
    }

}
package com.mykameal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mykameal.planner.R
import com.mykameal.planner.activity.AuthActivity
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.apiInterface.BaseUrl
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.commonworkutils.CommonWorkUtils
import com.mykameal.planner.databinding.FragmentSettingProfileBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.SettingViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.Data
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.ProfileRootResponse
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingProfileFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentSettingProfileBinding? = null
    private var isMenuOpened: Boolean = false
    private var isAboutAppExpanded: Boolean = false
    private var isPostalCodeExpanded: Boolean = false
    private lateinit var commonWorkUtils: CommonWorkUtils
    private lateinit var sessionManagement: SessionManagement
    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentSettingProfileBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[SettingViewModel::class.java]

        setupUIVisibility()

        setupBackPressedCallback()

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireContext())

        setupClickListeners()

        // When screen load then api call
        fetchDataOnLoad()

        return binding!!.root

    }

    private fun fetchDataOnLoad() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchUserProfileData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }
    private fun  userDeleteData(dialog: Dialog) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.userDeleteData { result ->
                BaseApplication.dismissMe()
                handleApiLogOutResponse(result,dialog)
            }
        }
    }


    private fun  userLogOutData(dialog: Dialog) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.userLogOutData { result ->
                BaseApplication.dismissMe()
                handleApiLogOutResponse(result,dialog)
            }
        }
    }

    private fun fetchUserProfileData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.userProfileData { result ->
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

    private fun handleApiLogOutResponse(result: NetworkResult<String>, dialog: Dialog) {
        when (result) {
            is NetworkResult.Success -> processSuccessLogOutResponse(result.data.toString(),dialog)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun processSuccessResponse(response: String) {
        try {
            val apiModel = Gson().fromJson(response, ProfileRootResponse::class.java)
            Log.d("@@@ Response profile", "message :- $response")
            if (apiModel.code == 200 && apiModel.success) {
                viewModel.setProfileData(apiModel.data)
                updateUI(apiModel.data)
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

    private fun processSuccessLogOutResponse(response: String, dialog: Dialog) {
        try {
            val apiModel = Gson().fromJson(response, ProfileRootResponse::class.java)
            Log.d("@@@ Response profile", "message :- $response")
            if (apiModel.code == 200 && apiModel.success) {
                dialog.dismiss()
                sessionManagement.sessionClear()
                startActivity(Intent(requireActivity(), AuthActivity::class.java).apply {
                    putExtra("type", "login")
                })
                requireActivity().finish()
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


    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {

        binding?.tvBio?.isEnabled=false

        if (data.name!=null){
            binding?.tvUserName?.text = data.name
        }

        if (data.bio!=null){
            if (!data.bio.equals("null")){
                binding?.tvBio?.visibility=View.VISIBLE
                binding?.tvBio?.setText(data.bio)
            }else{
                binding?.tvBio?.visibility=View.INVISIBLE
            }
        }else{
            binding?.tvBio?.visibility=View.INVISIBLE
        }

        if (data.profile_img!=null){
            Glide.with(this)
                .load(BaseUrl.imageBaseUrl+data.profile_img)
                .placeholder(R.drawable.image_not)
                .error(R.drawable.image_not)
                .into(binding?.imageProfile!!)
        }

        if ((data.calories ?: 0) == 0 ) {
            binding?.tvCalories?.text=""+0
        } else {
            binding?.tvCalories?.text=""+data.calories!!.toInt()
        }

        if ( (data.carbs ?: 0) == 0) {
            binding?.tvCarbs?.text=""+0
        } else {
            binding?.tvCarbs?.text=""+data.carbs!!.toInt()
        }

        if ((data.fat ?: 0) == 0) {
            binding?.tvFat?.text=""+0
        } else {
            binding?.tvFat?.text=""+data.fat!!.toInt()
        }

        if ( (data.protien ?: 0) == 0) {
            binding?.tvProtein?.text=""+0
        } else {
            binding?.tvProtein?.text=""+data.protien!!.toInt()
        }


    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun setupUIVisibility() {
        (activity as MainActivity?)?.apply {
            binding?.llIndicator?.visibility = View.GONE
            binding?.llBottomNavigation?.visibility = View.GONE
        }
    }

    private fun setupBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.clearData()
                    findNavController().navigateUp()
                }
            }
        )
    }

    private fun setupClickListeners() {
        binding?.apply {
            arrayOf(
                imageEditProfile, relMyWallet, relHealthData, relPreferences, imageNameEditable,
                imageEditTargets, imageProfile, imgBackProfileSetting, imgThreeDotIcon,relNotifications, relPrivacyTerms, relAboutApp,
                relPostalCode, rlSubmitBtn, relFeedbackSupport, relPrivacyPolicy, relSubscriptionPlan
            ).forEach { it.setOnClickListener(this@SettingProfileFragment) }
        }
    }

    override fun onClick(item: View) {
        when (item.id) {
            R.id.imgBackProfileSetting -> backButton()
            R.id.imgThreeDotIcon -> toggleMenuVisibility()
            R.id.relAboutApp -> toggleAboutAppVisibility()
            R.id.relPostalCode -> togglePostalCodeVisibility()
            R.id.rlSubmitBtn -> handlePostalCodeSubmit()
            R.id.imageEditProfile -> enableProfileEditing()
            R.id.imageNameEditable -> disableProfileEditing()
            R.id.imageEditTargets -> moveToNextScreen()
            R.id.relMyWallet -> navigateToFragment(R.id.walletFragment)
            R.id.relHealthData -> navigateToFragment(R.id.healthDataFragment)
            R.id.relFeedbackSupport -> navigateToFragment(R.id.feedbackFragment)
            R.id.imageProfile -> navigateToFragment(R.id.editProfileFragment)
            R.id.relPreferences -> navigateToFragment(R.id.preferencesFragment)
            R.id.relNotifications -> navigateToFragment(R.id.notificationFragment)
            R.id.relPrivacyTerms -> navigateToFragment(R.id.termsConditionFragment)
            R.id.relPrivacyPolicy -> navigateToFragment(R.id.privacyPolicyFragment)
            R.id.relSubscriptionPlan -> navigateToFragment(R.id.subscriptionPlanOverViewFragment)
        }
    }

    private fun backButton() {
        viewModel.clearData()
        findNavController().navigateUp()
    }

    private fun moveToNextScreen() {
        binding?.relMacroNutTrg?.setBackgroundResource(R.drawable.profile_editable_bg)
        navigateToFragment(R.id.healthDataFragment)
    }

    private fun toggleMenuVisibility() {
        val inflater = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val popupView: View? = inflater?.inflate(R.layout.item_profile, null)
        val popupWindow = PopupWindow(popupView, 500, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAsDropDown(binding?.imgThreeDotIcon,  0, 0, Gravity.END)
        // Access views inside the inflated layout using findViewById
        val relLogout = popupView?.findViewById<RelativeLayout>(R.id.relLogout)
        val relDeleteAccount = popupView?.findViewById<RelativeLayout>(R.id.relDeleteAccount)

        relLogout?.setOnClickListener {
            popupWindow.dismiss()
            showLogoutDialog()
        }

        relDeleteAccount?.setOnClickListener {
            popupWindow.dismiss()
            showRemoveAccountDialog()
        }

    }

    private fun toggleAboutAppVisibility() {
        isAboutAppExpanded = !isAboutAppExpanded
        binding?.apply {
            imgDropAboutApp.setImageResource(
                if (isAboutAppExpanded) R.drawable.drop_up_small_icon else R.drawable.drop_down_small_icon
            )
            relPrivacyPolicy.visibility = if (isAboutAppExpanded) View.VISIBLE else View.GONE
            relPrivacyTerms.visibility = if (isAboutAppExpanded) View.VISIBLE else View.GONE
        }
    }

    private fun togglePostalCodeVisibility() {
        isPostalCodeExpanded = !isPostalCodeExpanded
        binding?.apply {
            imgDropPostCode.setImageResource(
                if (isPostalCodeExpanded) R.drawable.drop_up_small_icon else R.drawable.drop_down_small_icon
            )
            relEnterCode.visibility = if (isPostalCodeExpanded) View.VISIBLE else View.GONE
        }
    }

    private fun handlePostalCodeSubmit() {
        binding?.etEnterCode?.text?.toString()?.trim().takeIf { it.isNullOrEmpty() }?.let {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.postCode, false)
        } ?: closePostalCodeSection()
    }

    private fun closePostalCodeSection() {
        binding?.apply {
            imgDropPostCode.setImageResource(R.drawable.drop_up_small_icon)
            relEnterCode.visibility = View.GONE
        }
        isPostalCodeExpanded = false
    }

    private fun enableProfileEditing() {
        binding?.tvBio?.isEnabled = true
        binding?.tvBio?.visibility = View.VISIBLE
        binding?.imageEditTargets?.visibility = View.VISIBLE
        binding?.imageProfile?.isClickable = true
        binding?.imageNameEditable?.visibility = View.VISIBLE
        binding?.relProfileNameImage?.setBackgroundResource(R.drawable.profile_editable_bg)
    }

    private fun disableProfileEditing() {
        if (BaseApplication.isOnline(requireActivity())) {
            if (binding?.tvBio?.text.toString().trim().isEmpty()){
                BaseApplication.alertError(requireContext(), ErrorMessage.bioError, false)
            }else{
                upDateProfile()
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun upDateProfile() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.upDateProfileRequest({
                BaseApplication.dismissMe()
                handleApiUpdateResponse(it)
            }, viewModel.getProfileData()?.name.toString()
                ,binding?.tvBio?.text.toString()
                ,viewModel.getProfileData()?.gender.toString()
                ,viewModel.getProfileData()?.dob.toString()
                ,viewModel.getProfileData()?.height.toString(),
                viewModel.getProfileData()?.height_type.toString(),
                viewModel.getProfileData()?.activity_level.toString(),
                viewModel.getProfileData()?.height_protein.toString()
                ,viewModel.getProfileData()?.calories.toString(),
                viewModel.getProfileData()?.fat.toString()
                ,viewModel.getProfileData()?.carbs.toString(),
                viewModel.getProfileData()?.protien.toString()
            )
        }
    }


    private fun handleApiUpdateResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleUpdateSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleUpdateSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, ProfileRootResponse::class.java)
            Log.d("@@@ Health profile", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                binding?.apply {
                    tvBio.visibility = View.VISIBLE
                    tvBio.isEnabled = false
                    imageNameEditable.visibility = View.GONE
                    imageEditTargets.visibility = View.GONE
                    relProfileNameImage.setBackgroundResource(R.drawable.calendar_events_bg)
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



    private fun navigateToFragment(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    private fun showLogoutDialog() {
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.alert_dialog_logout_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.tvDialogCancelBtn).setOnClickListener { dialog.dismiss() }

        dialog.findViewById<TextView>(R.id.tvDialogLogoutBtn)?.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                userLogOutData(dialog)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
        dialog.show()

    }

    private fun showRemoveAccountDialog() {
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.alert_dialog_delete_account)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.tvDialogCancelBtn).setOnClickListener { dialog.dismiss() }

        dialog.findViewById<TextView>(R.id.tvDialogRemoveBtn)?.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                userDeleteData(dialog)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
        dialog.show()
    }

}

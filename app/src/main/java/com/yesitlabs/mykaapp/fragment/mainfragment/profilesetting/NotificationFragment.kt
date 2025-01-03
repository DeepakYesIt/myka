package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

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
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentNotificationBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.notificationviewmodel.NotificationViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.settingviewmodel.SettingViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.ProfileRootResponse
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import kotlinx.coroutines.launch

class NotificationFragment : Fragment(),View.OnClickListener {

    private var binding:FragmentNotificationBinding?=null
    private var onAllNotification:Boolean?=false
    private var pushNotification:Boolean?=false
    private var recipeRecommendation:Boolean?=false
    private var productUpdates:Boolean?=false
    private var promotionalUpdates:Boolean?=false
    private lateinit var viewModel: NotificationViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBinding.inflate(layoutInflater,container,false)


        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE


        viewModel = ViewModelProvider(requireActivity())[NotificationViewModel::class.java]


        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        binding!!.imgBackNotification.setOnClickListener(this)
        binding!!.textEnableNotification.setOnClickListener(this)
        binding!!.textPushNotification.setOnClickListener(this)
        binding!!.textRecipeRecommendations.setOnClickListener(this)
        binding!!.textProductUpdates.setOnClickListener(this)
        binding!!.textPromotionalUpdates.setOnClickListener(this)


        // When screen load then api call
        fetchNotificationList()

        return binding!!.root
    }

    private fun fetchNotificationList() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchNotificationData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchNotificationData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            lifecycleScope.launch {
                viewModel.notificationRequest({
                    BaseApplication.dismissMe()
                    handleApiResponse(it)
                }, "","","",""
                )
            }
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, ProfileRootResponse::class.java)
            Log.d("@@@ Health profile", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                
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

    override fun onClick(item: View?) {
        when (item!!.id) {

            R.id.imgBackNotification->{
                findNavController().navigateUp()
            }

            R.id.textEnableNotification->{
                if (onAllNotification==true){
                    onAllNotification=false
                    binding!!.textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                    pushNotification=false
                    binding!!.textPushNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                    recipeRecommendation=false
                    binding!!.textRecipeRecommendations.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                    productUpdates=false
                    binding!!.textProductUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                    promotionalUpdates=false
                    binding!!.textPromotionalUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                }else{
                    onAllNotification=true
                    binding!!.textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    pushNotification=true
                    binding!!.textPushNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    recipeRecommendation=true
                    binding!!.textRecipeRecommendations.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    productUpdates=true
                    binding!!.textProductUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    promotionalUpdates=true
                    binding!!.textPromotionalUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                }
            }

            R.id.textPushNotification->{
                if (pushNotification==true){
                    pushNotification=false
                    binding!!.textPushNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                }else{
                    pushNotification=true
                    binding!!.textPushNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    onAllNotification=true
                    binding!!.textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                }
            }

            R.id.textRecipeRecommendations->{
                if (recipeRecommendation==true){
                    recipeRecommendation=false
                    binding!!.textRecipeRecommendations.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                }else{
                    recipeRecommendation=true
                    binding!!.textRecipeRecommendations.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    onAllNotification=true
                    binding!!.textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                }
            }

            R.id.textProductUpdates->{
                if (productUpdates==true){
                    productUpdates=false
                    binding!!.textProductUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                }else{
                    productUpdates=true
                    binding!!.textProductUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    onAllNotification=true
                    binding!!.textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                }
            }

            R.id.textPromotionalUpdates->{
                if (promotionalUpdates==true){
                    promotionalUpdates=false
                    binding!!.textPromotionalUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_on_icon,0 )
                }else{
                    promotionalUpdates=true
                    binding!!.textPromotionalUpdates.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                    onAllNotification=true
                    binding!!.textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.toggle_gray_off_icon,0 )
                }
            }
        }
    }
}
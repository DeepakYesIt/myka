package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment(),View.OnClickListener {

    private var binding:FragmentNotificationBinding?=null
    private var onAllNotification:Boolean?=false
    private var pushNotification:Boolean?=false
    private var recipeRecommendation:Boolean?=false
    private var productUpdates:Boolean?=false
    private var promotionalUpdates:Boolean?=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBinding.inflate(layoutInflater,container,false)


        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

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

        return binding!!.root
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
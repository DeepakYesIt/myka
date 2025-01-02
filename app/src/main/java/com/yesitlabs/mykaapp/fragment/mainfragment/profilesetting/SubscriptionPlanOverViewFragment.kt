package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentSubscriptionPlanOverViewBinding

class SubscriptionPlanOverViewFragment : Fragment() {
    private var binding:FragmentSubscriptionPlanOverViewBinding?=null
    private lateinit var slideUp: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSubscriptionPlanOverViewBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.textSeeAllButton.setOnClickListener {
            binding!!.textSeeAllButton.visibility = View.GONE

            binding!!.rlBottom.visibility = View.VISIBLE

            slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_anim)
            slideUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding!!.llSubPlans.clearAnimation()
                    binding!!.llSubPlans.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding!!.llSubPlans.startAnimation(slideUp)
        }
    }

}
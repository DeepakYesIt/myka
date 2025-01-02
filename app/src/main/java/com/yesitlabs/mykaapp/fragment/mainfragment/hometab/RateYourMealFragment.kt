package com.yesitlabs.mykaapp.fragment.mainfragment.hometab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentRateYourMealBinding

class RateYourMealFragment : Fragment() {

    private var binding:FragmentRateYourMealBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentRateYourMealBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.directionSteps2RecipeDetailsFragmentFragment)
            }
        })

        binding!!.imgBackRateMeal.setOnClickListener{
            findNavController().navigate(R.id.directionSteps2RecipeDetailsFragmentFragment)
        }

        binding!!.relPublishReview.setOnClickListener{
            findNavController().navigate(R.id.homeFragment)
        }

        return binding!!.root
    }

}
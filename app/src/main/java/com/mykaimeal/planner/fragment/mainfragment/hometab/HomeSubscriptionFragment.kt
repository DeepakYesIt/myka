package com.mykaimeal.planner.fragment.mainfragment.hometab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.databinding.FragmentHomeSubscriptionBinding


class HomeSubscriptionFragment : Fragment() {

    private var binding: FragmentHomeSubscriptionBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeSubscriptionBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imageCross.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvStartFreeTrial.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSeeAllPlan.setOnClickListener{
            findNavController().navigate(R.id.homeSubscriptionAllPlanFragment)
        }
    }

}
package com.mykaimeal.planner.fragment.mainfragment.profilesetting.subscriptionplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.SubscriptionAdaptor
import com.mykaimeal.planner.databinding.FragmentHomeSubscriptionAllPlanBinding
import com.mykaimeal.planner.model.SubscriptionModel
import java.util.ArrayList

class SubscriptionAllPlanFragment : Fragment() {

    private var binding: FragmentHomeSubscriptionAllPlanBinding?=null
    var adapter: SubscriptionAdaptor? = null
    var datalist: ArrayList<SubscriptionModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeSubscriptionAllPlanBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        /*binding!!.imageCrossing.setOnClickListener{
            findNavController().navigateUp()
        }*/

        return binding!!.root
    }


}
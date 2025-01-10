package com.mykameal.planner.fragment.mainfragment.hometab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.databinding.FragmentCreateCookBookBinding

class CreateCookBookFragment : Fragment() {
    private var binding:FragmentCreateCookBookBinding?=null
    private var isOpened:Boolean?=null
    private var checkType:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentCreateCookBookBinding.inflate(layoutInflater, container, false)

        if (arguments!=null){
            checkType= requireArguments().getString("value","")
        }

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        if (checkType=="New"){
            binding!!.tvToolbar.text="Create Cookbook"
        }else{
            binding!!.tvToolbar.text="Edit Cookbook"

        }

        binding!!.imageBackIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.imageInfo.setOnClickListener{
            if (isOpened==true){
                isOpened=false
                binding!!.cvInfoMessage.visibility=View.GONE
            }else{
                isOpened=true
                binding!!.cvInfoMessage.visibility=View.GONE

            }
        }
    }

}
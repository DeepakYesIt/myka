package com.yesitlabs.mykaapp.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.FragmentTescoCartItemFragmentBinding

class TescoCartItemFragmentFragment : Fragment() {
    private lateinit var binding:FragmentTescoCartItemFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentTescoCartItemFragmentBinding.inflate(layoutInflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding.root
    }

    private fun initialize() {
        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}
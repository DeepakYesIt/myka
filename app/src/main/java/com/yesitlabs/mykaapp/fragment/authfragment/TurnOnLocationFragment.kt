package com.yesitlabs.mykaapp.fragment.authfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.FragmentTurnOnLocationBinding

class TurnOnLocationFragment : Fragment() {
    private var binding: FragmentTurnOnLocationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTurnOnLocationBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imgBackTurnLocation.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.rlTurnOnLocation.setOnClickListener{
            findNavController().navigate(R.id.turnOnNotificationsFragment)
        }

        binding!!.tvNotNow.setOnClickListener{
            findNavController().navigate(R.id.turnOnNotificationsFragment)

        }
    }

}
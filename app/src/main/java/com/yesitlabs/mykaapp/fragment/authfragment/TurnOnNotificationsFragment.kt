package com.yesitlabs.mykaapp.fragment.authfragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentTurnOnNotificationsBinding

class TurnOnNotificationsFragment : Fragment() {

    private var binding: FragmentTurnOnNotificationsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTurnOnNotificationsBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imgBackNotifications.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.rlTurnOnNotifications.setOnClickListener{
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        binding!!.tvNotNow.setOnClickListener{
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }
    }

}
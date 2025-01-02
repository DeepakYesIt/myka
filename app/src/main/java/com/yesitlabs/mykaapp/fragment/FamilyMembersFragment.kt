package com.yesitlabs.mykaapp.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.CookingForMyselfActivity
import com.yesitlabs.mykaapp.databinding.FragmentFamilyMembersBinding

class FamilyMembersFragment : Fragment() {
    private var binding: FragmentFamilyMembersBinding? = null
    private var isChecked:Boolean?=null
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFamilyMembersBinding.inflate(inflater, container, false)
        sessionManagement = SessionManagement(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
                startActivity(intent)
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imgBackFamilyMember.setOnClickListener {
            if (sessionManagement.getCookingFor().equals("Myself")) {
                val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
                startActivity(intent)
            }
        }

        binding!!.checkBoxImages.setOnClickListener{
            if (isChecked==true){
                binding!!.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                isChecked=false
            }else{
                binding!!.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                isChecked=true
            }
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            findNavController().navigate(R.id.bodyGoalsFragment)
        }
    }

    private fun stillSkipDialog() {
        val dialogStillSkip: Dialog = context?.let { Dialog(it) }!!
        dialogStillSkip.setContentView(R.layout.alert_dialog_still_skip)
        dialogStillSkip.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogCancelBtn = dialogStillSkip.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogSkipBtn = dialogStillSkip.findViewById<TextView>(R.id.tvDialogSkipBtn)
        dialogStillSkip.show()
        dialogStillSkip.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogStillSkip.dismiss()
        }

        tvDialogSkipBtn.setOnClickListener {
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.bodyGoalsFragment)
        }
    }

}
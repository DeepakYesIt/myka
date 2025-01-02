package com.yesitlabs.mykaapp.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var isChecked: Boolean? = null
    private var status: String? = ""
    private var childFriendlyStatus: String? = ""
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFamilyMembersBinding.inflate(inflater, container, false)
        sessionManagement = SessionManagement(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
                    startActivity(intent)
                }
            })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.etMembersName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.etMemberAge.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.imgBackFamilyMember.setOnClickListener {
            val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
            startActivity(intent)
        }

        binding!!.checkBoxImages.setOnClickListener {
            if (isChecked == true) {
                childFriendlyStatus = "1"
                binding!!.checkBoxImages.setImageResource(R.drawable.tick_ckeckbox_images)
                isChecked = false
            } else {
                childFriendlyStatus = "0"
                binding!!.checkBoxImages.setImageResource(R.drawable.uncheck_box_images)
                isChecked = true
            }
            searchable()
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            if (status == "2") {
                sessionManagement.setFamilyMemName(binding!!.etMembersName.text.toString().trim())
                sessionManagement.setFamilyMemAge(binding!!.etMemberAge.text.toString().trim())
                sessionManagement.setFamilyStatus(childFriendlyStatus.toString())
                findNavController().navigate(R.id.bodyGoalsFragment)
            }

        }
    }

    private fun searchable() {
        if (binding!!.etMembersName.text.isNotEmpty()) {
            if (binding!!.etMemberAge.text.isNotEmpty()) {
                if (childFriendlyStatus == "1") {
                    status = "2"
                    binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
                } else {
                    status = "1"
                    binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
                }
            } else {
                status = "1"
                binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
            }
        } else {
            status = "1"
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
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
            sessionManagement.setFamilyMemName("")
            sessionManagement.setFamilyMemAge("")
            sessionManagement.setFamilyStatus("")
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.bodyGoalsFragment)
        }
    }

}
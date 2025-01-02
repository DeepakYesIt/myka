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
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.CookingForMyselfActivity
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.databinding.FragmentPartnerInfoDetailsBinding
import com.yesitlabs.mykaapp.databinding.FragmentSpendingOnGroceriesBinding

class PartnerInfoDetailsFragment : Fragment() {

    private var binding: FragmentPartnerInfoDetailsBinding? = null
    private var statusCheck: Boolean = true
    private var status: String = ""
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPartnerInfoDetailsBinding.inflate(inflater, container, false)

        sessionManagement = SessionManagement(requireContext())

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

        binding!!.etPartnerName.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.etPartnerAge.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })

        binding!!.imgBackPartnerInfo.setOnClickListener {
            val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
            startActivity(intent)
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            if (status=="2"){
                sessionManagement.setPartnerName(binding!!.etPartnerName.text.toString().trim())
                sessionManagement.setPartnerAge(binding!!.etPartnerAge.text.toString().trim())
                sessionManagement.setPartnerGender(binding!!.tvChooseGender.text.toString().trim())
                findNavController().navigate(R.id.bodyGoalsFragment)
            }
        }

        binding!!.rlSelectGender.setOnClickListener {
            if (statusCheck) {
                statusCheck = false
                val drawableEnd =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(
                    0,
                    0,
                    drawableEnd.intrinsicWidth,
                    drawableEnd.intrinsicHeight
                )
                binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility = View.VISIBLE
            } else {
                statusCheck = true
                val drawableEnd =
                    ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(
                    0,
                    0,
                    drawableEnd.intrinsicWidth,
                    drawableEnd.intrinsicHeight
                )
                binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility = View.GONE
            }
        }

        binding!!.rlSelectMale.setOnClickListener {
            binding!!.tvChooseGender.text = "Male"
            binding!!.relSelectedGender.visibility = View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
            statusCheck = true
            searchable()
        }

        binding!!.rlSelectFemale.setOnClickListener {
            binding!!.tvChooseGender.text = "Female"
            binding!!.relSelectedGender.visibility = View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
            statusCheck = true
            searchable()
        }
    }

    private fun searchable() {
        if (binding!!.etPartnerName.text.isNotEmpty()) {
            if (binding!!.etPartnerAge.text.isNotEmpty()) {
                if (binding!!.tvChooseGender.text.toString().isNotEmpty()) {
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
            sessionManagement.setPartnerName("")
            sessionManagement.setPartnerAge("")
            sessionManagement.setPartnerGender("")
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.bodyGoalsFragment)
        }
    }



}
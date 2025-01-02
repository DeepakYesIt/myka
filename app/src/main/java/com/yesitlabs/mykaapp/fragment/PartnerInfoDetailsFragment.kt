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
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.CookingForMyselfActivity
import com.yesitlabs.mykaapp.databinding.FragmentPartnerInfoDetailsBinding
import com.yesitlabs.mykaapp.databinding.FragmentSpendingOnGroceriesBinding

class PartnerInfoDetailsFragment : Fragment() {

    private var binding: FragmentPartnerInfoDetailsBinding? = null
    private var status:Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPartnerInfoDetailsBinding.inflate(inflater, container, false)

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

        binding!!.imgBackPartnerInfo.setOnClickListener{
            val intent = Intent(requireActivity(), CookingForMyselfActivity::class.java)
            startActivity(intent)
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            findNavController().navigate(R.id.bodyGoalsFragment)
        }

        binding!!.rlSelectGender.setOnClickListener{
            if (status){
                status=false
                val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility=View.VISIBLE
            }else{
                status=true
                val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility=View.GONE
            }
        }

        binding!!.rlSelectMale.setOnClickListener{
            binding!!.tvChooseGender.text="Male"
            binding!!.relSelectedGender.visibility=View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
            status=true
        }

        binding!!.rlSelectFemale.setOnClickListener{
            binding!!.tvChooseGender.text="Female"
            binding!!.relSelectedGender.visibility=View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(null, null, drawableEnd, null)
            status=true
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
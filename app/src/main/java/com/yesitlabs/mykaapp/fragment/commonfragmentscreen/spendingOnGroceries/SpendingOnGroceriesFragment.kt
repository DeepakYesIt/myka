package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.spendingOnGroceries

import android.app.Dialog
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
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.FragmentSpendingOnGroceriesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpendingOnGroceriesFragment : Fragment() {

    private var binding: FragmentSpendingOnGroceriesBinding? = null
    private var isOpen:Boolean=true
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpendingOnGroceriesBinding.inflate(inflater, container, false)

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.tvSpendGroceries.text="How much do you typically spend on groceries per week/month?"
            binding!!.progressBar9.max=10
            totalProgressValue=10
            updateProgress(8)
        } else {
            binding!!.tvSpendGroceries.text="How much do you normally spend on groceries each week or month?"
            binding!!.progressBar9.max=11
            totalProgressValue=11
            updateProgress(9)
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()
        return binding!!.root
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar9.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imgBackSpendGroceries.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            if (status=="2"){
                sessionManagement.setSpendingAmount(binding!!.etSpendingAmount.text.toString().trim())
                sessionManagement.setSpendingDuration(binding!!.tvChooseDuration.text.toString().trim().toLowerCase())
                findNavController().navigate(R.id.eatingOutFragment)
            }
        }

        binding!!.etSpendingAmount.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable()
            }
        })


        binding!!.rlSelectDuration.setOnClickListener{
            if (isOpen){
                isOpen=false
                val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectWeekMonthly.visibility=View.VISIBLE
            }else{
                isOpen=true
                val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
                binding!!.relSelectWeekMonthly.visibility=View.GONE
            }
        }

        binding!!.rlSelectWeek.setOnClickListener{
            binding!!.tvChooseDuration.text="Weekly"
            binding!!.relSelectWeekMonthly.visibility=View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
            isOpen=true
            searchable()
        }

        binding!!.rlSelectMonthly.setOnClickListener{
            binding!!.tvChooseDuration.text="Monthly"
            binding!!.relSelectWeekMonthly.visibility=View.GONE
            val drawableEnd = ContextCompat.getDrawable(requireContext(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            binding!!.tvChooseDuration.setCompoundDrawables(null, null, drawableEnd, null)
            isOpen=true
            searchable()
        }
    }

    private fun searchable() {
        if (binding!!.etSpendingAmount.text.isNotEmpty()){
            if (binding!!.tvChooseDuration.text.isNotEmpty()){
                status="2"
                binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            }else{
                status="1"
                binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
            }
        }else{
            status="1"
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
            sessionManagement.setSpendingAmount("")
            sessionManagement.setSpendingDuration("")
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.eatingOutFragment)
        }
    }

}
package com.yesitlabs.mykaapp.fragment.mainfragment.addrecipetab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentCreateRecipeBinding

class CreateRecipeFragment : Fragment() {

    private var binding: FragmentCreateRecipeBinding? = null
    private var quantity:Int=1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateRecipeBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                addRecipeDiscardDialog()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.spinnerCookBook.setItems(
            listOf("Christmas", "Favourite","Birthday", "Party", "Coriander"))

        binding!!.imgMinus.setOnClickListener{
            if (quantity > 1) {
                quantity--
                updateValue()
            }else{
                Toast.makeText(requireActivity(),"Minimum serving atleast value is one", Toast.LENGTH_LONG).show()
            }
        }

        binding!!.imgPlus.setOnClickListener{
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        binding!!.relBacks.setOnClickListener{
            addRecipeDiscardDialog()
        }

        binding!!.layBottom.setOnClickListener{
            addRecipeSuccessDialog()
        }

        binding!!.textPrivate.setOnClickListener{
            val drawableStart = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_uncheck_gray_icon)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPublic.setCompoundDrawables(drawableStart, null, null, null)

            val drawableStart1 = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_check_icon)
            drawableStart1!!.setBounds(0, 0, drawableStart1.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPrivate.setCompoundDrawables(drawableStart1, null, null, null)
        }

        binding!!.textPublic.setOnClickListener{
            val drawableStart = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_check_icon)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPublic.setCompoundDrawables(drawableStart, null, null, null)

            val drawableStart1 = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_uncheck_gray_icon)
            drawableStart1!!.setBounds(0, 0, drawableStart1.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPrivate.setCompoundDrawables(drawableStart1, null, null, null)

        }
    }

    private fun updateValue() {
        binding!!.textValue.text = String.format("%02d", quantity)
    }

    private fun addRecipeDiscardDialog() {
        val dialogDiscard: Dialog = context?.let { Dialog(it) }!!
        dialogDiscard.setContentView(R.layout.alert_dialog_discard_recipe)
        dialogDiscard.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogDiscard.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogYesBtn = dialogDiscard.findViewById<TextView>(R.id.tvDialogYesBtn)
        val tvDialogNoBtn = dialogDiscard.findViewById<TextView>(R.id.tvDialogNoBtn)
        dialogDiscard.show()
        dialogDiscard.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogYesBtn.setOnClickListener {
            dialogDiscard.dismiss()
            findNavController().navigateUp()
        }

        tvDialogNoBtn.setOnClickListener{
            dialogDiscard.dismiss()
        }
    }

    private fun addRecipeSuccessDialog() {
        val dialogSuccess: Dialog = context?.let { Dialog(it) }!!
        dialogSuccess.setContentView(R.layout.alert_dialog_add_recipe_success)
        dialogSuccess.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogSuccess.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogSuccess.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogSuccess.show()
        dialogSuccess.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            dialogSuccess.dismiss()
            findNavController().navigate(R.id.planFragment)
        }

    }

}
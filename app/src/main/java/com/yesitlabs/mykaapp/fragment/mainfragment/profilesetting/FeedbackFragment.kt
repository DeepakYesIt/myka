package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.AuthActivity
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentFeedbackBinding
import org.w3c.dom.Text

class FeedbackFragment : Fragment() {

    private var binding:FragmentFeedbackBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentFeedbackBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                feedbackDiscardDialog()
            }
        })

        binding!!.imgBackFeedback.setOnClickListener{
            feedbackDiscardDialog()
        }

        binding!!.relFeedbackSubmit.setOnClickListener{
            feedbackSubmitDialog()
        }

        return binding!!.root
    }

    private fun feedbackSubmitDialog() {
        val dialogFeedbackSubmit: Dialog = context?.let { Dialog(it) }!!
        dialogFeedbackSubmit.setContentView(R.layout.alert_dialog_feedback_submit)
        dialogFeedbackSubmit.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogFeedbackSubmit.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        val imageCrossFeedback = dialogFeedbackSubmit.findViewById<ImageView>(R.id.imageCrossFeedback)
        dialogFeedbackSubmit.show()
        dialogFeedbackSubmit.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            binding!!.edtEmail.text.clear()
            binding!!.edtDesc.text.clear()
            dialogFeedbackSubmit.dismiss()
        }

        imageCrossFeedback.setOnClickListener {
            binding!!.edtEmail.text.clear()
            binding!!.edtDesc.text.clear()
            dialogFeedbackSubmit.dismiss()
        }
    }


    private fun feedbackDiscardDialog() {
        val dialogFeedbackDiscard: Dialog = context?.let { Dialog(it) }!!
        dialogFeedbackDiscard.setContentView(R.layout.alert_dialog_feedback_discard_changes)
        dialogFeedbackDiscard.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogCancelBtn = dialogFeedbackDiscard.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val imgCrossDiscardChanges = dialogFeedbackDiscard.findViewById<ImageView>(R.id.imgCrossDiscardChanges)
        val tvDialogRemoveBtn = dialogFeedbackDiscard.findViewById<TextView>(R.id.tvDialogRemoveBtn)
        dialogFeedbackDiscard.show()
        dialogFeedbackDiscard.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)


        tvDialogCancelBtn.setOnClickListener {
            dialogFeedbackDiscard.dismiss()
        }

        imgCrossDiscardChanges.setOnClickListener{
            dialogFeedbackDiscard.dismiss()
        }

        tvDialogRemoveBtn.setOnClickListener {
            binding!!.edtEmail.text.clear()
            binding!!.edtDesc.text.clear()
            dialogFeedbackDiscard.dismiss()
            findNavController().navigateUp()
        }
    }

}
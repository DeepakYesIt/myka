package com.mykameal.planner.fragment.mainfragment.profilesetting.feedbackscreen

import android.app.Dialog
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.databinding.FragmentFeedbackBinding
import com.mykameal.planner.fragment.mainfragment.profilesetting.feedbackscreen.model.FeedbackModel
import com.mykameal.planner.fragment.mainfragment.profilesetting.feedbackscreen.viewmodel.FeedbackViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedbackFragment : Fragment() {

    private var binding:FragmentFeedbackBinding?=null
    private lateinit var feedbackViewModel: FeedbackViewModel
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentFeedbackBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        feedbackViewModel = ViewModelProvider(this)[FeedbackViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding!!.edtDesc.text.toString().isNotEmpty()){
                    feedbackDiscardDialog()
                }else{
                    findNavController().navigateUp()
                }
            }
        })

        binding!!.imgBackFeedback.setOnClickListener{
            if (binding!!.edtDesc.text.toString().isNotEmpty()){
                feedbackDiscardDialog()
            }else{
                findNavController().navigateUp()
            }
        }

        binding!!.edtEmail.text=sessionManagement.getEmail().toString()

        binding!!.relFeedbackSubmit.setOnClickListener{
            if (BaseApplication.isOnline(requireActivity())) {
                saveFeedbackApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }

            /*feedbackSubmitDialog()*/
        }

        return binding!!.root
    }


    /// Save Feedback api
    private fun saveFeedbackApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            feedbackViewModel.saveFeedback({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val feedbackModel = gson.fromJson(it.data, FeedbackModel::class.java)
                        if (feedbackModel.code == 200 && feedbackModel.success) {
                            binding!!.edtDesc.text.clear()
                            feedbackSubmitDialog()
                        } else {
                            if (feedbackModel.code == ErrorMessage.code) {
                                showAlertFunction(feedbackModel.message, true)
                            } else {
                                showAlertFunction(feedbackModel.message, false)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            },sessionManagement.getEmail().toString(),binding!!.edtDesc.text.toString().trim())
        }
    }

    /// show error message
    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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
            dialogFeedbackSubmit.dismiss()
        }

        imageCrossFeedback.setOnClickListener {
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
            binding!!.edtDesc.text.clear()
            dialogFeedbackDiscard.dismiss()
            findNavController().navigateUp()
        }
    }

}
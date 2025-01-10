package com.mykameal.planner.fragment.commonfragmentscreen.reasonTakeAway

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.LetsStartOptionActivity
import com.mykameal.planner.adapter.BodyGoalAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentReasonsForTakeAwayBinding
import com.mykameal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.mykameal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykameal.planner.fragment.commonfragmentscreen.reasonTakeAway.viewmodel.ReasonTakeAwayViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReasonsForTakeAwayFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentReasonsForTakeAwayBinding? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue: Int = 0
    private var status: String? = ""
    private var reasonSelect: String? = ""
    private lateinit var reasonTakeAwayViewModel: ReasonTakeAwayViewModel
    private var bodyGoalAdapter: BodyGoalAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReasonsForTakeAwayBinding.inflate(inflater, container, false)

        reasonTakeAwayViewModel = ViewModelProvider(this)[ReasonTakeAwayViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")) {
            binding!!.progressBar11.max = 10
            totalProgressValue = 10
            updateProgress(10)
        } else {
            binding!!.progressBar11.max = 11
            totalProgressValue = 11
            updateProgress(11)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateReasonTakeAway.visibility=View.VISIBLE
            if (BaseApplication.isOnline(requireContext())) {
                reasonTakeAwaySelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateReasonTakeAway.visibility=View.GONE
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                reasonTakeAwayApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })


        initialize()

        return binding!!.root
    }

    private fun reasonTakeAwaySelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            reasonTakeAwayViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.takeawayreason)
                        } else {
                            if (bodyModel.code == ErrorMessage.code) {
                                showAlertFunction(bodyModel.message, true)
                            }else{
                                showAlertFunction(bodyModel.message, false)
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
            }
        }
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar11.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imbBackTakeAway.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            if (status == "2") {
                sessionManagement.setReasonTakeAway(reasonSelect.toString())
                val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        binding!!.rlUpdateReasonTakeAway.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())) {
                updateReasonTakeAwayApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateReasonTakeAwayApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            reasonTakeAwayViewModel.updateReasonTakeAwayApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val updateModel = gson.fromJson(it.data, UpdatePreferenceSuccessfully::class.java)
                        if (updateModel.code == 200 && updateModel.success) {
                            findNavController().navigateUp()
                        } else {
                            if (updateModel.code == ErrorMessage.code) {
                                showAlertFunction(updateModel.message, true)
                            }else{
                                showAlertFunction(updateModel.message, false)
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
            },reasonSelect.toString())
        }
    }


    private fun reasonTakeAwayApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            reasonTakeAwayViewModel.getTakeAwayReason {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, BodyGoalModel::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data)
                        } else {
                            if (bodyModel.code == ErrorMessage.code) {
                                showAlertFunction(bodyModel.message, true)
                            }else{
                                showAlertFunction(bodyModel.message, false)
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
            }
        }
    }

    private fun showDataInUi(bodyModelData: List<BodyGoalModelData>) {
        if (bodyModelData!=null && bodyModelData.size>0){
            bodyGoalAdapter = BodyGoalAdapter(bodyModelData, requireActivity(), this)
            binding!!.rcyTakeAway.adapter = bodyGoalAdapter
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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
            sessionManagement.setReasonTakeAway("")
            dialogStillSkip.dismiss()
            val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun itemClick(selectItem: Int?, status1: String?, type: String?) {
        reasonSelect=""
        if (status1.equals("-1")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            reasonSelect = selectItem.toString()
            return
        }

        if (type.equals("true")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            reasonSelect = selectItem.toString()
        } else {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
    }


}
package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.reasonTakeAway

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.LetsStartOptionActivity
import com.yesitlabs.mykaapp.adapter.BodyGoalAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentReasonsForTakeAwayBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.reasonTakeAway.viewmodel.ReasonTakeAwayViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReasonsForTakeAwayFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentReasonsForTakeAwayBinding? = null
    private val dataList = ArrayList<DataModel>()
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue: Int = 0
    private var status: String? = ""
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

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        if (BaseApplication.isOnline(requireContext())) {
            reasonTakeAwayApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }


//        eatingOutModel()
        initialize()

        return binding!!.root
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
                val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
                startActivity(intent)
            }
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
                        Log.d("@@@ Response profile", "message :- ${it.data}")
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
            dialogStillSkip.dismiss()
            val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
            startActivity(intent)
        }
    }

//    private fun eatingOutModel() {
//        val data1 = DataModel()
//        val data2 = DataModel()
//        val data3 = DataModel()
//        val data4 = DataModel()
//        val data5 = DataModel()
//
//        data1.title = "No food prepared"
//        data1.isOpen= false
//        data1.type = "TakeAway"
//
//        data2.title = "Convenience"
//        data2.isOpen= false
//        data2.type = "TakeAway"
//
//        data3.title = "Cravings"
//        data3.isOpen= false
//        data3.type = "TakeAway"
//
//        data4.title = "Social Occasions"
//        data4.isOpen= false
//        data4.type = "TakeAway"
//
//        data5.title = "Add More"
//        data5.isOpen= false
//        data5.type = "TakeAway"
//
//        dataList.add(data1)
//        dataList.add(data2)
//        dataList.add(data3)
//        dataList.add(data4)
//        dataList.add(data5)
//
//        dietaryRestrictionsAdapter = AdapterCookingSchedule(dataList, requireActivity(),this)
//        binding!!.rcyEatingOut.adapter = dietaryRestrictionsAdapter
//    }

    override fun itemClick(position: Int?, status1: String?, type: String?) {
        if (status1 == "1") {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)

        }
    }


}
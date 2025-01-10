package com.mykameal.planner.fragment.commonfragmentscreen.eatingOut

import android.app.Dialog
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
import com.mykameal.planner.adapter.BodyGoalAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentEatingOutBinding
import com.mykameal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.mykameal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykameal.planner.fragment.commonfragmentscreen.eatingOut.viewmodel.EatingOutViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EatingOutFragment : Fragment(),View.OnClickListener,OnItemClickListener {

    private var binding: FragmentEatingOutBinding? = null
    private var status:String=""
    private var eatingOutSelect: String? = ""
    private var bodyGoalAdapter: BodyGoalAdapter? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private lateinit var eatingOutViewModel: EatingOutViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEatingOutBinding.inflate(inflater, container, false)

        eatingOutViewModel = ViewModelProvider(this)[EatingOutViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.progressBar10.max=10
            totalProgressValue=10
            updateProgress(9)
        } else {
            binding!!.progressBar10.max=11
            totalProgressValue=11
            updateProgress(10)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateEatingOut.visibility=View.VISIBLE
            if (BaseApplication.isOnline(requireActivity())) {
                eatingOutSelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateEatingOut.visibility=View.GONE
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                eatingOutApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun eatingOutSelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            eatingOutViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.eatingout)
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
        binding!!.progressBar10.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imbBackEatingOut.setOnClickListener(this)
        binding!!.tvSkipBtn.setOnClickListener(this)
        binding!!.tvNextBtn.setOnClickListener(this)
        binding!!.rlUpdateEatingOut.setOnClickListener(this)
//        binding!!.rlEatingDropDown.setOnClickListener(this)
//        binding!!.relDaily.setOnClickListener(this)
//        binding!!.relAFewTimes.setOnClickListener(this)
//        binding!!.relOnceAWeek.setOnClickListener(this)
//        binding!!.relRarely.setOnClickListener(this)

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
            sessionManagement.setEatingOut(eatingOutSelect.toString())
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.reasonsForTakeAwayFragment)
        }
    }

    private fun eatingOutApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            eatingOutViewModel.getEatingOut {
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

        if (bodyModelData!=null && bodyModelData.isNotEmpty()){
            bodyGoalAdapter = BodyGoalAdapter(bodyModelData, requireActivity(), this)
            binding!!.rcyEatingOut.adapter = bodyGoalAdapter
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun onClick(item: View?) {
        when (item!!.id) {
            R.id.imbBackEatingOut -> {
                findNavController().navigateUp()
            }

            R.id.tvSkipBtn->{
                stillSkipDialog()
            }

            R.id.tvNextBtn->{
                if (status=="2"){
                    sessionManagement.setEatingOut(eatingOutSelect.toString())
                    findNavController().navigate(R.id.reasonsForTakeAwayFragment)
//                    val intent = Intent(requireActivity(), LetsStartOptionActivity::class.java)
//                    startActivity(intent)
                }
            }

            R.id.rlUpdateEatingOut->{
                if (BaseApplication.isOnline(requireActivity())) {
                    updateEatingOutApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

        }
    }

    private fun updateEatingOutApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            eatingOutViewModel.updateEatingOutApi({
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
            }, eatingOutSelect)
        }
    }

    private fun status(){
        if (status != "2") {
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)

        }
    }

    override fun itemClick(selectItem: Int?, status1: String?, type: String?) {

        if (status1.equals("-1")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            eatingOutSelect = selectItem.toString()
            return
        }

        if (type.equals("true")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            eatingOutSelect = selectItem.toString()
        } else {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
       /* if (status1 == "1") {
            status()
        } else {
            status="2"
            eatingOutSelect=selectItem.toString()
            status()
        }*/
    }
}
package com.mykaimeal.planner.fragment.commonfragmentscreen.bodyGoals

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.BodyGoalAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentBodyGoalsBinding
import com.mykaimeal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModel
import com.mykaimeal.planner.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.mykaimeal.planner.fragment.commonfragmentscreen.bodyGoals.viewmodel.BodyGoalViewModel
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykaimeal.planner.messageclass.ErrorMessage

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BodyGoalsFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentBodyGoalsBinding? = null
    private var bodyGoalAdapter: BodyGoalAdapter? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue: Int = 0
    private var status: String? = null
    private var bodySelect: String? = ""
    private lateinit var bodyGoalViewModel: BodyGoalViewModel
    private var bodyModelData1: List<BodyGoalModelData>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBodyGoalsBinding.inflate(inflater, container, false)

        bodyGoalViewModel = ViewModelProvider(this)[BodyGoalViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())

        /// checked session value cooking for
        if (sessionManagement.getCookingFor().equals("Myself")) {
            binding!!.tvYourBodyGoals.text = "What are your body goals?"
            binding!!.textBodyGoals.visibility = View.VISIBLE
            binding!!.textBodyMembersGoals.visibility = View.GONE
            /*binding!!.textBodyGoals.text= getString(R.string.body_goals)*/
            binding!!.progressBar.max = 10
            totalProgressValue = 10
            updateProgress(1)

        } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
            binding!!.tvYourBodyGoals.text = "You and your partner's goals?"
            binding!!.textBodyGoals.visibility = View.VISIBLE
            binding!!.textBodyMembersGoals.visibility = View.GONE
            binding!!.progressBar.max = 11
            totalProgressValue = 11
            updateProgress(2)
        } else {
            binding!!.tvYourBodyGoals.text = "What are your family's body goals?"
            binding!!.textBodyGoals.visibility = View.GONE
            binding!!.textBodyMembersGoals.visibility = View.VISIBLE
            binding!!.progressBar.max = 11
            totalProgressValue = 11
            updateProgress(2)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")) {
            binding!!.llBottomBtn.visibility = View.GONE
            binding!!.rlUpdateBodyGoals.visibility = View.VISIBLE

            if (BaseApplication.isOnline(requireActivity())) {
                bodyGoalSelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        } else {
            binding!!.llBottomBtn.visibility = View.VISIBLE
            binding!!.rlUpdateBodyGoals.visibility = View.GONE

            if (bodyGoalViewModel.getBodyGoalData()!=null){
                showDataInUi(bodyGoalViewModel.getBodyGoalData()!!)
            }else{
                ///checking the device of mobile data in online and offline(show network error message)
                if (BaseApplication.isOnline(requireActivity())) {
                    bodyGoalApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }


        ///handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (sessionManagement.getCookingScreen() != "Profile") {
                        if (sessionManagement.getCookingFor().equals("Myself")) {
                           /* val intent = Intent(requireActivity(), CookingForScreenActivity::class.java)
                            startActivity(intent)*/
                            requireActivity().finish()
                        } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                            findNavController().navigateUp()
                        } else {
                            findNavController().navigateUp()
                        }
                    } else {
                        findNavController().navigateUp()
                    }
                }
            })

        initialize()

        return binding!!.root
    }

    private fun bodyGoalSelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            bodyGoalViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                            if (bodyModel.code == 200 && bodyModel.success) {
                                showDataInUi(bodyModel.data.bodygoal)
                            } else {
                                if (bodyModel.code == ErrorMessage.code) {
                                    showAlertFunction(bodyModel.message, true)
                                } else {
                                    showAlertFunction(bodyModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("bodyGoal@@","message"+e.message)
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

    private fun bodyGoalApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            bodyGoalViewModel.getBodyGoal {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val bodyModel = gson.fromJson(it.data, BodyGoalModel::class.java)
                            if (bodyModel.code == 200 && bodyModel.success) {
                                showDataInUi(bodyModel.data)
                            } else {
                                if (bodyModel.code == ErrorMessage.code) {
                                    showAlertFunction(bodyModel.message, true)
                                } else {
                                    showAlertFunction(bodyModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("bodyGoal@@@@","message"+e.message)

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

    private fun showDataInUi(bodyModelData: MutableList<BodyGoalModelData>) {
        try {
            if (bodyModelData != null && bodyModelData.size > 0) {
                bodyModelData1 = bodyModelData
                bodyGoalAdapter = BodyGoalAdapter(bodyModelData, requireActivity(), this)
                binding!!.rcyBodyGoals.adapter = bodyGoalAdapter
            }
        }catch (e:Exception){
            Log.d("bodyGoal","message"+e.message)
        }

    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imageBackBodyGoals.setOnClickListener {
            if (sessionManagement.getCookingScreen() != "Profile") {
                if (sessionManagement.getCookingFor().equals("Myself")) {
                    requireActivity().finish()
                } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                    findNavController().navigateUp()
                } else {
                    findNavController().navigateUp()
                }
            } else {
                findNavController().navigateUp()
            }
        }

        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener {
            if (status == "2") {
                bodyGoalViewModel.setBodyGoalData(bodyModelData1!!.toMutableList())
                sessionManagement.setBodyGoal(bodySelect.toString())
//                NavAnimations.navigateForward(findNavController(), R.id.dietaryRestrictionsFragment)
                findNavController().navigate(R.id.dietaryRestrictionsFragment)
            }
        }

        binding!!.rlUpdateBodyGoals.setOnClickListener {
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireActivity())) {
                updateBodyGoalApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateBodyGoalApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            bodyGoalViewModel.updateBodyGoalApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val updateModel =
                                gson.fromJson(it.data, UpdatePreferenceSuccessfully::class.java)
                            if (updateModel.code == 200 && updateModel.success) {
                                findNavController().navigateUp()
                            } else {
                                if (updateModel.code == ErrorMessage.code) {
                                    showAlertFunction(updateModel.message, true)
                                } else {
                                    showAlertFunction(updateModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("bodyGoal@@@","message"+e.message)
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }, bodySelect.toString())
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
            sessionManagement.setBodyGoal(bodySelect.toString())
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.dietaryRestrictionsFragment)
        }
    }


    override fun itemClick(selectItem: Int?, status1: String?, type: String?) {

        bodySelect = ""

        if (status1.equals("-1")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            bodySelect = selectItem.toString()
            return
        }

        if (type.equals("true")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            bodySelect = selectItem.toString()
        } else {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
    }

}
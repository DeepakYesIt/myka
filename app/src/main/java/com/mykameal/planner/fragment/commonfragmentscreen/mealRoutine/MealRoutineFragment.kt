package com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine

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
import com.mykameal.planner.OnItemClickedListener
import com.mykameal.planner.R
import com.mykameal.planner.adapter.MealRoutineAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentMealRoutineBinding
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModel
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.viewmodel.MealRoutineViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealRoutineFragment : Fragment(),View.OnClickListener, OnItemClickedListener {

    private var binding: FragmentMealRoutineBinding? = null

    private lateinit var sessionManagement: SessionManagement
    private var mealRoutineAdapter: MealRoutineAdapter? = null
    private var totalProgressValue:Int=0
    private var status:String?=""
    private var mealRoutineSelectedId = mutableListOf<String>()
    private lateinit var mealRoutineViewModel: MealRoutineViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMealRoutineBinding.inflate(inflater, container, false)

        mealRoutineViewModel = ViewModelProvider(this)[MealRoutineViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.textAllergensIng.visibility=View.VISIBLE
            binding!!.textAllergensIngPartner.visibility=View.GONE
            binding!!.textAllergensIngFamily.visibility=View.GONE
            binding!!.tvMealRoutineDesc.text=getString(R.string.meal_routine_desc)
            binding!!.progressBar6.max=10
            totalProgressValue=10
            updateProgress(6)
        } else if (sessionManagement.getCookingFor().equals("MyPartner")){
            binding!!.textAllergensIng.visibility=View.GONE
            binding!!.textAllergensIngPartner.visibility=View.VISIBLE
            binding!!.textAllergensIngFamily.visibility=View.GONE
            binding!!.tvMealRoutineDesc.text="Which days do you guys normally meal prep or cook on?\n"
            binding!!.progressBar6.max=11
            totalProgressValue=11
            updateProgress(7)
        } else {
            binding!!.textAllergensIng.visibility=View.GONE
            binding!!.textAllergensIngPartner.visibility=View.GONE
            binding!!.textAllergensIngFamily.visibility=View.VISIBLE
            binding!!.tvMealRoutineDesc.text="What meals do you typically cook for your family?"
            binding!!.progressBar6.max=11
            totalProgressValue=11
            updateProgress(7)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateMealRoutine.visibility=View.VISIBLE
            if (BaseApplication.isOnline(requireContext())) {
                mealRoutineSelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateMealRoutine.visibility=View.GONE
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                mealRoutineApi()
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

    private fun updateProgress(progress: Int) {
        binding!!.progressBar6.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.tvSkipBtn.setOnClickListener(this)
        binding!!.imgBackMealRoutine.setOnClickListener(this)
        binding!!.tvNextBtn.setOnClickListener(this)
        binding!!.rlUpdateMealRoutine.setOnClickListener(this)

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
            sessionManagement.setMealRoutineList(null)
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.cookingFrequencyFragment)
        }
    }

    private fun mealRoutineApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            mealRoutineViewModel.getMealRoutine {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val mealRoutineModel = gson.fromJson(it.data, MealRoutineModel::class.java)
                        if (mealRoutineModel.code == 200 && mealRoutineModel.success) {
                            showDataInUi(mealRoutineModel.data)
                        } else {
                            if (mealRoutineModel.code == ErrorMessage.code) {
                                showAlertFunction(mealRoutineModel.message, true)
                            }else{
                                showAlertFunction(mealRoutineModel.message, false)
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

    private fun mealRoutineSelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            mealRoutineViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.mealroutine)
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

    private fun showDataInUi(dietaryModelData: List<MealRoutineModelData>) {
        if (dietaryModelData!=null && dietaryModelData.isNotEmpty()){
            mealRoutineAdapter = MealRoutineAdapter(dietaryModelData, requireActivity(), this)
            binding!!.rcyMealRoutine.adapter = mealRoutineAdapter
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun onClick(item: View?) {
        when(item!!.id){

            R.id.imgBackMealRoutine->{
                findNavController().navigateUp()
            }
            R.id.tvSkipBtn-> {
                stillSkipDialog()
            }

            R.id.tvNextBtn ->{
                if (status=="2"){
                    sessionManagement.setMealRoutineList(mealRoutineSelectedId)
                    findNavController().navigate(R.id.cookingFrequencyFragment)
                }
            }

            R.id.rlUpdateMealRoutine->{
                if (BaseApplication.isOnline(requireContext())) {
                    updateMealRoutineApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

        }
    }

    private fun updateMealRoutineApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            mealRoutineViewModel.updateMealRoutineApi({
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
            },mealRoutineSelectedId)
        }
    }

    override fun itemClicked(position: Int?, list: MutableList<String>, status1: String?, type: String?) {
    /*    if (status1 == "1") {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            mealRoutineSelectedId=list
        }*/

        if (status1.equals("-1")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            mealRoutineSelectedId=list
            return
        }

        if (type.equals("true")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            mealRoutineSelectedId=list
        } else {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
    }


}
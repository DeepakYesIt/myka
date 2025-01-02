package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine

import android.app.Dialog
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
import com.yesitlabs.mykaapp.adapter.MealRoutineAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentMealRoutineBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.viewmodel.MealRoutineViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealRoutineFragment : Fragment(),View.OnClickListener, OnItemClickListener {

    private var binding: FragmentMealRoutineBinding? = null

    private lateinit var sessionManagement: SessionManagement
    private var mealRoutineAdapter: MealRoutineAdapter? = null
    private var totalProgressValue:Int=0
    private var status:String?=""
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
//        binding!!.relSelectAll.setOnClickListener(this)
//        binding!!.relBreakFast.setOnClickListener(this)
//        binding!!.relLunch.setOnClickListener(this)
//        binding!!.relDinner.setOnClickListener(this)
//        binding!!.relSnacks.setOnClickListener(this)

        if (BaseApplication.isOnline(requireContext())) {
            mealRoutineApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
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
                        Log.d("@@@ Response profile", "message :- ${it.data}")
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
                    findNavController().navigate(R.id.cookingFrequencyFragment)
                }
            }

//            R.id.relSelectAll ->{
//                if (isOpenSelectAll){
//                    status=""
//                    isOpenSelectAll=false
//                    binding!!.relSelectAll.setBackgroundResource(R.drawable.gray_box_border_bg)
//                    binding!!.imageSelectAll.visibility=View.GONE
//
//                    binding!!.relBreakFast.setBackgroundResource(R.drawable.gray_box_border_bg)
//                    binding!!.imageBreakFast.visibility=View.GONE
//
//                    binding!!.relLunch.setBackgroundResource(R.drawable.gray_box_border_bg)
//                    binding!!.imageLunch.visibility=View.GONE
//
//                    binding!!.relDinner.setBackgroundResource(R.drawable.gray_box_border_bg)
//                    binding!!.imageDinner.visibility=View.GONE
//
//                    binding!!.relSnacks.setBackgroundResource(R.drawable.gray_box_border_bg)
//                    binding!!.imageSnacks.visibility=View.GONE
//                    status()
//                }else{
//                    status="2"
//                    binding!!.relSelectAll.setBackgroundResource(R.drawable.green_box_bg)
//                    binding!!.imageSelectAll.visibility=View.VISIBLE
//
//                    binding!!.relBreakFast.setBackgroundResource(R.drawable.orange_box_bg)
//                    binding!!.imageBreakFast.visibility=View.VISIBLE
//
//                    binding!!.relLunch.setBackgroundResource(R.drawable.orange_box_bg)
//                    binding!!.imageLunch.visibility=View.VISIBLE
//
//                    binding!!.relDinner.setBackgroundResource(R.drawable.orange_box_bg)
//                    binding!!.imageDinner.visibility=View.VISIBLE
//
//                    binding!!.relSnacks.setBackgroundResource(R.drawable.orange_box_bg)
//                    binding!!.imageSnacks.visibility=View.VISIBLE
//                    isOpenSelectAll=true
//                    status()
//                }
//
//            }

//            R.id.relBreakFast->{
//                status="2"
//                binding!!.relSelectAll.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSelectAll.visibility=View.GONE
//
//                binding!!.relBreakFast.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageBreakFast.visibility=View.VISIBLE
//
//                binding!!.relLunch.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageLunch.visibility=View.GONE
//
//                binding!!.relDinner.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageDinner.visibility=View.GONE
//
//                binding!!.relSnacks.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSnacks.visibility=View.GONE
//                status()
//            }
//
//            R.id.relLunch->{
//                status="2"
//                binding!!.relSelectAll.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSelectAll.visibility=View.GONE
//
//                binding!!.relBreakFast.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageBreakFast.visibility=View.GONE
//
//                binding!!.relLunch.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageLunch.visibility=View.VISIBLE
//
//                binding!!.relDinner.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageDinner.visibility=View.GONE
//
//                binding!!.relSnacks.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSnacks.visibility=View.GONE
//                status()
//            }
//
//            R.id.relDinner->{
//                status="2"
//                binding!!.relSelectAll.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSelectAll.visibility=View.GONE
//
//                binding!!.relBreakFast.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageBreakFast.visibility=View.GONE
//
//                binding!!.relLunch.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageLunch.visibility=View.GONE
//
//                binding!!.relDinner.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageDinner.visibility=View.VISIBLE
//
//                binding!!.relSnacks.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSnacks.visibility=View.GONE
//                status()
//
//            }
//
//            R.id.relSnacks->{
//                status="2"
//                binding!!.relSelectAll.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageSelectAll.visibility=View.GONE
//
//                binding!!.relBreakFast.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageBreakFast.visibility=View.GONE
//
//                binding!!.relLunch.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageLunch.visibility=View.GONE
//
//                binding!!.relDinner.setBackgroundResource(R.drawable.gray_box_border_bg)
//                binding!!.imageDinner.visibility=View.GONE
//
//                binding!!.relSnacks.setBackgroundResource(R.drawable.orange_box_bg)
//                binding!!.imageSnacks.visibility=View.VISIBLE
//                status()
//
//            }

        }
    }

//    private fun status(){
//        if (status != "2") {
//            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
//        } else {
//            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
//
//        }
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
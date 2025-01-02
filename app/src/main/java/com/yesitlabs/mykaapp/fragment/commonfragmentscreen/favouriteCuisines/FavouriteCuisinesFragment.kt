package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.favouriteCuisines

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
import com.yesitlabs.mykaapp.adapter.AdapterCookingSchedule
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentFavouriteCuisinesBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.favouriteCuisines.viewmodel.FavouriteCuisineViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouriteCuisinesFragment : Fragment(),OnItemClickListener {

    private var binding: FragmentFavouriteCuisinesBinding? = null
    private var adapterCookingSchedule: AdapterCookingSchedule? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String?=""
    private lateinit var favouriteCuisineViewModel: FavouriteCuisineViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteCuisinesBinding.inflate(inflater, container, false)

        favouriteCuisineViewModel = ViewModelProvider(this)[FavouriteCuisineViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.tvCuisinesEnjoy.text="What cuisines do you enjoy most?"
            binding!!.progressBar3.max=10
            totalProgressValue=10
            updateProgress(3)
        }else if (sessionManagement.getCookingFor().equals("MyPartner")){
            binding!!.tvCuisinesEnjoy.text="What cuisines do you and your partner enjoy most?"
            binding!!.progressBar3.max=11
            totalProgressValue=11
            updateProgress(6)
        } else {
            binding!!.tvCuisinesEnjoy.text="What cuisines do you and your family enjoy most?"
            binding!!.progressBar3.max=11
            totalProgressValue=11
            updateProgress(6)
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        if (BaseApplication.isOnline(requireContext())) {
            favouriteCuisineApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

//        favouritesModel()
        initialize()

        return binding!!.root
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar3.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imbBackFavouriteCuisines.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            if (status=="2"){
                if (sessionManagement.getCookingFor().equals("Myself")){
                    findNavController().navigate(R.id.ingredientDislikesFragment)
                } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                    findNavController().navigate(R.id.mealRoutineFragment)
//                    findNavController().navigate(R.id.cookingScheduleFragment)
                } else {
                    findNavController().navigate(R.id.mealRoutineFragment)
                }
            }
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
            if (sessionManagement.getCookingFor().equals("Myself")){
                findNavController().navigate(R.id.ingredientDislikesFragment)
            } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                findNavController().navigate(R.id.cookingScheduleFragment)
            } else {
                findNavController().navigate(R.id.mealRoutineFragment)
            }
        }
    }

    private fun favouriteCuisineApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            favouriteCuisineViewModel.getFavouriteCuisines {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val dietaryModel = gson.fromJson(it.data, DietaryRestrictionsModel::class.java)
                        Log.d("@@@ Response profile", "message :- ${it.data}")
                        if (dietaryModel.code == 200 && dietaryModel.success) {
                            showDataInUi(dietaryModel.data)
                        } else {
                            if (dietaryModel.code == ErrorMessage.code) {
                                showAlertFunction(dietaryModel.message, true)
                            }else{
                                showAlertFunction(dietaryModel.message, false)
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

    private fun showDataInUi(dietaryModelData: List<DietaryRestrictionsModelData>) {

        if (dietaryModelData!=null && dietaryModelData.size>0){
            adapterCookingSchedule = AdapterCookingSchedule(dietaryModelData, requireActivity(), this)
            binding!!.rcyFavCuisines.adapter = adapterCookingSchedule
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

//    private fun favouritesModel() {
//        val data1 = DataModel()
//        val data2 = DataModel()
//        val data3 = DataModel()
//        val data4 = DataModel()
//        val data5 = DataModel()
//        val data6 = DataModel()
//        val data7 = DataModel()
//        val data8 = DataModel()
//        val data9 = DataModel()
//
//        data1.title = "Italian"
//        data1.isOpen= false
//        data1.type = "Cuisines"
//
//        data2.title = "Spanish"
//        data2.isOpen= false
//        data2.type = "Cuisines"
//
//        data3.title = "Mexican"
//        data3.isOpen= false
//        data3.type = "Cuisines"
//
//        data4.title = "Caribbean"
//        data4.isOpen= false
//        data4.type = "Cuisines"
//
//        data5.title = "Mediterranean"
//        data5.isOpen= false
//        data5.type = "Cuisines"
//
//        data6.title = "Chinese"
//        data6.isOpen= false
//        data6.type = "Cuisines"
//
//        data7.title = "Indian"
//        data7.isOpen= false
//        data7.type = "Cuisines"
//
//        data8.title = "American"
//        data8.isOpen= false
//        data8.type = "Cuisines"
//
//        data9.title = "Add More"
//        data9.isOpen= false
//        data9.type = "Cuisines"
//
//        dataList.add(data1)
//        dataList.add(data2)
//        dataList.add(data3)
//        dataList.add(data4)
//        dataList.add(data5)
//        dataList.add(data6)
//        dataList.add(data7)
//        dataList.add(data8)
//        dataList.add(data9)
//
//        dietaryRestrictionsAdapter = AdapterCookingSchedule(dataList, requireActivity(),this)
//        binding!!.rcyFavCuisines.adapter = dietaryRestrictionsAdapter
//    }

    override fun itemClick(position: Int?, status1: String?, type: String?) {
        if (status1 == "1") {
            status=""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            status="2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)

        }
    }

}
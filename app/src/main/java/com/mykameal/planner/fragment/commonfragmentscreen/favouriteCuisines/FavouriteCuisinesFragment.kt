package com.mykameal.planner.fragment.commonfragmentscreen.favouriteCuisines

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
import com.mykameal.planner.adapter.AdapterFavouriteCuisinesItem
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentFavouriteCuisinesBinding
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModel
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.mykameal.planner.fragment.commonfragmentscreen.favouriteCuisines.viewmodel.FavouriteCuisineViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouriteCuisinesFragment : Fragment(),OnItemClickedListener {

    private var binding: FragmentFavouriteCuisinesBinding? = null
    private var adapterFavouriteCuisinesItem: AdapterFavouriteCuisinesItem? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String?=""
    private var favouriteSelectId = mutableListOf<String>()
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

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateFavCuisine.visibility=View.VISIBLE
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateFavCuisine.visibility=View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })


        if (sessionManagement.getCookingScreen()!="Profile"){
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                favouriteCuisineApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            if (BaseApplication.isOnline(requireContext())) {
                favouriteCuisineSelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

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
                sessionManagement.setFavouriteCuisineList(favouriteSelectId)
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

        binding!!.rlUpdateFavCuisine.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())) {
                updateFavCuisineApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateFavCuisineApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            favouriteCuisineViewModel.updateFavouriteApi({
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
            }, favouriteSelectId)
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
            sessionManagement.setFavouriteCuisineList(null)
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

    private fun favouriteCuisineSelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            favouriteCuisineViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.favouritcuisine)
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


    private fun favouriteCuisineApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            favouriteCuisineViewModel.getFavouriteCuisines {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val dietaryModel = gson.fromJson(it.data, DietaryRestrictionsModel::class.java)
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

        if (dietaryModelData!=null && dietaryModelData.isNotEmpty()){
            adapterFavouriteCuisinesItem = AdapterFavouriteCuisinesItem(dietaryModelData, requireActivity(), this)
            binding!!.rcyFavCuisines.adapter = adapterFavouriteCuisinesItem
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    override fun itemClicked(position: Int?, list: MutableList<String>, status1: String?, type: String?
    ) {
    /*    if (status1 == "1") {
            status=""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            status="2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            favouriteSelectId=list
        }*/

        if (status1.equals("-1")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            favouriteSelectId=list
            return
        }

        if (type.equals("true")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            favouriteSelectId=list
        } else {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
    }

}
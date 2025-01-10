package com.mykameal.planner.fragment.commonfragmentscreen.ingredientDislikes

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.OnItemClickedListener
import com.mykameal.planner.R
import com.mykameal.planner.adapter.DietaryRestrictionsAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentIngredientDislikesBinding
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModel
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.mykameal.planner.fragment.commonfragmentscreen.ingredientDislikes.viewmodel.DislikeIngredientsViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class IngredientDislikesFragment : Fragment(),OnItemClickedListener {

    private var binding: FragmentIngredientDislikesBinding? = null
    private var dietaryRestrictionsModelData = mutableListOf<DietaryRestrictionsModelData>()
    private var dietaryRestrictionsAdapter: DietaryRestrictionsAdapter? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String?=""
    private var dislikeSelectedId = mutableListOf<String>()
    private lateinit var dislikeIngredientsViewModel: DislikeIngredientsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIngredientDislikesBinding.inflate(inflater, container, false)

        dislikeIngredientsViewModel = ViewModelProvider(this)[DislikeIngredientsViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.tvIngDislikes.text="Pick or search the ingredients you dislike"
            binding!!.progressBar4.max=10
            totalProgressValue=10
            updateProgress(4)
        } else if (sessionManagement.getCookingFor().equals("MyPartner")){
            binding!!.tvIngDislikes.text="Select ingredients that you and your partner dislike"
            binding!!.progressBar4.max=11
            totalProgressValue=11
            updateProgress(4)
        } else {
            binding!!.tvIngDislikes.text="Ingredients that you dislike"
            binding!!.progressBar4.max=11
            totalProgressValue=11
            updateProgress(4)
        }

        if (sessionManagement.getCookingScreen().equals("Profile")){
            binding!!.llBottomBtn.visibility=View.GONE
            binding!!.rlUpdateIngDislike.visibility=View.VISIBLE
        }else{
            binding!!.llBottomBtn.visibility=View.VISIBLE
            binding!!.rlUpdateIngDislike.visibility=View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        if (sessionManagement.getCookingScreen()!="Profile"){
            ///checking the device of mobile data in online and offline(show network error message)
            if (BaseApplication.isOnline(requireContext())) {
                ingredientDislikeApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }else{
            if (BaseApplication.isOnline(requireContext())) {
                ingredientDislikeSelectApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        initialize()

        return binding!!.root
    }

    private fun ingredientDislikeSelectApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            dislikeIngredientsViewModel.userPreferencesApi {
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val bodyModel = gson.fromJson(it.data, GetUserPreference::class.java)
                        if (bodyModel.code == 200 && bodyModel.success) {
                            showDataInUi(bodyModel.data.ingredientdislike)
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
        binding!!.progressBar4.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imbBackIngDislikes.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            if (status=="2"){
                sessionManagement.setDislikeIngredientList(dislikeSelectedId)
                findNavController().navigate(R.id.allergensIngredientsFragment)
            }
        }

        binding!!.etIngDislikesSearchBar.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable(editable.toString())
            }
        })

        binding!!.rlUpdateIngDislike.setOnClickListener{
            if (BaseApplication.isOnline(requireContext())) {
                updateIngDislikeApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun updateIngDislikeApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            dislikeIngredientsViewModel.updateDislikedIngredientsApi({
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
            },dislikeSelectedId)
        }
    }

    private fun ingredientDislikeApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            dislikeIngredientsViewModel.getDislikeIngredients {
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
            dietaryRestrictionsModelData= dietaryModelData.toMutableList()
            dietaryRestrictionsAdapter = DietaryRestrictionsAdapter(dietaryModelData, requireActivity(), this)
            binding!!.rcyIngDislikes.adapter = dietaryRestrictionsAdapter
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun searchable(editText: String) {
        val filteredList: MutableList<DietaryRestrictionsModelData> = java.util.ArrayList<DietaryRestrictionsModelData>()
        for (item in dietaryRestrictionsModelData) {
            if (item.name.toLowerCase().contains(editText.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.size > 0) {
            dietaryRestrictionsAdapter!!.filterList(filteredList)
            binding!!.rcyIngDislikes.visibility = View.VISIBLE
        } else {
            binding!!.rcyIngDislikes.visibility = View.GONE
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
            sessionManagement.setDislikeIngredientList(null)

            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.allergensIngredientsFragment)
        }
    }



    override fun itemClicked(position: Int?, list: MutableList<String>, status1: String?, type: String?) {
        if (status1.equals("-1")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            dislikeSelectedId=list
            return
        }

        if (type.equals("true")) {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            dislikeSelectedId=list
        } else {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
    }

}
package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.allergensIngredients

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.yesitlabs.mykaapp.OnItemClickedListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.adapter.DietaryRestrictionsAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentAllergensIngredientsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.allergensIngredients.viewmodel.AllergenIngredientViewModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class AllergensIngredientsFragment : Fragment(), OnItemClickedListener {

    private var binding: FragmentAllergensIngredientsBinding? = null
    private var dietaryRestrictionsAdapter: DietaryRestrictionsAdapter? = null
    private lateinit var sessionManagement: SessionManagement
    private var dietaryRestrictionsModelData = mutableListOf<DietaryRestrictionsModelData>()
    private var totalProgressValue: Int = 0
    private var status:String?=null
    private var allergensSelectedId = mutableListOf<String>()
    private lateinit var allergenIngredientViewModel: AllergenIngredientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllergensIngredientsBinding.inflate(inflater, container, false)

        allergenIngredientViewModel = ViewModelProvider(this)[AllergenIngredientViewModel::class.java]
        sessionManagement = SessionManagement(requireContext())

        /// checked session value cooking for
        if (sessionManagement.getCookingFor().equals("Myself")) {
            binding!!.tvAllergensDesc.text = getString(R.string.allergens_ingredients_desc)
            binding!!.progressBar5.max = 10
            totalProgressValue = 10
            updateProgress(5)
        } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
            binding!!.tvAllergensDesc.text = "Pick ingredients you and your partner are allergic to"
            binding!!.progressBar5.max = 11
            totalProgressValue = 11
            updateProgress(5)
        } else {
            binding!!.tvAllergensDesc.text =
                "Which ingredients are you and your family allergic to?"
            binding!!.progressBar5.max = 11
            totalProgressValue = 11
            updateProgress(5)
        }

        //// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        ///checking the device of mobile data in online and offline(show network error message)
        /// allergies api implement 
        if (BaseApplication.isOnline(requireContext())) {
            allergenIngredientApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        ///main function using all triggered of this screen
        initialize()

        return binding!!.root
    }

    /// update progressbar value and progress
    private fun updateProgress(progress: Int) {
        binding!!.progressBar5.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        /// handle on back pressed
        binding!!.imgBackAllergensIng.setOnClickListener {
            findNavController().navigateUp()
        }

        /// handle click event for skip this screen
        binding!!.tvSkipBtn.setOnClickListener {
            stillSkipDialog()
        }

        // Add a TextWatcher to monitor changes in the username EditText field.
        // The searchable() function is triggered after text changes to search ingredient
        binding!!.etAllergensIngSearchBar.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable(editable.toString())
            }
        })

        /// handle click event for redirect next part
        binding!!.tvNextBtn.setOnClickListener {
            if (status=="2"){
                sessionManagement.setAllergenIngredientList(allergensSelectedId)
                if (sessionManagement.getCookingFor().equals("Myself")) {
                    findNavController().navigate(R.id.mealRoutineFragment)
                } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                    findNavController().navigate(R.id.favouriteCuisinesFragment)
                } else {
                    findNavController().navigate(R.id.favouriteCuisinesFragment)
                }
            }
        }
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
            binding!!.rcyAllergensDesc.visibility = View.VISIBLE
        } else {
            binding!!.rcyAllergensDesc.visibility = View.GONE
        }
    }

    private fun allergenIngredientApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            allergenIngredientViewModel.getAllergensIngredients {
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
            binding!!.rcyAllergensDesc.adapter = dietaryRestrictionsAdapter
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
            sessionManagement.setAllergenIngredientList(null)
            findNavController().navigate(R.id.mealRoutineFragment)
        }
    }

    override fun itemClicked(position: Int?, list: MutableList<String>, status1: String?, type: String?) {
        if (status1 == "1") {
            status = ""
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        } else {
            status = "2"
            binding!!.tvNextBtn.isClickable = true
            binding!!.tvNextBtn.setBackgroundResource(R.drawable.green_fill_corner_bg)
            allergensSelectedId=list

        }
    }


}
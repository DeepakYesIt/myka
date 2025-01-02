package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.ingredientDislikes

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.adapter.DietaryRestrictionsAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentIngredientDislikesBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.ingredientDislikes.viewmodel.DislikeIngredientsViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class IngredientDislikesFragment : Fragment(),OnItemClickListener {

    private var binding: FragmentIngredientDislikesBinding? = null
    private var dietaryRestrictionsModelData = mutableListOf<DietaryRestrictionsModelData>()
    private var dietaryRestrictionsAdapter: DietaryRestrictionsAdapter? = null
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String?=""
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

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        if (BaseApplication.isOnline(requireContext())) {
            ingredientDislikeApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        initialize()

        return binding!!.root
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
            dialogStillSkip.dismiss()
            findNavController().navigate(R.id.allergensIngredientsFragment)
        }
    }


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
package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions

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
import com.yesitlabs.mykaapp.adapter.DietaryRestrictionsAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentDietaryRestrictionsBinding
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModel
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.viewmodel.DietaryRestrictionsViewModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DietaryRestrictionsFragment : Fragment(),OnItemClickListener {

    private var binding: FragmentDietaryRestrictionsBinding? = null
    private var dietaryRestrictionsAdapter: DietaryRestrictionsAdapter? = null
    private val dataList = ArrayList<DataModel>()
    private lateinit var sessionManagement: SessionManagement
    private var totalProgressValue:Int=0
    private var status:String?=null
    private lateinit var dietaryRestrictionsViewModel: DietaryRestrictionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDietaryRestrictionsBinding.inflate(inflater, container, false)

        dietaryRestrictionsViewModel = ViewModelProvider(this)[DietaryRestrictionsViewModel::class.java]

        sessionManagement = SessionManagement(requireContext())
        if (sessionManagement.getCookingFor().equals("Myself")){
            binding!!.tvRestrictions.text="Do you have any dietary restrictions?"
            binding!!.progressBar2.max=10
            totalProgressValue=10
            updateProgress(2)
        } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
            binding!!.tvRestrictions.text="Do you or your partner have any dietary restrictions?"
            binding!!.progressBar2.max=11
            totalProgressValue=11
            updateProgress(3)
        } else {
            binding!!.tvRestrictions.text="Do you or any of your family members have any  dietary restrictions?"
            binding!!.progressBar2.max=11
            totalProgressValue=11
            updateProgress(3)
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        if (BaseApplication.isOnline(requireContext())) {
            dietaryRestrictionApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        initialize()

        return binding!!.root
    }

    private fun dietaryRestrictionApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            dietaryRestrictionsViewModel.getDietaryRestrictions {
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
            dietaryRestrictionsAdapter = DietaryRestrictionsAdapter(dietaryModelData, requireActivity(), this)
            binding!!.rcyDietaryRestrictions.adapter = dietaryRestrictionsAdapter
        }

    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar2.progress = progress
        binding!!.tvProgressText.text = "$progress/$totalProgressValue"
    }

    private fun initialize() {

        binding!!.imageBackDietaryRestrictions.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.tvSkipBtn.setOnClickListener{
            stillSkipDialog()
        }

        binding!!.tvNextBtn.setOnClickListener{
            if (status=="2"){
                if (sessionManagement.getCookingFor().equals("Myself")){
                    findNavController().navigate(R.id.favouriteCuisinesFragment)
                } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                    findNavController().navigate(R.id.ingredientDislikesFragment)
                } else {
                    findNavController().navigate(R.id.ingredientDislikesFragment)
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
                findNavController().navigate(R.id.favouriteCuisinesFragment)
            } else if (sessionManagement.getCookingFor().equals("MyPartner")) {
                findNavController().navigate(R.id.ingredientDislikesFragment)
            } else {
                findNavController().navigate(R.id.ingredientDislikesFragment)
            }
        }
    }

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
package com.mykameal.planner.fragment.mainfragment.profilesetting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentNutritionGoalBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.SettingViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.Data
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.ProfileRootResponse
import com.mykameal.planner.messageclass.ErrorMessage
import kotlinx.coroutines.launch

class NutritionGoalFragment : Fragment() {

    private lateinit var binding: FragmentNutritionGoalBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNutritionGoalBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[SettingViewModel::class.java]

        setupBackPressHandler()
        initializeUI()
        loadProfileData()

        return binding.root
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun loadProfileData() {
        val profileData = viewModel.getProfileData()
        if (profileData != null) {
            updateUI(profileData)
        } else if (BaseApplication.isOnline(requireActivity())) {
            fetchUserProfileData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchUserProfileData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.userProfileData { result ->
                BaseApplication.dismissMe()
                handleApiResponse(result)
            }
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> parseAndHandleSuccess(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun parseAndHandleSuccess(data: String) {
        try {
            val response = Gson().fromJson(data, ProfileRootResponse::class.java)
            if (response.code == 200 && response.success) {
                response.data?.let {
                    viewModel.setProfileData(it)
                    updateUI(it)
                }
            } else {
                showAlert(response.message, response.code == ErrorMessage.code)
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    private fun showAlert(message: String?, isError: Boolean) {
        BaseApplication.alertError(requireContext(), message, isError)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: Data) {
        if (data.height_protein!=null){
            binding.spinnerHighProtein.text = data.height_protein
        }

        if (data.calories!=null){
            binding.seekbarcalories.progress = data.calories?.toInt() ?: 0
            binding.textCalorisTotal.text=""+data.calories?.toInt()+"/500"
        }else{
            binding.seekbarcalories.progress =  0
            binding.textCalorisTotal.text="0/500"
        }

        if (data.fat!=null){
            binding.seekbarFats.progress = data.fat?.toInt() ?: 0
            binding.textFatTotal.text=""+data.fat?.toInt()+"/500"
        }else{
            binding.seekbarFats.progress =  0
            binding.textFatTotal.text="0/500"
        }

        if (data.protien!=null){
            binding.seekbarProtein.progress = data.protien?.toInt() ?: 0
            binding.textProtienTotal.text=""+data.protien?.toInt()+"/500"
        }else{
            binding.seekbarProtein.progress =  0
            binding.textProtienTotal.text="0/500"
        }

        if (data.carbs!=null){
            binding.seekbarCarbs.progress = data.carbs?.toInt() ?: 0
            binding.textCarbsTotal.text=""+data.carbs?.toInt()+"/500"
        }else{
            binding.seekbarCarbs.progress =  0
            binding.textCarbsTotal.text="0/500"
        }


    }

    private fun initializeUI() {

        binding.imageBackNutrition.setOnClickListener {
            findNavController().navigateUp()
        }





        setSeekBarValue()

        setupSpinner()

        setupUpdateButton()
    }

    private fun setSeekBarValue() {

        // Helper function to set SeekBar change listener and update the corresponding TextView
        fun setSeekBarListener(seekBar: SeekBar, textView: TextView) {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // Update TextView with SeekBar's current value
                    textView.text = "$progress/500"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Do something when touch starts
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Do something when touch stops
                }
            })
        }

        // Set listeners for each SeekBar and corresponding TextView
        setSeekBarListener(binding.seekbarcalories, binding.textCalorisTotal)
        setSeekBarListener(binding.seekbarFats, binding.textFatTotal)
        setSeekBarListener(binding.seekbarCarbs, binding.textCarbsTotal)
        setSeekBarListener(binding.seekbarProtein, binding.textProtienTotal)
    }

    private fun setupSpinner() {
        binding.spinnerHighProtein.setItems(
            listOf("Low fat", "Kito", "High Protein", "Low Carb", "Balanced")
        )

    }

    private fun setupUpdateButton() {

        binding.rlUpdateButton.setOnClickListener {
            viewModel.getProfileData()?.let { data ->
                data.apply {
                    height_protein=binding.spinnerHighProtein.text.toString()
                    fat = binding.seekbarFats.progress
                    carbs = binding.seekbarCarbs.progress
                    calories = binding.seekbarcalories.progress
                    protien = binding.seekbarProtein.progress
                }
                viewModel.setProfileData(data)
                findNavController().navigateUp()
            }
        }
    }
}

package com.yesitlabs.mykaapp.fragment.authfragment.locationfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentTurnOnLocationBinding
import com.yesitlabs.mykaapp.fragment.authfragment.locationfragment.model.LocationModel
import com.yesitlabs.mykaapp.fragment.authfragment.locationfragment.viewmodel.LocationViewModel
import com.yesitlabs.mykaapp.fragment.authfragment.login.model.LoginModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TurnOnLocationFragment : Fragment() {
    private var binding: FragmentTurnOnLocationBinding? = null
    private lateinit var locationViewModel: LocationViewModel
    private var status: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTurnOnLocationBinding.inflate(inflater, container, false)

        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        /// handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        ///main function using all triggered of this screen
        initialize()

        return binding!!.root
    }

    private fun initialize() {

        /// handle on back pressed
        binding!!.imgBackTurnLocation.setOnClickListener {
            findNavController().navigateUp()
        }

        ///checking the device of mobile data in online and offline(show network error message)
        /// turn on location permission and implement api
        binding!!.rlTurnOnLocation.setOnClickListener {
            status = "1"
            if (BaseApplication.isOnline(requireActivity())) {
                locationApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        /// handle click event location permission denied in this time
        ///checking the device of mobile data in online and offline(show network error message)
        ///// implement location api
        binding!!.tvNotNow.setOnClickListener {
            status = "0"
            if (BaseApplication.isOnline(requireActivity())) {
                locationApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }

        }
    }

    /// implement location api & redirection
    private fun locationApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            locationViewModel.updateLocation(
                {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> {
                            val gson = Gson()
                            val locationModel = gson.fromJson(it.data, LocationModel::class.java)
                            if (locationModel.code == 200 && locationModel.success) {
                                findNavController().navigate(R.id.turnOnNotificationsFragment)
                            } else {
                                if (locationModel.code == ErrorMessage.code) {
                                    showAlertFunction(locationModel.message, true)
                                } else {
                                    showAlertFunction(locationModel.message, false)
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
                }, status
            )
        }
    }

    /// show error message
    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

}
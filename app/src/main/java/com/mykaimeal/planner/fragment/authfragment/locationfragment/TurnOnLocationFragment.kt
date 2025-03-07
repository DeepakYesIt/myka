package com.mykaimeal.planner.fragment.authfragment.locationfragment

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentTurnOnLocationBinding
import com.mykaimeal.planner.fragment.authfragment.locationfragment.model.LocationModel
import com.mykaimeal.planner.fragment.authfragment.locationfragment.viewmodel.LocationViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TurnOnLocationFragment : Fragment() {
    private var binding: FragmentTurnOnLocationBinding? = null
    private lateinit var locationViewModel: LocationViewModel
    private var status: String = ""
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTurnOnLocationBinding.inflate(inflater, container, false)

        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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
        binding!!.rlShareLocation.setOnClickListener {
            status = "1"

            findNavController().navigate(R.id.enterYourAddressFragment)
//            requestLocationPermission()

        /*    if (BaseApplication.isOnline(requireActivity())){
                // This condition for check location run time permission
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getCurrentLocation()
                } else {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 100
                    )
                }    
            }else{
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }*/
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, proceed with location access
            getCurrentLocation()
        } else {
            // Show permission request dialog
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Handle Permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireActivity(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        // Initialize Location manager
        val locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            fusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                // Initialize location
                // Check condition
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    // When location result is not
                    // null set latitude
                    status = "1"
//                    locationApi()

                } else {
                    // When location result is null
                    // initialize location request
                    val locationRequest = LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10000)
                        .setFastestInterval(1000)
                    // Initialize location call back
                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // location
                            val location1 = locationResult.lastLocation
                            /*latitude = location1!!.latitude.toString()
                            longitude = location1.longitude.toString()*/

                            status = "1"
//                            locationApi()

                        }
                    }
                    fusedLocationClient!!.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            }
        } else {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
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
                            try {
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
                            }catch (e:Exception){
                                Log.d("Location On","message:-- "+e.message)
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
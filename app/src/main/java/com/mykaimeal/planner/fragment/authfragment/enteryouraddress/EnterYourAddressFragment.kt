package com.mykaimeal.planner.fragment.authfragment.enteryouraddress

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentEnterYourAddressBinding

class EnterYourAddressFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private var binding: FragmentEnterYourAddressBinding? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private val REQUEST_CODE = 101
    private val LOCATION_CODE = 100
    private var latitude = "0.0"
    private var longitude = "0.0"
    private val TAG = "LocationOnOff"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterYourAddressBinding.inflate(inflater, container, false)

        initialize()

        return binding!!.root
    }

    private fun initialize() {
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fetchLocation()
    }

    private fun getCurrentLocation() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            // When location service is enabled
            // Get last location
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationProviderClient!!.lastLocation.addOnCompleteListener(OnCompleteListener<Location?> { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    latitude = location!!.latitude.toString()
                    longitude = location.longitude.toString()
                   /* binding.tvAddress.setText(getAddress(location.latitude, location.longitude))
                    address = getAddress(location.latitude, location.longitude)*/
//                    binding.tvCountryName.setText(cityName + ", " + countryName)
                } else {
                    val locationRequest =
                        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000).setFastestInterval(1000)
                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // Initialize
                            // location
                            val location1 = locationResult.lastLocation
                            latitude = location1!!.latitude.toString()
                            longitude = location1.longitude.toString()
                        }
                    }

                    // Request location updates
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        return@OnCompleteListener
                    }
                    fusedLocationProviderClient!!.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            })
        } else {
            // When location service is not enabled
            // open location setting
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_CODE
            )
            displayLocationSettingsRequest(requireContext())
        }
    }

    private fun displayLocationSettingsRequest(requireContext: Context) {
        val googleApiClient =
            GoogleApiClient.Builder(requireContext).addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 1000
        locationRequest.numUpdates = 1
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(TAG, "All location settings are satisfied.")
                    getCurrentLocation()
                }

                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(requireActivity(), LOCATION_CODE)
                    } catch (e: SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(TAG,
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

    private fun fetchLocation() {
     /*   if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }

        val task: Task<Location> = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            location?.let {
                currentLocation = it
                val mapFragment = childFragmentManager.findFragmentById(R.id.mapid) as? SupportMapFragment
                mapFragment?.getMapAsync(this@EnterYourAddressFragment)
            }
        }*/
    }

    override fun onMapReady(map: GoogleMap) {
        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        map.addMarker(markerOptions)
        map.setOnMapClickListener(this)
    }

    override fun onMapClick(latlong: LatLng) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
           REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }

            LOCATION_CODE -> if (requestCode == 100 && grantResults.size > 0 && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // When permission are granted
                // Call  method
//            getCurrentLocation();
            } else {
                // When permission are denied
                // Display toast
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_SHORT).show()
        }
    }

}
package com.mykaimeal.planner.fragment.mainfragment.commonscreen.addressmapfullscreen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.databinding.FragmentAddressMapFullScreenBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.addressmapfullscreen.viewmodel.AddressMapFullScreenViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class AddressMapFullScreenFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentAddressMapFullScreenBinding? = null
    private lateinit var sessionManagement: SessionManagement
    private lateinit var addressMapFullScreenViewModel:AddressMapFullScreenViewModel
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private var address: String? = ""
    private var setStatus: String? = "Home"
    private var latitude: String? = ""
    private var longitude: String? = ""
    private var streetName: String? = ""
    private var streetNum: String? = ""
    private var apartNum: String? = ""
    private var city: String? = ""
    private var country: String? = ""
    private var zipcode: String? = ""
    private var primary: String? = "1"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressMapFullScreenBinding.inflate(layoutInflater, container, false)

        addressMapFullScreenViewModel = ViewModelProvider(requireActivity())[AddressMapFullScreenViewModel::class.java]

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sessionManagement = SessionManagement(requireContext())

        if (sessionManagement.getLatitude()!=""){
            latitude=sessionManagement.getLatitude().toString()
        }else{
            latitude="40.7128"
        }

        if (sessionManagement.getLongitude()!=""){
            longitude=sessionManagement.getLongitude().toString()
        }else{
            longitude="-74.0060"
        }

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding?.llSetHome?.setOnClickListener {
            setStatus="Home"
            binding!!.llSetHome.setBackgroundResource(R.drawable.outline_green_border_bg)
            binding!!.llSetWork.setBackgroundResource(R.drawable.height_type_bg)

        }

        binding?.llSetWork?.setOnClickListener {
            setStatus="Work"
            binding!!.llSetHome.setBackgroundResource(R.drawable.height_type_bg)
            binding!!.llSetWork.setBackgroundResource(R.drawable.outline_address_green_border_bg)
        }

        binding!!.imageCrossWeb.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.tvConfirmBtn.setOnClickListener {

            sessionManagement.setLatitude(latitude.toString())
            sessionManagement.setLongitude(longitude.toString())
            sessionManagement.setAddress(address.toString())
            findNavController().navigateUp()
            /*if (BaseApplication.isOnline(requireContext())){
                addFullAddressApi()
            }else{
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }*/
        }
    }

    private fun addFullAddressApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            addressMapFullScreenViewModel.addAddressUrl({
                BaseApplication.dismissMe()
                handleApiAddAddressResponse(it)
            },latitude,longitude,streetName,streetNum,apartNum,city,country,zipcode,"1","",setStatus)
        }
    }

    private fun handleApiAddAddressResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessAddAddressResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessAddAddressResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                findNavController().navigateUp()
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                streetName = address.thoroughfare ?: "N/A" // Street Name
                streetNum = address.subThoroughfare ?: "N/A" // Street Number
                apartNum = address.premises ?: "N/A" // Apartment Number
                city = address.subLocality ?: "N/A" // City
                country = address.countryName ?: "N/A" // Country
                zipcode = address.postalCode ?: "N/A" // Zip Code

                Log.d("Address", "Street Name: $streetName")
                Log.d("Address", "Street Number: $streetNum")
                Log.d("Address", "Apartment Number: $apartNum")
                Log.d("Address", "City: $city")
                Log.d("Address", "Country: $country")
                Log.d("Address", "ZIP Code: $zipcode")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat = latitude?.toDoubleOrNull() ?: 0.0  // Convert String to Double, default to 0.0 if null
        val lng = longitude?.toDoubleOrNull() ?: 0.0
        mMap = googleMap
        // 🔹 Clear all markers (if any exist)
        mMap.clear()
        val initialPosition = LatLng(lat, lng) // Example: Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))

        // 🔹 Change marker image when map is moving
        mMap.setOnCameraMoveStartedListener {
            Log.d("TESTING_MAP", "Map is moving...")

            // Change marker image while dragging
            binding!!.markerImage.setImageResource(R.drawable.map_marker_icon)
        }

        mMap.setOnCameraIdleListener {
            val centerPosition = mMap.cameraPosition.target
            // ✅ Ensure marker is initialized before using it
            if (marker == null) {
//                marker = mMap.addMarker(
//                    MarkerOptions()
//                        .position(centerPosition)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                )
            } else {
                marker?.position = centerPosition // ✅ Move marker to the center
            }
            // Change marker image while dragging
            binding!!.markerImage.setImageResource(R.drawable.map_marker_icon)
            Log.d(
                "TESTING_MAP",
                "Dragging started at: ${marker?.position?.latitude}, ${marker?.position?.longitude}"
            )
            // ✅ Ensure LatLng is not null before calling function
            val lat = centerPosition.latitude
            val lng = centerPosition.longitude
            latitude= centerPosition.latitude.toString()
            longitude=centerPosition.longitude.toString()
            binding!!.tvAddress.text = getAddressFromLatLng(lat, lng)
            address = getAddressFromLatLng(lat, lng)
            getAddressFromLocation(lat,lng)
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            geocoder.getFromLocation(latitude, longitude, 1)
                ?.firstOrNull()
                ?.getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if an error occurs
        }
    }


    private fun bitmapDescriptorFromVector(
        vectorResId: Int,
        width: Int,
        height: Int
    ): BitmapDescriptor? {
        val vectorDrawable: Drawable? =
            ContextCompat.getDrawable(requireContext(), vectorResId) ?: return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Manage MapView Lifecycle
    override fun onResume() {
        super.onResume()

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()

    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()

    }

}
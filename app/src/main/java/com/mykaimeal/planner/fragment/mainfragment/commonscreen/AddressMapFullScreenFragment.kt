package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.databinding.FragmentAddressMapFullScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AddressMapFullScreenFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentAddressMapFullScreenBinding
    private lateinit var sessionManagement: SessionManagement
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private var address:String?=""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddressMapFullScreenBinding.inflate(layoutInflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sessionManagement = SessionManagement(requireContext())

        initialize()

        return binding.root
    }

    private fun initialize() {

        binding.imageCrossWeb.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvConfirmBtn.setOnClickListener{
            sessionManagement.setAddress(address.toString())
            findNavController().navigateUp()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // 🔹 Clear all markers (if any exist)
        mMap.clear()
        val initialPosition = LatLng(40.7128, -74.0060) // Example: Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))

        // 🔹 Change marker image when map is moving
        mMap.setOnCameraMoveStartedListener {
            Log.d("TESTING_MAP", "Map is moving...")

            // Change marker image while dragging
            binding.markerImage.setImageResource(R.drawable.current_location_marker)
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
            binding.markerImage.setImageResource(R.drawable.marker_icon_larg)
            Log.d("TESTING_MAP", "Dragging started at: ${marker?.position?.latitude}, ${marker?.position?.longitude}")
            // ✅ Ensure LatLng is not null before calling function
            val lat = centerPosition.latitude
            val lng = centerPosition.longitude
            binding.tvAddress.text = getAddressFromLatLng(lat, lng)
            address=getAddressFromLatLng(lat,lng)
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

}
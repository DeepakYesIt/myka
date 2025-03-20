package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentAddressMapFullScreenBinding
import com.mykaimeal.planner.databinding.FragmentDropOffOptionsScreenBinding
import java.util.Locale

class AddressMapFullScreenFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentAddressMapFullScreenBinding? = null

    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressMapFullScreenBinding.inflate(layoutInflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initialize()
        return binding!!.root
    }

    private fun initialize() {

        binding!!.imageCrossWeb.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // 🔹 Clear all markers (if any exist)
        mMap.clear()
        val initialPosition = LatLng(-34.0, 151.0) // Example: Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))

        // 🔹 Change marker image when map is moving
        mMap.setOnCameraMoveStartedListener {
            Log.d("TESTING_MAP", "Map is moving...")

            // Change marker image while dragging
            binding!!.markerImage.setImageResource(R.drawable.marker_icon)
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
            binding!!.markerImage.setImageResource(R.drawable.marker_icon_larg)
            Log.d("TESTING_MAP", "Dragging started at: ${marker?.position?.latitude}, ${marker?.position?.longitude}")
            // ✅ Ensure LatLng is not null before calling function
            val lat = centerPosition.latitude
            val lng = centerPosition.longitude
            binding!!.tvAddress.text = getAddressFromLatLng(lat, lng)
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


    private fun bitmapDescriptorFromVector(vectorResId: Int, width: Int, height: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), vectorResId) ?: return null
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
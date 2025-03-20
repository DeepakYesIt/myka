package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentAddressMapFullScreenBinding
import com.mykaimeal.planner.databinding.FragmentDropOffOptionsScreenBinding

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

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val initialPosition = LatLng(-34.0, 151.0) // Example: Sydney
        val customMarker = bitmapDescriptorFromVector(R.drawable.marker_icon, 100, 100)




       /* // 🔹 Prevent map panning when marker is clicked
        mMap.setOnMarkerClickListener {
            it.showInfoWindow()
            true // ✅ Prevents map from moving on marker click
        }



        // 🔹 Handle marker drag events
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                Log.d("TESTING_MAP","Dragging started at: ${marker.position.latitude}, ${marker.position.longitude}")

                // ✅ Disable map gestures to avoid panning

                mMap.uiSettings.isScrollGesturesEnabled = true
                mMap.uiSettings.isZoomGesturesEnabled = true
            }

            override fun onMarkerDrag(marker: Marker) {
                println("Dragging at: ${marker.position.latitude}, ${marker.position.longitude}")
            }

            override fun onMarkerDragEnd(marker: Marker) {
                println("Dragging ended at: ${marker.position.latitude}, ${marker.position.longitude}")

                // ✅ Re-enable map gestures after dragging
                mMap.uiSettings.isScrollGesturesEnabled = true
                mMap.uiSettings.isZoomGesturesEnabled = true
                // ✅ Get the new LatLng after marker is dropped
                val newLatLng = marker.position
                Toast.makeText(requireContext(), "New Location: ${newLatLng.latitude}, ${newLatLng.longitude}", Toast.LENGTH_SHORT).show()
            }
        })*/


        // 🔹 Change marker image when map is moving
        mMap.setOnCameraMoveStartedListener {
            Log.d("TESTING_MAP", "Map is moving...")

            // Change marker image while dragging
            binding!!.markerImage.setImageResource(R.drawable.radio_green_icon)
        }

        // 🔹 Detect map movement and update marker position
        mMap.setOnCameraIdleListener {
            val centerPosition = mMap.cameraPosition.target
            marker?.position = centerPosition // Move marker to the center of the screen
            // Change marker image while dragging
            binding!!.markerImage.setImageResource(R.drawable.marker_icon)
            Log.d("TESTING_MAP","Dragging started at: ${marker?.position?.latitude}, ${marker?.position?.longitude}")

            // 🔹 Get the location name when map stops moving
//            getAddressFromLatLng(centerPosition.latitude, centerPosition.longitude)
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
package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentAddressMapFullScreenBinding
import com.mykaimeal.planner.databinding.FragmentDropOffOptionsScreenBinding

class AddressMapFullScreenFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentAddressMapFullScreenBinding? = null

    private var mapViewBundle: Bundle? = null

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressMapFullScreenBinding.inflate(layoutInflater, container, false)

//        // Load saved instance state
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(R.string.api_key.toString())
//
//            mapView = binding!!.gmsMapView
//            mapView.onCreate(savedInstanceState)
//            mapView.getMapAsync(this@AddressMapFullScreenFragment)
//        }

//        mapView = binding!!.gmsMapView
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)



        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> binding!!.scrollView.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> binding!!.scrollView.requestDisallowInterceptTouchEvent(false)
            }
            false
        }


        initialize()
        return binding!!.root
    }

    private fun initialize() {

    }

//    override fun onMapReady(gmap: GoogleMap) {
//        mMap = gmap
//        val newYork = LatLng(28.6070135, 77.4075354)
//        mMap?.addMarker(MarkerOptions().position(newYork).title("Marker in New York"))
//        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(newYork, 12f))
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val initialPosition = LatLng(-34.0, 151.0) // Example: Sydney
        marker = mMap.addMarker(
            MarkerOptions()
                .position(initialPosition)
                .title("Drag me")
                .draggable(true) // ✅ Make sure it's set to true
        )!!

        // Move camera to marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10f))

        // 🔹 Consume marker click event to ensure dragging works
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true // ✅ Consumes the event, preventing map panning
        }

        // Set a listener for marker drag events
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                println("Marker starts dragging at: ${marker.position.latitude}, ${marker.position.longitude}")

                // 🔹 Disable map scrolling when marker starts dragging
                mMap.uiSettings.setAllGesturesEnabled(false)
            }

            override fun onMarkerDrag(marker: Marker) {
                println("Marker is dragging at: ${marker.position.latitude}, ${marker.position.longitude}")
            }

            override fun onMarkerDragEnd(marker: Marker) {
                println("Marker stopped dragging at: ${marker.position.latitude}, ${marker.position.longitude}")

                // 🔹 Re-enable map scrolling after marker drag ends
                mMap.uiSettings.setAllGesturesEnabled(true)
            }
        })
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
package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentAddressMapFullScreenBinding
import com.mykaimeal.planner.databinding.FragmentDropOffOptionsScreenBinding

class AddressMapFullScreenFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentAddressMapFullScreenBinding? = null
    private lateinit var mapView: MapView
    private var mapViewBundle: Bundle? = null
    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressMapFullScreenBinding.inflate(layoutInflater, container, false)

        // Load saved instance state
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(R.string.api_key.toString())

            mapView = binding!!.gmsMapView
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this@AddressMapFullScreenFragment)
        }

        initialize()
        return binding!!.root
    }

    private fun initialize() {

    }

    override fun onMapReady(gmap: GoogleMap) {
        mMap = gmap
        val newYork = LatLng(28.6070135, 77.4075354)
        mMap?.addMarker(MarkerOptions().position(newYork).title("Marker in New York"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(newYork, 12f))
    }

    // Manage MapView Lifecycle
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}
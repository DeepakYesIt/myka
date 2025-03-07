package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.FragmentCheckoutScreenBinding

class CheckoutScreenFragment : Fragment(),OnMapReadyCallback {
    private var binding: FragmentCheckoutScreenBinding?=null
    private lateinit var mapView: MapView
    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCheckoutScreenBinding.inflate(layoutInflater, container, false)

        // Load saved instance state
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(R.string.api_key.toString())
        }

        mapView = binding!!.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@CheckoutScreenFragment)

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.relSetHomes.setOnClickListener{
            addressDialog()
        }

        binding!!.relSetMeetAtDoor.setOnClickListener{
            findNavController().navigate(R.id.dropOffOptionsScreenFragment)
        }

        binding!!.relAddNumber.setOnClickListener{
            findNavController().navigate(R.id.addNumberVerifyFragment)
        }
    }

    private fun addressDialog() {
        val dialogMiles: Dialog = context?.let { Dialog(it) }!!
        dialogMiles.setContentView(R.layout.alert_dialog_addresses_popup)
        dialogMiles.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogMiles.setCancelable(false)
        dialogMiles.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val relDone = dialogMiles.findViewById<RelativeLayout>(R.id.relDone)
        dialogMiles.show()
        dialogMiles.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        relDone.setOnClickListener {
            dialogMiles.dismiss()
        }
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
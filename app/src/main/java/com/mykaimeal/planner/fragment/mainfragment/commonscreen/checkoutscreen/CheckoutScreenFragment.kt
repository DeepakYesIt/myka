package com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.SuperMarketListAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentCheckoutScreenBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.viewmodel.CheckoutScreenViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutScreenFragment : Fragment(),OnMapReadyCallback {
    private var binding: FragmentCheckoutScreenBinding?=null
    private lateinit var mapView: MapView
    private var mMap: GoogleMap? = null
    private var status:Boolean=false
    private lateinit var checkoutScreenViewModel: CheckoutScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCheckoutScreenBinding.inflate(layoutInflater, container, false)

        checkoutScreenViewModel = ViewModelProvider(requireActivity())[CheckoutScreenViewModel::class.java]

        // Load saved instance state
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle= savedInstanceState.getBundle("MapViewBundleKey")
//            mapViewBundle = savedInstanceState.getBundle("AIzaSyA-e6IRZ8axxpwrm1GEjlFOTzwb5KVQHgc")
        }

        mapView = binding!!.mapView
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this@CheckoutScreenFragment)

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        if (BaseApplication.isOnline(requireContext())){
            getCheckoutApi()
        }else{
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        binding!!.imageBackIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.relSetHomes.setOnClickListener{
            addressDialog()
//            findNavController().navigate(R.id.addressMapFullScreenFragment)
        }

        binding!!.textPayBtn.setOnClickListener{
            findNavController().navigate(R.id.addTipScreenFragment)
        }

        binding!!.relSetMeetAtDoor.setOnClickListener{
            findNavController().navigate(R.id.dropOffOptionsScreenFragment)
        }

        binding!!.relAddNumber.setOnClickListener{
            findNavController().navigate(R.id.addNumberVerifyFragment)
        }

        binding!!.tvAddCard.setOnClickListener{
            findNavController().navigate(R.id.paymentCreditDebitFragment)
        }

        binding!!.imageCheckRadio.setOnClickListener{
            if (status){
                status=false
                binding!!.imageCheckRadio.setImageResource(R.drawable.radio_uncheck_gray_icon)
            }else{
                binding!!.imageCheckRadio.setImageResource(R.drawable.radio_green_icon)
                status=true

            }
        }

       /* binding!!.mapView.setOnClickListener{
            findNavController().navigate(R.id.addressMapFullScreenFragment)
        }*/
    }

    private fun getCheckoutApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            checkoutScreenViewModel.getCheckoutScreenUrl{
                BaseApplication.dismissMe()
                handleApiCheckoutResponse(it)
            }
        }
    }

    private fun handleApiCheckoutResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessCheckoutResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessCheckoutResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, CheckoutScreenModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                if (apiModel.data != null) {
                    showDataInUI(apiModel.data)
                }
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

    private fun showDataInUI(data: CheckoutScreenModelData?) {

        if (data!!.phone!=null){
            binding!!.tvAddNumber.text=data.phone.toString()
            binding!!.tvAddNumber.setTextColor(Color.parseColor("#000000"))
        }

        if (data.note!!.pickup!=null){
            binding!!.tvSetDoorStep.text=data.note.pickup.toString()
        }

        if (data.note.description!=null){
            binding!!.tvDeliveryInstructions.text=data.note.description.toString()
            binding!!.tvDeliveryInstructions.setTextColor(Color.parseColor("#000000"))
        }

        if (data.subtotal!=null){
            binding!!.textSubTotalPrices.text=data.subtotal.toString()
        }

        if (data.bagfee!=null){
            binding!!.textBagFees.text=data.bagfee.toString()
        }


        if (data.delivery!=null){
            binding!!.textDeliveryPrice.text=data.delivery.toString()
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
        val newYork = LatLng(40.7128, -74.0060)
/*
        val newYork = LatLng(28.6070135, 77.4075354)
*/
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
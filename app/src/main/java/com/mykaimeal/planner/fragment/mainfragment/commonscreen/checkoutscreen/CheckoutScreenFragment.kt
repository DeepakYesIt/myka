package com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.AdapterCardPreferredItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.databinding.FragmentCheckoutScreenBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.viewmodel.CheckoutScreenViewModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen.model.GetCardMealMeModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen.model.GetCardMealMeModelData
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class CheckoutScreenFragment : Fragment(), OnMapReadyCallback,OnItemSelectListener {
    private var binding: FragmentCheckoutScreenBinding? = null
    private lateinit var mapView: MapView
    private var mMap: GoogleMap? = null
    private var status: Boolean = false
    private lateinit var checkoutScreenViewModel: CheckoutScreenViewModel
    private lateinit var adapterPaymentCreditDebitItem: AdapterCardPreferredItem
    private lateinit var sessionManagement: SessionManagement

    private var latitude: String? = ""
    private var longitude: String? = ""
    private var totalPrices: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutScreenBinding.inflate(layoutInflater, container, false)

        checkoutScreenViewModel = ViewModelProvider(requireActivity())[CheckoutScreenViewModel::class.java]

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

        mapView = binding!!.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

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

        if (sessionManagement.getUserAddress()!=""){
            binding!!.tvAddressNames.text=sessionManagement.getUserAddress().toString()
        }

        if (BaseApplication.isOnline(requireContext())) {
            getCheckoutApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        binding!!.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }


        binding!!.layEdit.setOnClickListener {

            findNavController().navigate(R.id.addressMapFullScreenFragment)
        }


        binding!!.relSetHomes.setOnClickListener {
            addressDialog()
        }

        binding!!.textPayBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("totalPrices",totalPrices)
            }
            findNavController().navigate(R.id.addTipScreenFragment,bundle)
        }

        binding!!.relSetMeetAtDoor.setOnClickListener {
            findNavController().navigate(R.id.dropOffOptionsScreenFragment)
        }

        binding!!.relAddNumber.setOnClickListener {
            findNavController().navigate(R.id.addNumberVerifyFragment)
        }

        binding!!.tvAddCard.setOnClickListener {
            findNavController().navigate(R.id.paymentCreditDebitFragment)
        }

        binding!!.imageCheckRadio.setOnClickListener {
            if (status) {
                status = false
                binding!!.imageCheckRadio.setImageResource(R.drawable.radio_uncheck_gray_icon)
            } else {
                binding!!.imageCheckRadio.setImageResource(R.drawable.radio_green_icon)
                status = true

            }
        }

        /* binding!!.mapView.setOnClickListener{
             findNavController().navigate(R.id.addressMapFullScreenFragment)
         }*/
    }

    private fun getCheckoutApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            checkoutScreenViewModel.getCheckoutScreenUrl {
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

        getCardMealMe()

    }


    private fun getCardMealMe() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchUserCardData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchUserCardData() {
//        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            checkoutScreenViewModel.getCardMealMeUrl { result ->
                BaseApplication.dismissMe()
                handleGetCardApiResponse(result)
            }
        }
    }

    private fun handleGetCardApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> processGetCardSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun processGetCardSuccessResponse(response: String) {
        try {
            val apiModel = Gson().fromJson(response, GetCardMealMeModel::class.java)
            Log.d("@@@ Response cardBank ", "message :- $response")
            if (apiModel.code == 200 && apiModel.success==true) {
                showDataInUi(apiModel.data)
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

    private fun showDataInUi(data: MutableList<GetCardMealMeModelData>?) {

        if (data!=null && data.size>0){
            binding!!.rcyDebitCreditCard.visibility=View.VISIBLE
            adapterPaymentCreditDebitItem = AdapterCardPreferredItem(requireContext(), data,  this,0)
            binding!!.rcyDebitCreditCard.adapter = adapterPaymentCreditDebitItem
        }else{
            binding!!.rcyDebitCreditCard.visibility=View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDataInUI(data: CheckoutScreenModelData?) {

        if (data!!.phone != null || data.country_code!=null) {
            binding!!.tvAddNumber.text = "("+data.country_code+")"+data.phone.toString()
            binding!!.tvAddNumber.setTextColor(Color.parseColor("#000000"))
        }

        if (data.Store!=null){
            binding!!.tvSuperMarketName.text=data.Store.toString()
        }

        if (data.note != null) {
            if (data.note.pickup != null) {
                binding!!.tvSetDoorStep.text = data.note.pickup.toString()
            }

            if (data.note.description != null) {
                binding!!.tvDeliveryInstructions.text = data.note.description.toString()
                binding!!.tvDeliveryInstructions.setTextColor(Color.parseColor("#000000"))
            }
        }

        if (data.net_total!=null){
            val roundedSubTotal = data.net_total.let {
                BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            binding!!.textSubTotalPrices.text= "$$roundedSubTotal"
        }

        if (data.tax != null) {
            val roundedBagFees = data.tax.let {
                BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            binding!!.textBagFees.text="$$roundedBagFees"
        }

        if (data.processing!=null){
            val roundedServices = data.processing.let {
                BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            binding!!.textServicesPrice.text= "$$roundedServices"
        }

        if (data.delivery != null) {
            val roundedDelivery = data.delivery.let {
                BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            binding!!.textDeliveryPrice.text = "$$roundedDelivery"
        }

        if (data.total != null) {
            val roundedTotal = data.total.let {
                BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
            totalPrices=roundedTotal.toString()
            binding!!.textTotalAmounts.text = "$$roundedTotal"
        }

        if (data.ingredient_count!=null){
            binding!!.tvItemsCount.text=data.ingredient_count.toString()+" Items"
        }


        data.let {
            // ✅ Load image with Glide
            Glide.with(requireActivity())
                .load(it.store_image)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility = View.GONE
                        return false
                    }
                })
                .into(binding!!.imageWelmart)
        }


    }

    private fun addressDialog() {
        val dialogMiles: Dialog = context?.let { Dialog(it) }!!
        dialogMiles.setContentView(R.layout.alert_dialog_addresses_popup)
        dialogMiles.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogMiles.setCancelable(false)
        dialogMiles.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val relDone = dialogMiles.findViewById<RelativeLayout>(R.id.relDone)
        dialogMiles.show()
        dialogMiles.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        relDone.setOnClickListener {
            dialogMiles.dismiss()
        }
    }

    override fun onMapReady(gmap: GoogleMap) {

        Log.d("Location latitude", "********$latitude")
        Log.d("Location longitude", "********$longitude")
        mMap = gmap

        val lat = latitude?.toDoubleOrNull() ?: 0.0  // Convert String to Double, default to 0.0 if null
        val lng = longitude?.toDoubleOrNull() ?: 0.0
        val newYork = LatLng(lat, lng)
        val customMarker = bitmapDescriptorFromVector(R.drawable.map_marker_icon,45,60) // Change with your drawable

/*        val newYork = LatLng(40.7128, -74.0060)
        val customMarker = bitmapDescriptorFromVector(R.drawable.current_location_marker,50,50) // Change with your drawable*/
//        val customMarker = bitmapDescriptorFromVector(R.drawable.marker_icon,50,50) // Change with your drawable
        mMap?.addMarker(
            MarkerOptions()
                .position(newYork)
                .icon(customMarker)
        )

        // 🔹 Disable map movement
        mMap?.uiSettings?.apply {
            isScrollGesturesEnabled = false  // ❌ Disable scrolling
            isZoomGesturesEnabled = false    // ❌ Disable zooming
            isTiltGesturesEnabled = false    // ❌ Disable tilt
            isRotateGesturesEnabled = false  // ❌ Disable rotation
        }


        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(newYork, 20f))
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int, width: Int, height: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(requireContext(), vectorResId)
        if (vectorDrawable == null) {
            return null
        }
        // Create a new bitmap with desired width and height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set bounds for the drawable
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
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

    override fun itemSelect(position: Int?, status: String?, type: String?) {

    }

}
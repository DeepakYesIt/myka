package com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.AdapterCardPreferredItem
import com.mykaimeal.planner.adapter.PlacesAutoCompleteAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentCheckoutScreenBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.addressmapfullscreen.model.AddAddressModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.model.CheckoutScreenModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.viewmodel.CheckoutScreenViewModel
import com.mykaimeal.planner.listener.OnPlacesDetailsListener
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.Place
import com.mykaimeal.planner.model.PlaceAPI
import com.mykaimeal.planner.model.PlaceDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

@AndroidEntryPoint
class CheckoutScreenFragment : Fragment(), OnMapReadyCallback,OnItemSelectListener {
    private var binding: FragmentCheckoutScreenBinding? = null
    private lateinit var mapView: MapView
    private var mMap: GoogleMap? = null
    private var status: Boolean = false
    private lateinit var checkoutScreenViewModel: CheckoutScreenViewModel
    private lateinit var adapterPaymentCreditDebitItem: AdapterCardPreferredItem
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationManager: LocationManager? = null
    private lateinit var sessionManagement: SessionManagement

    private var latitude: String? = ""
    private var longitude: String? = ""
    private var totalPrices: String? = ""
    private var statusTypes: String? = "Home"

    private lateinit var edtStreetName: EditText
    private lateinit var edtStreetNumber: EditText
    private lateinit var edtApartNumber: EditText
    private lateinit var edtCity: EditText
    private lateinit var edtStates: EditText
    private lateinit var edtPostalCode: EditText
    private lateinit var edtAddress: EditText
    private lateinit var tvAddress: AutoCompleteTextView
    private var address: String? = ""
    private var streetName: String? = ""
    private var streetNum: String? = ""
    private var apartNum: String? = ""
    private var city: String? = ""
    private var states: String? = ""
    private var zipcode: String? = ""
    private var country: String? = ""

    private lateinit var commonWorkUtils: CommonWorkUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutScreenBinding.inflate(layoutInflater, container, false)

        checkoutScreenViewModel = ViewModelProvider(requireActivity())[CheckoutScreenViewModel::class.java]

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        sessionManagement = SessionManagement(requireContext())
        commonWorkUtils = CommonWorkUtils(requireActivity())

        if (sessionManagement.getLatitude()!=""){
            latitude=sessionManagement.getLatitude().toString()
        }else{
            latitude="37.7767081"
        }

        if (sessionManagement.getLongitude()!=""){
            longitude=sessionManagement.getLongitude().toString()
        }else{
            longitude="-122.3947791"
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
    }


    @SuppressLint("SetTextI18n")
    private fun showDataInUI(data: CheckoutScreenModelData?) {

        if (data!!.phone != null || data.country_code!=null) {
            binding!!.tvAddNumber.text = "("+data.country_code+")"+data.phone.toString()
            binding!!.tvAddNumber.setTextColor(Color.parseColor("#000000"))
        }

        data.address?.let { address ->
            if (!address.type.isNullOrEmpty() &&
                !address.apart_num.isNullOrEmpty() &&
                !address.street_name.isNullOrEmpty() &&
                !address.city.isNullOrEmpty() &&
                !address.state.isNullOrEmpty() &&
                !address.country.isNullOrEmpty() &&
                !address.zipcode.isNullOrEmpty() &&
                !address.latitude.isNullOrEmpty() &&
                !address.longitude.isNullOrEmpty()
            ) {
                val fullAddress = listOf(
                    address.apart_num, address.street_name, address.city,
                    address.state, address.country, address.zipcode
                ).joinToString(" ")

                // Store in a String variable
                val storedAddress: String = fullAddress
                sessionManagement.setAddress(storedAddress.toString())
                sessionManagement.setLatitude(address.latitude.toString())
                sessionManagement.setLongitude(address.longitude.toString())

                // Set text in TextView
                binding?.tvAddressNames?.text = storedAddress
            }
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

        if (data.card!=null){
            binding!!.relCardDetails.visibility=View.VISIBLE
            if (data.card.card_num!=null){
                binding!!.tvCardNumber.text="**** **** **** "+data.card.card_num.toString()
            }
        }else{
            binding!!.relCardDetails.visibility=View.GONE
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
        dialogMiles.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }

        val placesApi = PlaceAPI.Builder().apiKey(apiKey).build(requireContext())

        val relDone = dialogMiles.findViewById<RelativeLayout>(R.id.relDone)
        val llSetWork = dialogMiles.findViewById<LinearLayout>(R.id.llSetWork)
        val llSetHome = dialogMiles.findViewById<LinearLayout>(R.id.llSetHome)
        val relTrialBtn = dialogMiles.findViewById<RelativeLayout>(R.id.relTrialBtn)
        tvAddress = dialogMiles.findViewById<AutoCompleteTextView>(R.id.tvAddress)
        dialogMiles.show()
        dialogMiles.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        llSetHome.setOnClickListener{
            statusTypes="Home"
            llSetHome.setBackgroundResource(R.drawable.outline_address_green_border_bg)
            llSetWork.setBackgroundResource(R.drawable.height_type_bg)
        }

        llSetWork.setOnClickListener{
            statusTypes="Work"
            llSetHome.setBackgroundResource(R.drawable.height_type_bg)
            llSetWork.setBackgroundResource(R.drawable.outline_address_green_border_bg)
        }

        relTrialBtn.setOnClickListener{
            dialogMiles.dismiss()
           /* if (BaseApplication.isOnline(requireContext())) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }*/
        }

    /*    tvAddress.setAdapter(PlacesAutoCompleteAdapter(requireContext(), placesApi))
        tvAddress.setOnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position) as Place
            tvAddress.setText(place.description)
            address=place.description
            getPlaceDetails(place.id, placesApi)*/


        relDone.setOnClickListener {
//            fullAddressDialog()
            dialogMiles.dismiss()
        }
    }

    private fun getCurrentLocation() {
        // Initialize Location manager
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
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
            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                // Initialize location
                val location = task.result
                // Check condition
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    tvAddress.text.clear()
                    requireActivity().runOnUiThread {
                        getAddressFromLocation(location.latitude, location.longitude)

                    }

                } else {
                    // When location result is null
                    val locationRequest =
                        LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1)

                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // Initialize
                            // location
                            val location1 = locationResult.lastLocation
                            latitude = location1!!.latitude.toString()
                            longitude = location1.longitude.toString()
                            tvAddress.text.clear()
                            requireActivity().runOnUiThread {
                                getAddressFromLocation(location1.latitude, location1.longitude)
                            }
                        }
                    }
                    // Request location updates
                    mFusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()!!
                    )

                }
            }
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }
    }

    private fun getPlaceDetails(placeId: String, placesApi: PlaceAPI) {
        placesApi.fetchPlaceDetails(placeId, object : OnPlacesDetailsListener {
            override fun onError(errorMessage: String) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
                try {
                    latitude = placeDetails.lat.toString()
                    longitude = placeDetails.lng.toString()
                    requireActivity().runOnUiThread {
                        getAddressFromLocation(placeDetails.lat, placeDetails.lng)
                    }

                } catch (e: Exception) {
                    BaseApplication.alertError(requireContext(), e.message, false)
                }
            }
        })
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                streetName = address.thoroughfare ?: "" // Street Name
                streetNum = address.subThoroughfare ?: "" // Street Number
                apartNum = address.premises ?: "" // Apartment Number
                city = address.subLocality ?: "" // City
                states = address.adminArea ?: "" // State/Province
                country = address.countryName ?: "" // Country
                zipcode = address.postalCode ?: "" // Zip Code

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


    private fun fullAddressDialog() {
        val dialogMiles: Dialog = context?.let { Dialog(it) }!!
        dialogMiles.setContentView(R.layout.alert_dialog_address_popup)
        dialogMiles.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogMiles.setCancelable(false)
        dialogMiles.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val relConfirm = dialogMiles.findViewById<RelativeLayout>(R.id.relConfirm)
        val imageCross = dialogMiles.findViewById<ImageView>(R.id.imageCross)
        edtStreetName = dialogMiles.findViewById(R.id.edtStreetName)
        edtStreetNumber = dialogMiles.findViewById(R.id.edtStreetNumber)
        edtApartNumber = dialogMiles.findViewById(R.id.edtApartNumber)
        edtCity = dialogMiles.findViewById(R.id.edtCity)
        edtStates = dialogMiles.findViewById(R.id.edtStates)
        edtPostalCode = dialogMiles.findViewById(R.id.edtPostalCode)
        edtAddress = dialogMiles.findViewById(R.id.edtAddress)
        dialogMiles.show()

        if (streetName != "") {
            edtStreetName.setText(streetName.toString())
        }

        if (streetNum != "") {
            edtStreetNumber.setText(streetNum.toString())
        }

        if (apartNum != "") {
            edtApartNumber.setText(apartNum.toString())
        }

        if (city != "") {
            edtCity.setText(city.toString())
        }

        if (states!=""){
            edtStates.setText(states.toString())
        }

        if (address != "") {
            edtAddress.setText(address.toString())
        }

        if (zipcode != "") {
            edtPostalCode.setText(zipcode.toString())
        }

        dialogMiles.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        relConfirm.setOnClickListener {
            if (BaseApplication.isOnline(requireContext())) {
                if (validate()) {
                    streetName=edtStreetName.text.toString().trim()
                    streetNum=edtStreetNumber.text.toString().trim()
                    apartNum=edtApartNumber.text.toString().trim()
                    city=edtCity.text.toString().trim()
                    states=edtStates.text.toString().trim()
                    address=edtAddress.text.toString().trim()
                    zipcode=edtPostalCode.text.toString().trim()
                    addFullAddressApi()
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
            dialogMiles.dismiss()
        }

        imageCross.setOnClickListener {
            dialogMiles.dismiss()
        }
    }


    private fun addFullAddressApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            checkoutScreenViewModel.addAddressUrl(
                {
                    BaseApplication.dismissMe()
                    handleApiAddAddressResponse(it)
                }, latitude, longitude, streetName, streetNum, apartNum, city,states, country, zipcode, "1", "", statusTypes
            )
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
            val apiModel = Gson().fromJson(data, AddAddressModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                getCheckoutApi()
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

    private fun validate(): Boolean {
        // Check if email/phone is empty
        if (edtStreetName.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.streetNameError, false)
            return false
        } else if (edtStreetNumber.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.streetNumberError, false)
            return false
        } else if (edtApartNumber.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.apartNumberError, false)
            return false
        } else if (edtCity.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.cityEnterError, false)
            return false
        } else if (edtStates.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.statesEnterError, false)
            return false
        }else if (edtAddress.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.addressError, false)
            return false
        } else if (edtPostalCode.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.postalCodeError, false)
            return false
        }
        return true
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
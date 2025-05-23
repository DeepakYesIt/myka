package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemSelectUnSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.AdapterSuperMarket
import com.mykaimeal.planner.adapter.CategoryProductAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentBasketDetailSuperMarketBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.model.BasketDetailsSuperMarketModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.model.BasketDetailsSuperMarketModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.model.Product
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.viewmodel.BasketDetailsSuperMarketViewModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Store
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse.SuperMarketModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class BasketDetailSuperMarketFragment : Fragment(), OnItemClickListener,
    OnItemSelectUnSelectListener {

    private lateinit var binding: FragmentBasketDetailSuperMarketBinding
    private lateinit var itemSectionAdapter: CategoryProductAdapter
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var adapter: AdapterSuperMarket? = null
    private var rcvBottomDialog: RecyclerView? = null
    private var products:MutableList<Product>?=null
    private lateinit var basketDetailsSuperMarketViewModel: BasketDetailsSuperMarketViewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var latitude = "0.0"
    private var longitude = "0.0"
    private var storeUid: String? = ""
    private var storeName: String? = ""

    private var stores: MutableList<Store>?=null
    private var clickStatus: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasketDetailSuperMarketBinding.inflate(layoutInflater, container, false)

        basketDetailsSuperMarketViewModel = ViewModelProvider(requireActivity())[BasketDetailsSuperMarketViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding.root
    }

    private fun getBasketDetailsApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketDetailsSuperMarketViewModel.getStoreProductUrl {
                BaseApplication.dismissMe()
                handleApiBasketDetailsResponse(it)
            }
        }
    }

    private fun initialize() {

        binding.relTescoMarket.setOnClickListener{
            bottomSheetDialog()
        }

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlGoToCheckout.setOnClickListener {
            if (clickStatus) {
                findNavController().navigate(R.id.checkoutScreenFragment)
            } else {
                showAlert(getString(R.string.available_products), false)
            }
        }

        if (BaseApplication.isOnline(requireActivity())){
            // This condition for check location run time permission
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 100
                )
            }
        }else{
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        if (BaseApplication.isOnline(requireActivity())){
            getBasketDetailsApi()
        }else{
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        // Initialize Location manager
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            // When location service is enabled
            // Get last location
            fusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                // Initialize location
                // Check condition
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    // When location result is not
                    // null set latitude
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                } else {
                    // When location result is null
                    // initialize location request
                    val locationRequest = LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10000)
                        .setFastestInterval(1000)
                    // Initialize location call back
                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // location
                            val location1 = locationResult.lastLocation
                            latitude = location1!!.latitude.toString()
                            longitude = location1.longitude.toString()

                        }
                    }
                    fusedLocationClient!!.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            }
        } else {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }
    }

    private fun handleApiBasketDetailsResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessBasketResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessBasketResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, BasketDetailsSuperMarketModel::class.java)
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

    private fun showDataInUI(data: BasketDetailsSuperMarketModelData?) {

        if (data!!.total!=null || data.total=="0.0"){
            clickStatus = true
            binding.textPrice.text=data.total.toString()
        }

        if (data.product!= null) {
            products=data.product
            binding.recyclerItemList.layoutManager = LinearLayoutManager(requireActivity())
            /* itemSectionAdapter = ItemSectionAdapter(data.product,this)
             binding.recyclerItemList.adapter = itemSectionAdapter  */

            itemSectionAdapter = CategoryProductAdapter(requireActivity(),data.product, this)
            binding.recyclerItemList.adapter = itemSectionAdapter
            /*    binding.rcyBreakfast.visibility=View.VISIBLE
                binding.tvBreakFast.visibility=View.VISIBLE
                yourRecipeAdapter = YourRecipeAdapter(data.Breakfast,requireActivity(), this,"Breakfast")
                binding.rcyBreakfast.adapter = yourRecipeAdapter*/
        } else {
            /*     binding.rcyBreakfast.visibility=View.GONE
                 binding.tvBreakFast.visibility=View.GONE*/
        }


        if (data.store != null) {
            if (data.store.image != null) {
                Glide.with(requireActivity())
                    .load(data.store.image)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.layProgess.root.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.layProgess.root.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.tescoLogoImage)
            } else {
                binding.layProgess.root.visibility = View.GONE
            }
        }
    }

    private fun bottomSheetDialog() {
        getSuperMarketsList()
    }

    private fun getSuperMarketsList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketDetailsSuperMarketViewModel.getSuperMarket({
                BaseApplication.dismissMe()
                handleMarketApiResponse(it)
            },latitude, longitude)
        }
    }

    private fun handleMarketApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleMarketSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleMarketSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuperMarketModel::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success==true) {
                showUIData(apiModel.data)
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

    private fun showUIData(data: MutableList<Store>?) {
        try {
            if (data!=null){
                bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
                bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_select_super_market_near_me)
                rcvBottomDialog = bottomSheetDialog!!.findViewById<RecyclerView>(R.id.rcvBottomDialog)
                bottomSheetDialog!!.show()
                adapter = AdapterSuperMarket(data, requireActivity(), this)
                rcvBottomDialog!!.adapter = adapter
            }

        }catch (e:Exception){
            showAlert(e.message, false)
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

      /*  if (status == "2") {
            findNavController().navigate(R.id.tescoCartItemFragmentFragment)
        }*/
    }

    override fun itemSelectUnSelect(id: Int?, status: String?, type: String?, position: Int?) {

        if (type=="Product"){
            if (status=="Plus"){
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, status)
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }else if (status=="Minus"){
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, status)
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else{
                val mainId:String= products!![position!!].id.toString()
                val productId:String= products!![position].pro_id.toString()
                val productName:String= products!![position].name.toString()
                val foodId:String= products!![position].food_id.toString()
                val schId:String= products!![position].sch_id.toString()
                val bundle = Bundle().apply {
                    putString("id",mainId)
                    putString("SwapProId",productId)
                    putString("SwapProName",productName)
                    putString("foodId",foodId)
                    putString("schId",schId)
                }
                findNavController().navigate(R.id.basketProductDetailsFragment,bundle)
            }
        }else if (type=="SuperMarket"){
            bottomSheetDialog!!.dismiss()

            storeUid = position?.let { stores?.get(it)?.store_uuid.toString() }
            storeName = position?.let { stores?.get(it)?.store_name.toString() }

            if (BaseApplication.isOnline(requireActivity())) {
                selectSuperMarketApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun selectSuperMarketApi() {
        lifecycleScope.launch {
            basketDetailsSuperMarketViewModel.selectStoreProductUrl({
                BaseApplication.dismissMe()
                handleSelectSupermarketApiResponse(it)
            }, storeName, storeUid)
        }
    }

    private fun handleSelectSupermarketApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuperMarketResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuperMarketResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                getBasketDetailsApi()
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

    private fun removeAddIngServing(position: Int?, type: String) {
        val item= position?.let { products?.get(it) }
        if (type.equals("Plus",true) || type.equals("Minus",true)) {
            var count = item?.sch_id
            val foodId= item?.food_id
            count = when (type.lowercase()) {
                "plus" -> count!! + 1
                "minus" -> count!! - 1
                else -> count // No change if `apiType` doesn't match
            }
            increaseIngRecipe(foodId,count.toString(),item,position)
        }
    }

    private fun increaseIngRecipe(foodId: String?, quantity: String, item: Product?, position: Int?) {
//        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketDetailsSuperMarketViewModel.basketIngIncDescUrl({
                BaseApplication.dismissMe()
                handleApiIngResponse(it,item,quantity,position)
            },foodId,quantity)
        }
    }

    private fun handleApiIngResponse(result: NetworkResult<String>, item: Product?, quantity: String, position: Int?) {
        when (result) {
            is NetworkResult.Success -> handleSuccessIngResponse(result.data.toString(),item,quantity,position)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessIngResponse(data: String, item: Product?, quantity: String, position: Int?) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                item?.sch_id = quantity.toInt()
                if (item!= null) {
                    products?.set(position!!, item)
                }
                // Update the adapter
                if (products != null) {
                    itemSectionAdapter.updateList(products!!)
                }

                getBasketDetailsApi()
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


}
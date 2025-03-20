package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.AdapterProductsDetailsSelectItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentBasketProductDetailsBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketDetailsModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketDetailsModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketProductsDetailsModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketProductsDetailsModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.ProductSwapSuccessModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.viewmodel.BasketProductsDetailsViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BasketProductDetailsFragment : Fragment(), OnItemSelectListener {
    private lateinit var binding: FragmentBasketProductDetailsBinding
    private var adapterProductsDetailsSelectItem: AdapterProductsDetailsSelectItem? = null
    private lateinit var basketProductsDetailsViewModel: BasketProductsDetailsViewModel
    private var proId: String = ""
    private var proName: String = ""
    private var id: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasketProductDetailsBinding.inflate(layoutInflater, container, false)

        proId = arguments?.getString("SwapProId", "").toString()
        proName = arguments?.getString("SwapProName", "").toString()
        id = arguments?.getString("id", "").toString()

        basketProductsDetailsViewModel =
            ViewModelProvider(requireActivity())[BasketProductsDetailsViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

//        getProductsUrl

        initialize()
//        productDetailsModel()

        return binding.root
    }

    private fun initialize() {
        binding.imgBackRecipeDetails.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDetails.setOnClickListener{
            findNavController().navigate(R.id.basketIngredientsDetailsFragment)
        }

        if (BaseApplication.isOnline(requireContext())) {
            getProductsDetailsApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }


    private fun getProductsDetailsApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketProductsDetailsViewModel.getProductsDetailsUrl({
                BaseApplication.dismissMe()
                handleApiProductsDetailsApiResponse(it)
            }, proId)
        }
    }

    private fun handleApiProductsDetailsApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessProductsDetailsResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessProductsDetailsResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, BasketDetailsModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                if (apiModel.data != null) {
                    showDataProductsInUI(apiModel.data)
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

        getProductsRelatedApi()

    }

    private fun showDataProductsInUI(data: BasketDetailsModelData?) {

        if (data != null) {
            if (data.name != null) {
                binding.tvProductName.text = data.name.toString()
            }

            if (data.price != null) {
                binding.tvProductsprices.text = data.price.toString()
            }

            // ✅ Load image with Glide
            Glide.with(requireActivity())
                .load(data.image)
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
                .into(binding.imageSuperMarket)
        }

    }

    private fun getProductsRelatedApi() {
//            BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketProductsDetailsViewModel.getProductsUrl({
                BaseApplication.dismissMe()
                handleApiProductsDetailsResponse(it)
            }, proName)
        }
    }


    private fun handleApiProductsDetailsResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessProductsResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessProductsResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, BasketProductsDetailsModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
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

    private fun showDataInUI(data: MutableList<BasketProductsDetailsModelData>) {

        if (data.size != null && data.size > 0) {
            binding.relNoProductsFound.visibility = View.GONE
            adapterProductsDetailsSelectItem =
                AdapterProductsDetailsSelectItem(data, requireActivity(), this)
            binding.rcyProductItems.adapter = adapterProductsDetailsSelectItem
        } else {
            binding.relNoProductsFound.visibility = View.VISIBLE
        }
    }

    override fun itemSelect(position: Int?, productId: String?, type: String?) {

        if (type=="products"){
            if (BaseApplication.isOnline(requireContext())) {
                getProductsSwapApi(productId)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

    }

    private fun getProductsSwapApi(productId: String?) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketProductsDetailsViewModel.getSelectProductsUrl({
                BaseApplication.dismissMe()
                handleApiProductsSwapApiResponse(it)
            }, id,productId)
        }
    }


    private fun handleApiProductsSwapApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleApiProductsSwapApiResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleApiProductsSwapApiResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, ProductSwapSuccessModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                findNavController().navigateUp()
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

        getProductsRelatedApi()

    }

    /*   private fun productDetailsModel() {
           val dataList = ArrayList<DataModel>()
           val data1 = DataModel()
           val data2 = DataModel()
           val data3 = DataModel()
           val data4 = DataModel()
           val data5 = DataModel()
           val data6 = DataModel()
           val data7 = DataModel()

           data1.title = "Ketchup"
           data1.isOpen = false
           data1.type = "ProductDetails"
           data1.quantity="70 ml"
           data1.price="24"
           data1.image = R.drawable.apple_ing_image

           data2.title = "Milk"
           data2.isOpen = false
           data2.type = "ProductDetails"
           data2.quantity="70 ml"
           data2.price="20"
           data2.image = R.drawable.avacado_ing_image

           data3.title = "Pasta"
           data3.isOpen = false
           data3.type = "ProductDetails"
           data3.quantity="70 ml"
           data3.price="22"
           data3.image = R.drawable.orange_ing_image

           data4.title = "Egg"
           data4.isOpen = false
           data4.type = "ProductDetails"
           data4.quantity="72 ml"
           data4.price="24"
           data4.image = R.drawable.grapes_ing_image

           data5.title = "Pasta"
           data5.isOpen = false
           data5.type = "ProductDetails"
           data5.quantity="74 ml"
           data5.price="26"
           data5.image = R.drawable.banana_ing_image

           data6.title = "Tesco Mustard Seeds"
           data6.isOpen = false
           data6.type = "ProductDetails"
           data6.quantity="76 ml"
           data6.price="27"
           data6.image = R.drawable.guava_ing_image

           data7.title = "Ketchup"
           data7.isOpen = false
           data7.type = "ProductDetails"
           data7.quantity="70 ml"
           data7.price="28"
           data7.image = R.drawable.watermelon_ing_image


           dataList.add(data1)
           dataList.add(data2)
           dataList.add(data3)
           dataList.add(data4)
           dataList.add(data5)
           dataList.add(data6)
           dataList.add(data7)

           adapterProductsDetailsSelectItem = AdapterProductsDetailsSelectItem(dataList, requireActivity())
           binding.rcyProductItems.adapter = adapterProductsDetailsSelectItem
       }*/

}
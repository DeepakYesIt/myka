package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Ingredient
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class BasketProductDetailsFragment : Fragment(), OnItemSelectListener {
    private lateinit var binding: FragmentBasketProductDetailsBinding
    private var adapterProductsDetailsSelectItem: AdapterProductsDetailsSelectItem? = null
    private lateinit var basketProductsDetailsViewModel: BasketProductsDetailsViewModel
    private var proId: String = ""
    private var proName: String = ""
    private var id: String = ""
    private var foodId: String = ""
    private var schId: String = ""
    private var basketProductsDetailsModelData: MutableList<BasketProductsDetailsModelData>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasketProductDetailsBinding.inflate(layoutInflater, container, false)

        proId = arguments?.getString("SwapProId", "").toString()
        proName = arguments?.getString("SwapProName", "").toString()
        id = arguments?.getString("id", "").toString()
        foodId = arguments?.getString("foodId", "").toString()
        schId = arguments?.getString("schId", "").toString()

        basketProductsDetailsViewModel =
            ViewModelProvider(requireActivity())[BasketProductsDetailsViewModel::class.java]

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

    private fun initialize() {

        binding.imgBackRecipeDetails.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDetails.setOnClickListener {
            val bundle = Bundle().apply {
                putString("SwapProId", proId)
                putString("SwapProName", proName)
                putString("foodId", foodId)
                putString("schId", schId)
            }
            findNavController().navigate(R.id.basketIngredientsDetailsFragment, bundle)
        }

        if (BaseApplication.isOnline(requireContext())) {
            getProductsDetailsApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        binding.etIngDislikesSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val query = editable.toString().trim()
                if (query.isNotEmpty()) {
                        filterIngredients(query)
                } else {
                    resetLists()
                }
            }
        })
    }

    private fun filterIngredients(editText: String) {
        val filteredList: MutableList<BasketProductsDetailsModelData> =
            java.util.ArrayList<BasketProductsDetailsModelData>()
        for (item in basketProductsDetailsModelData!!) {
            if (item.name!!.lowercase().contains(editText.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.size > 0) {
            adapterProductsDetailsSelectItem!!.filterList(filteredList)
            binding.rcyProductItems.visibility = View.VISIBLE
            binding.relNoProductsFound.visibility = View.GONE
        } else {
            binding.rcyProductItems.visibility = View.GONE
            binding.relNoProductsFound.visibility = View.VISIBLE
        }
    }

    private fun resetLists() {
        binding.rcyProductItems.visibility = View.VISIBLE
        binding.relNoProductsFound.visibility = View.GONE
        basketProductsDetailsModelData?.let { adapterProductsDetailsSelectItem!!.submitList(it) } // Reset recipe list

    }


    private fun getProductsDetailsApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketProductsDetailsViewModel.getProductsDetailsUrl({
                BaseApplication.dismissMe()
                handleApiProductsDetailsApiResponse(it)
            }, proId, proName, foodId, schId)
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

            if (data.formatted_price != null) {
                binding.tvProductsprices.text = data.formatted_price.toString()
            }

            if (data.image != null) {
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
            } else {
                binding.layProgess.root.visibility = View.GONE
            }
        }
    }

    private fun getProductsRelatedApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketProductsDetailsViewModel.getProductsUrl({
                BaseApplication.dismissMe()
                handleApiProductsDetailsResponse(it)
            }, proName, foodId, schId)
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
        if (data.size > 0) {
            basketProductsDetailsModelData = data
            binding.relNoProductsFound.visibility = View.GONE
            adapterProductsDetailsSelectItem = AdapterProductsDetailsSelectItem(data, requireActivity(), this)
            binding.rcyProductItems.adapter = adapterProductsDetailsSelectItem
        } else {
            binding.relNoProductsFound.visibility = View.VISIBLE
        }
    }

    override fun itemSelect(position: Int?, productId: String?, type: String?) {

        if (type == "products") {
            if (BaseApplication.isOnline(requireContext())) {
                getProductsSwapApi(productId, position.toString())
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        } else if (type == "swap") {
            val bundle = Bundle().apply {
                putString("SwapProId", productId)
                putString("SwapProName", position?.let { basketProductsDetailsModelData?.get(it)?.name })
                putString("foodId", position?.let { basketProductsDetailsModelData?.get(it)?.food_id })
                putString("schId", position?.let { basketProductsDetailsModelData?.get(it)?.sch_id.toString() })
            }
            findNavController().navigate(R.id.basketIngredientsDetailsFragment, bundle)
        } else if (type == "Ingredients") {
            if (productId == "Minus") {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else if (productId == "Plus") {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

        }
    }

    private fun removeAddIngServing(position: Int?, type: String) {
        val item = position?.let { basketProductsDetailsModelData?.get(it) }
        if (type.equals("plus", true) || type.equals("minus", true)) {
            var count = item?.sch_id
            val foodId = item?.food_id
            count = when (type.lowercase()) {
                "plus" -> count!! + 1
                "minus" -> count!! - 1
                else -> count // No change if `apiType` doesn't match
            }
            increaseIngRecipe(foodId, count.toString(), item, position)
        }
    }


    private fun increaseIngRecipe(
        foodId: String?, quantity: String, item: BasketProductsDetailsModelData?, position: Int?
    ) {
        lifecycleScope.launch {
            basketProductsDetailsViewModel.basketIngIncDescUrl({
                BaseApplication.dismissMe()
                handleApiIngResponse(it, item, quantity, position)
            }, foodId, quantity)
        }
    }

    private fun handleApiIngResponse(
        result: NetworkResult<String>,
        item: BasketProductsDetailsModelData?,
        quantity: String,
        position: Int?
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessIngResponse(
                result.data.toString(),
                item,
                quantity,
                position
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessIngResponse(
        data: String,
        item: BasketProductsDetailsModelData?,
        quantity: String,
        position: Int?
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {

                if (quantity != "0") {
                    // Toggle the is_like value
                    item?.sch_id = quantity.toInt()
                    if (item != null) {
                        basketProductsDetailsModelData?.set(position!!, item)
                    }
                    // Update the adapter
                    if (basketProductsDetailsModelData != null) {
                        adapterProductsDetailsSelectItem?.updateList(basketProductsDetailsModelData!!)
                    }
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

    private fun getProductsSwapApi(productId: String?, schId: String) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketProductsDetailsViewModel.getSelectProductsUrl({
                BaseApplication.dismissMe()
                handleApiProductsSwapApiResponse(it)
            }, id, productId, schId)
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
    }

}
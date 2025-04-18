package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentBaseketIngredientsDetailsBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketDetailsModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.model.BasketDetailsModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.viewmodel.BasketProductsDetailsViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasketIngredientsDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBaseketIngredientsDetailsBinding
    private lateinit var basketProductsDetailsViewModel: BasketProductsDetailsViewModel
    private var proId: String = ""
    private var proName: String = ""
    private var foodId: String = ""
    private var schId: String = ""
    private var quantity: Int = 1
    private var type: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBaseketIngredientsDetailsBinding.inflate(inflater, container, false)

        proId = arguments?.getString("SwapProId", "") ?: ""
        proName = arguments?.getString("SwapProName", "") ?: ""
        foodId = arguments?.getString("foodId", "") ?: ""
        schId = arguments?.getString("schId", "") ?: ""

        basketProductsDetailsViewModel = ViewModelProvider(requireActivity())[BasketProductsDetailsViewModel::class.java]

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

        binding.relDoneBtn.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                removeAddIngServing()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
            /*findNavController().navigateUp()*/
        }

        binding.relBack.setOnClickListener {
            findNavController().navigateUp()
        }

        if (BaseApplication.isOnline(requireContext())) {
            getProductsDetailsApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        binding.tvRemoveBasket.setOnClickListener {
            type = "delete"
            if (BaseApplication.isOnline(requireActivity())) {
                removeAddIngServing()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }


        binding.imageMinusIcon.setOnClickListener {
            type = "minus"
            if (quantity > 1) {
                quantity--
                updateValue()
            } else {
                Toast.makeText(requireActivity(), ErrorMessage.servingError, Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.imageAddIcon.setOnClickListener {
            type = "plus"
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }
    }

    private fun removeAddIngServing() {
        val count = if (type.equals("plus", true) || type.equals("minus", true)) {
            binding.textCount.text.toString().trim().toIntOrNull() ?: 0
        } else {
            0
        }
        increaseIngRecipe(count.toString())
    }

    @SuppressLint("DefaultLocale")
    private fun updateValue() {
        binding.textCount.text = String.format("%02d", quantity)
    }

    private fun increaseIngRecipe(quantity: String) {
        lifecycleScope.launch {
            basketProductsDetailsViewModel.basketIngIncDescUrl({
                BaseApplication.dismissMe()
                handleApiIngResponse(it)
            }, foodId, quantity)
        }
    }

    private fun handleApiIngResponse(
        result: NetworkResult<String>
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessIngResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessIngResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (type == "delete") {
                    findNavController().navigate(R.id.basketScreenFragment)
                } else {
                    findNavController().navigateUp()
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

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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
    }

    private fun showDataProductsInUI(data: BasketDetailsModelData?) {

        if (data != null) {
            if (data.name != null) {
                binding.tvIngredientsName.text = data.name.toString()
            }

            if (data.sch_id != null) {
                binding.textCount.text = data.sch_id.toString()
                quantity = data.sch_id
            }

            if (data.unit_size != null) {
                binding.tvQuantity.text = data.unit_size.toString()
            }

            if (data.formatted_price != null) {
                binding.tvActualPrices.text = data.formatted_price.toString()
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
                    .into(binding.imageData)
            } else {
                binding.layProgess.root.visibility = View.GONE
            }
        }
    }
}
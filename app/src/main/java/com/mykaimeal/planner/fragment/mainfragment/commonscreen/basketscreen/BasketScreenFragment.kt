package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemClickedListener
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.SuperMarketListAdapter
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterGetAddressItem
import com.mykaimeal.planner.adapter.IngredientsAdapter
import com.mykaimeal.planner.adapter.BasketYourRecipeAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentBasketScreenBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.BasketScreenModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.BasketScreenModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.GetAddressListModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.GetAddressListModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Ingredient
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Recipes
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.viewmodel.BasketScreenViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class BasketScreenFragment : Fragment(), OnItemClickListener, OnItemSelectListener,
    OnItemClickedListener {
    private lateinit var binding: FragmentBasketScreenBinding
    private var adapter: SuperMarketListAdapter? = null
    private var adapterGetAddressItem: AdapterGetAddressItem? = null
    private var adapterRecipe: BasketYourRecipeAdapter? = null
    private lateinit var adapterIngredients: IngredientsAdapter
    private lateinit var basketScreenViewModel: BasketScreenViewModel
    private var rcySavedAddress: RecyclerView? = null
    private var recipe: MutableList<Recipes>?=null
    private var ingredientList: MutableList<Ingredient>?=null
    private var storeUid:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBasketScreenBinding.inflate(layoutInflater, container, false)
        

        (activity as? MainActivity)?.binding?.apply {
            llIndicator.visibility = View.GONE
            llBottomNavigation.visibility = View.GONE
        }

        basketScreenViewModel = ViewModelProvider(requireActivity())[BasketScreenViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        if (BaseApplication.isOnline(requireActivity())){
            getBasketList()
        }else{
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        initialize()

        return binding.root
    }

    private fun initialize() {

        binding.textSeeAll1.setOnClickListener {
            findNavController().navigate(R.id.superMarketsNearByFragment)
        }

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.textSeeAll2.setOnClickListener {
            findNavController().navigate(R.id.basketYourRecipeFragment)
        }

        binding.textShoppingList.setOnClickListener {
            findNavController().navigate(R.id.shoppingListFragment)
        }

        binding.textConfirmOrder.setOnClickListener {
            val bundle = Bundle().apply {
                putString("storeUId",storeUid)
            }
            findNavController().navigate(R.id.basketDetailSuperMarketFragment,bundle)
        }

    }

    private fun getAddressList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketScreenViewModel.getAddressUrl {
                BaseApplication.dismissMe()
                handleApiGetAddressResponse(it)
            }
        }
    }


    private fun getBasketList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketScreenViewModel.getBasketUrl({
                BaseApplication.dismissMe()
                handleApiBasketResponse(it)
            },"014d3d2e-ad5d-4b00-9198-af07acce2f3a")
        }
    }

    private fun handleApiBasketResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessBasketResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessBasketResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, BasketScreenModel::class.java)
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


    private fun handleApiGetAddressResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessGetAddressResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessGetAddressResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, GetAddressListModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
                    if (apiModel.data != null && apiModel.data.size > 0) {
                        showDataInAddressUI(apiModel.data)
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

    private fun showDataInAddressUI(data: MutableList<GetAddressListModelData>?) {

        adapterGetAddressItem = AdapterGetAddressItem(data, requireActivity(), this)
        rcySavedAddress!!.adapter = adapterGetAddressItem

    }

    @SuppressLint("SetTextI18n")
    private fun showDataInUI(data: BasketScreenModelData) {

        if (data.billing!=null){
            if (data.billing.recipes!=null){
                binding.textRecipeCount.text=data.billing.recipes.toString()
            }

            if (data.billing.net_total!=null){
                val roundedNetTotal = data.billing.net_total.let {
                    BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
                }
                binding.textNetTotalProduct.text=roundedNetTotal.toString()
            }

            if (data.billing.total!=null){
                val roundedTotal = data.billing.total.let {
                    BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
                }
                binding.textTotalAmount.text= "$$roundedTotal*"
            }
        }

        if (data.stores != null && data.stores.size > 0) {
            binding.rlSuperMarket.visibility=View.VISIBLE
            adapter = SuperMarketListAdapter(data.stores, requireActivity(), this,0)
            binding.rcvSuperMarket.adapter = adapter
        }else{
            binding.rlSuperMarket.visibility=View.GONE
        }

        if (data.recipe != null && data.recipe.size > 0) {
            binding.rlYourRecipes.visibility=View.VISIBLE
            recipe=data.recipe
            adapterRecipe = BasketYourRecipeAdapter(data.recipe, requireActivity(), this)
            binding.rcvYourRecipes.adapter = adapterRecipe
        }else{
            binding.rlYourRecipes.visibility=View.GONE
        }

        if (data.ingredient != null && data.ingredient.size > 0) {
            ingredientList=data.ingredient
            binding.rlIngredients.visibility=View.VISIBLE
            adapterIngredients = IngredientsAdapter(data.ingredient, requireActivity(), this)
            binding.rcvIngredients.adapter = adapterIngredients
        }else{
            binding.rlIngredients.visibility=View.GONE
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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
        rcySavedAddress = dialogMiles.findViewById(R.id.rcySavedAddress)
        dialogMiles.show()

        getAddressList()

        dialogMiles.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        relDone.setOnClickListener {
            getBasketList()
            dialogMiles.dismiss()
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
      /*  if (status == "2") {
            removeRecipeBasketDialog(status)
        }*/
    }


    private fun removeRecipeBasketDialog(recipeId: String?,position:Int?) {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_remove_recipe_basket)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val tvDialogCancelBtn = dialogAddItem.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogRemoveBtn = dialogAddItem.findViewById<TextView>(R.id.tvDialogRemoveBtn)
        dialogAddItem.show()
        dialogAddItem.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogAddItem.dismiss()
        }

        tvDialogRemoveBtn.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                removeBasketRecipeApi(recipeId.toString(), dialogAddItem,position)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun removeBasketRecipeApi(recipeId: String, dialogRemoveDay: Dialog,position:Int?) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketScreenViewModel.removeBasketUrlApi({
                BaseApplication.dismissMe()
                handleApiRemoveBasketResponse(it,position,dialogRemoveDay)
            },recipeId)
        }
    }

    private fun handleApiRemoveBasketResponse(result: NetworkResult<String>,position:Int?,dialogRemoveDay: Dialog) {
        when (result) {
            is NetworkResult.Success -> handleSuccessRemoveBasketResponse(result.data.toString(),position,dialogRemoveDay)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessRemoveBasketResponse(data: String,position:Int?,dialogRemoveDay: Dialog) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                dialogRemoveDay.dismiss()
                if (recipe!=null){
                    recipe!!.removeAt(position!!)
                }

                // Update the adapter
                if (recipe != null) {
                    adapterRecipe?.updateList(recipe)
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


    override fun itemSelect(position: Int?, recipeId: String?, type: String?) {

        if (type=="YourRecipe"){
            if (recipeId=="Minus"){
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddRecipeServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }else if (recipeId=="Plus"){
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddRecipeServing(position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }else{
                removeRecipeBasketDialog(recipeId,position)
            }
        }else if (type=="SuperMarket"){
            storeUid=recipeId
        }else {
            if (recipeId=="Minus"){
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }else if (recipeId=="Plus"){
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing( position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }

    }

    private fun removeAddRecipeServing(position: Int?, type: String) {
        val item= position?.let { recipe?.get(it) }
        if (type.equals("plus",true) || type.equals("minus",true)) {
            var count = item?.serving?.toInt()
            val uri= item?.uri
            count = when (type.lowercase()) {
                "plus" -> count!! + 1
                "minus" -> count!! - 1
                else -> count // No change if `apiType` doesn't match
            }
            increaseQuantityRecipe(uri,count.toString(),item,position)
        }
    }


    private fun removeAddIngServing(position: Int?, type: String) {
        val item= position?.let { ingredientList?.get(it) }
        if (type.equals("plus",true) || type.equals("minus",true)) {
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

    private fun increaseIngRecipe(foodId: String?, quantity: String, item: Ingredient?, position: Int?) {
        lifecycleScope.launch {
            basketScreenViewModel.basketIngIncDescUrl({
                BaseApplication.dismissMe()
                handleApiIngResponse(it,item,quantity,position)
            },foodId,quantity)
        }
    }

    private fun handleApiIngResponse(result: NetworkResult<String>, item: Ingredient?, quantity: String, position: Int?) {
        when (result) {
            is NetworkResult.Success -> handleSuccessIngResponse(result.data.toString(),item,quantity,position)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessIngResponse(data: String, item: Ingredient?, quantity: String, position: Int?) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                item?.sch_id = quantity.toInt()
                if (item!= null) {
                    ingredientList?.set(position!!, item)
                }
                // Update the adapter
                if (ingredientList != null) {
                    adapterIngredients.updateList(ingredientList!!)
                }

                getBasketList()
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

    private fun increaseQuantityRecipe(uri: String?, quantity: String, item: Recipes?, position: Int?) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketScreenViewModel.basketYourRecipeIncDescUrl({
                BaseApplication.dismissMe()
                handleApiQuantityResponse(it,item,quantity,position)
            },uri,quantity)
        }
    }

    private fun handleApiQuantityResponse(result: NetworkResult<String>, item: Recipes?, quantity: String, position: Int?) {
        when (result) {
            is NetworkResult.Success -> handleSuccessQuantityResponse(result.data.toString(),item,quantity,position)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessQuantityResponse(data: String, item: Recipes?, quantity: String, position: Int?) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                item?.serving = quantity.toInt().toString()
                if (item != null) {
                    recipe?.set(position!!, item)
                }
                // Update the adapter
                if (recipe != null) {
                    adapterRecipe?.updateList(recipe)
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


    override fun itemClicked(position: Int?, list: MutableList<String>?, status: String?, type: String?) {

    }

}
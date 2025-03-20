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
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Recipes
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.viewmodel.BasketScreenViewModel
import com.mykaimeal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class BasketScreenFragment : Fragment(), OnItemClickListener, OnItemSelectListener,
    OnItemClickedListener {
    private var binding: FragmentBasketScreenBinding? = null
    private var adapter: SuperMarketListAdapter? = null
    private var adapterGetAddressItem: AdapterGetAddressItem? = null
    private var adapterRecipe: BasketYourRecipeAdapter? = null
    private lateinit var adapterIngredients: IngredientsAdapter
    private lateinit var basketScreenViewModel: BasketScreenViewModel
    private var rcySavedAddress: RecyclerView? = null
    private var recipe: MutableList<Recipes>?=null
    private var storeUid:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBasketScreenBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.GONE

        basketScreenViewModel =
            ViewModelProvider(requireActivity())[BasketScreenViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        /*n
                shoppingPreferencesDialog()
        */
//        addressDialog()

        getBasketList()
        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.textSeeAll1.setOnClickListener {
            findNavController().navigate(R.id.superMarketsNearByFragment)
        }

        binding!!.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.textSeeAll2.setOnClickListener {
            findNavController().navigate(R.id.basketYourRecipeFragment)
        }

        binding!!.textShoppingList.setOnClickListener {
            findNavController().navigate(R.id.shoppingListFragment)
        }

        binding!!.textConfirmOrder.setOnClickListener {
            val bundle = Bundle().apply {
                putString("storeUid","")
            }
            findNavController().navigate(R.id.basketDetailSuperMarketFragment,bundle)
//             findNavController().navigate(R.id.tescoCartItemFragmentFragment)
        }

     /*   binding!!.textSeeAll3.setOnClickListener {
            findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
        }*/

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

    private fun showDataInUI(data: BasketScreenModelData) {

        if (data.billing!=null){
            if (data.billing.recipes!=null){
                binding!!.textRecipeCount.text=data.billing.recipes.toString()
            }

            if (data.billing.net_total!=null){
                binding!!.textNetTotalProduct.text=data.billing.net_total.toString()
            }

            if (data.billing.tax!=null){
                binding!!.textTaxPrice.text="$"+data.billing.tax.toString()
            }

            if (data.billing.delivery!=null){
                binding!!.textDeliveyPrice.text="$"+data.billing.delivery.toString()
            }

            if (data.billing.processing!=null){
                val roundedQuantity = data.billing.processing.let {
                    BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble()
                }
                binding!!.textProcessingAmount.text=roundedQuantity.toString()+"%"
            }

            if (data.billing.total!=null){
                binding!!.textTotalAmount.text="$"+data.billing.total.toString()+"*"
            }

        }

        if (data.stores != null && data.stores.size > 0) {
            binding!!.rlSuperMarket.visibility=View.VISIBLE
            adapter = SuperMarketListAdapter(data.stores, requireActivity(), this,0)
            binding!!.rcvSuperMarket.adapter = adapter
        }else{
            binding!!.rlSuperMarket.visibility=View.GONE
        }

        if (data.recipe != null && data.recipe.size > 0) {
            binding!!.rlYourRecipes.visibility=View.VISIBLE
            recipe=data.recipe
            adapterRecipe = BasketYourRecipeAdapter(data.recipe, requireActivity(), this)
            binding!!.rcvYourRecipes.adapter = adapterRecipe
        }else{
            binding!!.rlYourRecipes.visibility=View.GONE
        }

        if (data.ingredient != null && data.ingredient.size > 0) {
            binding!!.rlIngredients.visibility=View.VISIBLE
            adapterIngredients = IngredientsAdapter(data.ingredient, requireActivity(), this)
            binding!!.rcvIngredients.adapter = adapterIngredients
        }else{
            binding!!.rlIngredients.visibility=View.GONE
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

    /*    private fun shoppingPreferencesDialog() {
            val dialogMiles: Dialog = context?.let { Dialog(it) }!!
            dialogMiles.setContentView(R.layout.alert_dialog_shopping_preferences)
            dialogMiles.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        }*/

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
//            dialogAddItem.dismiss()
        }
    }


    private fun removeBasketRecipeApi(recipeId: String, dialogRemoveDay: Dialog,position:Int?) {
        BaseApplication.showMe(requireActivity())
        lifecycleScope.launch {
            basketScreenViewModel.removeBasketUrlApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val cookedModel = gson.fromJson(it.data, CookedTabModel::class.java)
                        if (cookedModel.code == 200 && cookedModel.success) {
                            if (recipe!=null){
                                recipe!!.removeAt(position!!)
                            }
                            dialogRemoveDay.dismiss()
                        } else {
                            if (cookedModel.code == ErrorMessage.code) {
                                showAlert(cookedModel.message, true)
                            } else {
                                showAlert(cookedModel.message, false)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlert(it.message, false)
                    }

                    else -> {
                        showAlert(it.message, false)
                    }
                }
            }, recipeId)
        }
    }

    override fun itemSelect(position: Int?, recipeId: String?, type: String?) {

        if (type=="YourRecipe"){
            removeRecipeBasketDialog(recipeId,position)
        }else if (type=="SuperMarket"){
            storeUid=recipeId
        }
        /*findNavController().navigate(R.id.basketDetailSuperMarketFragment)*/

    }

    override fun itemClicked(position: Int?, list: MutableList<String>?, status: String?, type: String?) {

    }

}
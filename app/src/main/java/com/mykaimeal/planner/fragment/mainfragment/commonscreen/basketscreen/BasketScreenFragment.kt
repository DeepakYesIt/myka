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
import com.mykaimeal.planner.model.MarketItem
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
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.viewmodel.BasketScreenViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.DataModel
import com.mykaimeal.planner.model.IngredientsItems
import com.mykaimeal.planner.model.YourRecipeItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasketScreenFragment : Fragment(), OnItemClickListener, OnItemSelectListener,
    OnItemClickedListener {
    private var binding: FragmentBasketScreenBinding? = null
    private var adapter: SuperMarketListAdapter? = null
    private var adapterGetAddressItem: AdapterGetAddressItem? = null
    private var adapterRecipe: BasketYourRecipeAdapter? = null
    private lateinit var adapterIngredients: IngredientsAdapter
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private lateinit var basketScreenViewModel: BasketScreenViewModel
    private var rcySavedAddress: RecyclerView? = null

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
        addressDialog()
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
            findNavController().navigate(R.id.checkoutScreenFragment)
//             findNavController().navigate(R.id.tescoCartItemFragmentFragment)
        }

        binding!!.textSeeAll3.setOnClickListener {
            findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
        }

        adapterInitialize()
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

        if (data.stores != null && data.stores.size > 0) {
            adapter = SuperMarketListAdapter(data.stores, requireActivity(), this)
            binding!!.rcvSuperMarket.adapter = adapter
        }

        if (data.recipe != null && data.recipe.size > 0) {
            adapterRecipe = BasketYourRecipeAdapter(data.recipe, requireActivity(), this)
            binding!!.rcvYourRecipes.adapter = adapterRecipe
        }

        if (data.ingredient != null && data.ingredient.size > 0) {
            adapterIngredients = IngredientsAdapter(data.ingredient, requireActivity(), this)
            binding!!.rcvIngredients.adapter = adapterIngredients
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

    private fun adapterInitialize() {
        /* adapter = SuperMarketListAdapter(requireContext(), itemList = list, this)*/
        /*
                adapterRecipe = YourRecipeAdapter(requireContext(), itemList = listRecipe, this)
        */
        /*
                adapterIngredients = IngredientsAdapter(requireContext(), itemList = listIngredients)
        */

        /*  binding!!.rcvSuperMarket.adapter = adapter
          binding!!.rcvYourRecipes.adapter = adapterRecipe
          binding!!.rcvIngredients.adapter = adapterIngredients*/

        val initialList = List(6) { index ->
            if (index % 2 == 0) {
                MarketItem(R.drawable.ic_supermarket_icon1, "Tesco", "$25*", "0.7 mile")
            } else {
                MarketItem(R.drawable.ic_supermarket_icon2, "Sainsbury", "$45*", "0.8 mile")
            }
        }


        val initialList1 = List(6) { index ->
            if (index % 2 == 0) {
                YourRecipeItem("Pot Lentil", R.drawable.ic_food_image, "Serves 1")
            } else {
                YourRecipeItem("Pot Rice", R.drawable.ic_food_image, "Serves 2")
            }
        }

        val initialList2 = List(4) { index ->
            if (index % 2 == 0) {
                IngredientsItems(R.drawable.ic_food_image, "Tesco Mustard Seeds", "60 G", "$25")
            } else {
                IngredientsItems(R.drawable.ic_food_image, "ketchup", "70 ml", "$35")
            }
        }
        /*
                adapter.addItems(initialList)
        */
//        adapterRecipe.addItems(initialList1)
//        adapterIngredients.addItems(initialList2)

    }

    private fun basketScreenModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()

        data1.title = "Walmart"
        data1.isOpen = false
        data1.type = "FullCookSchDinner"
        data1.image = R.drawable.ic_welmart_super_market

        data2.title = "Kroger"
        data2.isOpen = false
        data2.type = "FullCookSchDinner"
        data2.image = R.drawable.ic_kroger_super_market

        data3.title = "Whole Foods"
        data3.isOpen = false
        data3.type = "FullCookSchDinner"
        data3.image = R.drawable.ic_target_super_market

        data4.title = "Aldi"
        data4.isOpen = false
        data4.type = "FullCookSchDinner"
        data4.image = R.drawable.ic_whole_foods_super_market

        data5.title = "Costco"
        data5.isOpen = false
        data5.type = "FullCookSchDinner"
        data5.image = R.drawable.super_market_aldi_image

        data6.title = "stawberry"
        data6.isOpen = false
        data6.type = "FullCookSchDinner"
        data6.image = R.drawable.super_market_costco_image

        data7.title = "stawberry"
        data7.isOpen = false
        data7.type = "FullCookSchDinner"
        data7.image = R.drawable.ic_albertsons_super_market

        dataList3.add(data1)
        dataList3.add(data2)
        dataList3.add(data3)
        dataList3.add(data4)
        dataList3.add(data5)

        /* ingredientDinnerAdapter = IngredientsDinnerAdapter(dataList3, requireActivity(), this, null,this)
         binding!!.rcyRecipesCooked.adapter = ingredientDinnerAdapter*/
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (status == "2") {
            removeRecipeBasketDialog()
        }
    }


    private fun removeRecipeBasketDialog() {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_remove_recipe_basket)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val tvDialogCancelBtn = dialogAddItem.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogRemoveBtn = dialogAddItem.findViewById<TextView>(R.id.tvDialogRemoveBtn)
        dialogAddItem.show()
        dialogAddItem.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogAddItem.dismiss()
        }

        tvDialogRemoveBtn.setOnClickListener {
            dialogAddItem.dismiss()
        }
    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {
        findNavController().navigate(R.id.basketDetailSuperMarketFragment)

    }

    override fun itemClicked(position: Int?, list: MutableList<String>?, status: String?, type: String?) {

    }

}
package com.yesitlabs.mykaapp.fragment.mainfragment.commonscreen

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.OnItemSelectListener
import com.yesitlabs.mykaapp.model.MarketItem
import com.yesitlabs.mykaapp.adapter.SuperMarketListAdapter
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.LetsStartOptionActivity
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.IngredientsAdapter
import com.yesitlabs.mykaapp.adapter.YourRecipeAdapter
import com.yesitlabs.mykaapp.databinding.FragmentBasketScreenBinding
import com.yesitlabs.mykaapp.model.IngredientsItems
import com.yesitlabs.mykaapp.model.YourRecipeItem

class BasketScreenFragment : Fragment(),OnItemClickListener,OnItemSelectListener {
    private var binding:FragmentBasketScreenBinding?=null
    private lateinit var adapter: SuperMarketListAdapter
    private lateinit var adapterRecipe: YourRecipeAdapter
    private lateinit var adapterIngredients: IngredientsAdapter
    lateinit var handler: Handler
    private val list = mutableListOf<MarketItem>()
    private val listRecipe = mutableListOf<YourRecipeItem>()
    private val listIngredients = mutableListOf<IngredientsItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentBasketScreenBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        shoppingPreferencesDialog()
        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.textSeeAll1.setOnClickListener {
            findNavController().navigate(R.id.superMarketsNearByFragment)
        }

        binding!!.imageBackIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.textSeeAll2.setOnClickListener {
            findNavController().navigate(R.id.basketYourRecipeFragment)
        }

        binding!!.textShoppingList.setOnClickListener {
            findNavController().navigate(R.id.shoppingListFragment)
        }

        binding!!.textCheckoutTesco.setOnClickListener {
            findNavController().navigate(R.id.tescoCartItemFragmentFragment)
        }

        binding!!.textSeeAll3.setOnClickListener {
            findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
        }

        adapterInitialize()

    }

    private fun shoppingPreferencesDialog() {
        val dialogMiles: Dialog = context?.let { Dialog(it) }!!
        dialogMiles.setContentView(R.layout.alert_dialog_shopping_preferences)
        dialogMiles.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogMiles.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        val relDone = dialogMiles.findViewById<RelativeLayout>(R.id.relDone)
        dialogMiles.show()
        dialogMiles.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        relDone.setOnClickListener{
            dialogMiles.dismiss()
        }

    }

    private fun adapterInitialize(){
        adapter = SuperMarketListAdapter(requireContext(), itemList = list,this)
        adapterRecipe = YourRecipeAdapter(requireContext(), itemList = listRecipe,this)
        adapterIngredients = IngredientsAdapter(requireContext(), itemList = listIngredients)

        binding!!.rcvSuperMarket.adapter = adapter
        binding!!.rcvYourRecipes.adapter = adapterRecipe
        binding!!.rcvIngredients.adapter = adapterIngredients


        val initialList = List(6) { index ->
            if (index % 2 == 0) {
                MarketItem( R.drawable.ic_supermarket_icon1,  "Tesco", "$25*","0.7 mile")
            } else {
                MarketItem( R.drawable.ic_supermarket_icon2,  "Sainsbury", "$45*","0.8 mile")
            }
        }


        val initialList1 = List(6) { index ->
            if (index % 2 == 0) {
                YourRecipeItem( "Pot Lentil",R.drawable.ic_food_image,  "Serves 1")
            } else {
                YourRecipeItem("Pot Rice",R.drawable.ic_food_image,  "Serves 2")
            }
        }

        val initialList2 = List(4) { index ->
            if (index % 2 == 0) {
                IngredientsItems( R.drawable.ic_food_image,  "Tesco Mustard Seeds","60 G","$25")
            } else {
                IngredientsItems(R.drawable.ic_food_image,  "ketchup","70 ml","$35")
            }
        }

        adapter.addItems(initialList)

        adapterRecipe.addItems(initialList1)
        adapterIngredients.addItems(initialList2)

    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (status=="2"){
            removeRecipeBasketDialog()
        }
    }


    private fun removeRecipeBasketDialog() {
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
            dialogAddItem.dismiss()
        }
    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {
        findNavController().navigate(R.id.basketDetailSuperMarketFragment)

    }


}
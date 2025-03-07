package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.IngredientsAdapter
import com.mykaimeal.planner.adapter.BasketYourRecipeAdapter
import com.mykaimeal.planner.databinding.FragmentShoppingListBinding
import com.mykaimeal.planner.model.IngredientsItems
import com.mykaimeal.planner.model.YourRecipeItem

class ShoppingListFragment : Fragment(),OnItemClickListener {
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var adapterRecipe: BasketYourRecipeAdapter
    private lateinit var adapterIngredients: IngredientsAdapter

    private val listRecipe = mutableListOf<YourRecipeItem>()
    private val listIngredients = mutableListOf<IngredientsItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentShoppingListBinding.inflate(layoutInflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding.root
    }

    private fun initialize() {

        binding.textSeeAll3.setOnClickListener {
            findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
        }

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlAddPlanButton.setOnClickListener{
            findNavController().navigate(R.id.addMoreItemsFragment)
        }

        adapterInitialize()

    }

    private fun adapterInitialize(){

//        adapterRecipe = YourRecipeAdapter(requireContext(), itemList = listRecipe,this)
//        adapterIngredients = IngredientsAdapter(requireContext(), itemList = listIngredients)

        binding.rcvYourRecipes.adapter = adapterRecipe
        binding.rcvIngredients.adapter = adapterIngredients

        val initialList1 = List(6) { index ->
            if (index % 2 == 0) {
                YourRecipeItem( "Pot Lentil",R.drawable.ic_food_image,  "Serves 2")
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

//        adapterRecipe.addItems(initialList1)
//        adapterIngredients.addItems(initialList2)

    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

    }

}
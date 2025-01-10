package com.mykameal.planner.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykameal.planner.R
import com.mykameal.planner.adapter.ShoppingMissingIngredientsAdapter
import com.mykameal.planner.databinding.FragmentShoppingMissingIngredientsBinding
import com.mykameal.planner.model.DataModel

class ShoppingMissingIngredientsFragment : Fragment() {

    private lateinit var binding: FragmentShoppingMissingIngredientsBinding
    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var selectAll:Boolean?=false
    lateinit var adapter: ShoppingMissingIngredientsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentShoppingMissingIngredientsBinding.inflate(layoutInflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        missingIngModel()
        return binding.root
    }

    private fun missingIngModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()
        val data8 = DataModel()

        data1.title = "Tesco Mustard Seeds"
        data1.quantity = "60 G"
        data1.image = R.drawable.ic_food_image

        data2.title = "Tesco Mustard Seeds"
        data2.quantity = "60 G"
        data2.image = R.drawable.ic_food_image

        data3.title = "Tesco Mustard Seeds"
        data3.quantity = "60 G"
        data3.image = R.drawable.ic_food_image

        data4.title = "Tesco Mustard Seeds"
        data4.quantity = "60 G"
        data4.image = R.drawable.ic_food_image

        data5.title = "Tesco Mustard Seeds"
        data5.quantity = "60 G"
        data5.image = R.drawable.ic_food_image

        data6.title = "Tesco Mustard Seeds"
        data6.quantity = "60 G"
        data6.image = R.drawable.ic_food_image

        data7.title = "Tesco Mustard Seeds"
        data7.quantity = "60 G"
        data7.image = R.drawable.ic_food_image

        data8.title = "Tesco Mustard Seeds"
        data8.quantity = "60 G"
        data8.image = R.drawable.ic_food_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)
        dataList1.add(data4)
        dataList1.add(data5)
        dataList1.add(data6)
        dataList1.add(data7)
        dataList1.add(data8)

        adapter = ShoppingMissingIngredientsAdapter(dataList1)
        binding.rcvIngredients.adapter = adapter
    }


    private fun initialize() {

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.checkboxSelectAll.setOnClickListener{
            if (selectAll==true){
                adapter.setCheckEnabled(false)
                binding.checkboxSelectAll.isChecked=false
                selectAll=false
                binding.textAddBasket.text="Add to Basket"
            }else{
                binding.checkboxSelectAll.isChecked=true
                selectAll=true
                binding.textAddBasket.text="I have Purchased Everything"
                adapter.setCheckEnabled(true)
            }
        }
    }

}
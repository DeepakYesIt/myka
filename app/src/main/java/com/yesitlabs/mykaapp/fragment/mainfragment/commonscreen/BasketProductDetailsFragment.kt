package com.yesitlabs.mykaapp.fragment.mainfragment.commonscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.adapter.AdapterProductsDetailsSelectItem
import com.yesitlabs.mykaapp.databinding.FragmentBasketProductDetailsBinding
import com.yesitlabs.mykaapp.model.DataModel

class BasketProductDetailsFragment : Fragment() {
    private lateinit var binding:FragmentBasketProductDetailsBinding
    private var adapterProductsDetailsSelectItem:AdapterProductsDetailsSelectItem?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentBasketProductDetailsBinding.inflate(layoutInflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()
        productDetailsModel()

        return binding.root
    }

    private fun initialize() {
        binding.imgBackRecipeDetails.setOnClickListener{
            findNavController().navigateUp()
        }

    }

    private fun productDetailsModel() {
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
    }

}
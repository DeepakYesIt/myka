package com.mykameal.planner.fragment.mainfragment.hometab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.IngredientsRecipeAdapter
import com.mykameal.planner.databinding.FragmentMissingIngredientsBinding
import com.mykameal.planner.model.DataModel

class MissingIngredientsFragment : Fragment() {
    private var binding: FragmentMissingIngredientsBinding? = null
    private var ingredientsRecipeAdapter: IngredientsRecipeAdapter? = null
    private var selectAll:Boolean?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMissingIngredientsBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        addedIngredientsModel()
        missingIngredientsModel()

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imgBackMissingIng.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.checkBoxImg.setOnClickListener{
            if (selectAll==true){
                ingredientsRecipeAdapter?.setCheckEnabled(false)
                binding!!.checkBoxImg.setImageResource(R.drawable.orange_uncheck_box_images)
                selectAll=false
            }else{
                binding!!.checkBoxImg.setImageResource(R.drawable.orange_checkbox_images)
                selectAll=true
                ingredientsRecipeAdapter?.setCheckEnabled(true)
            }
        }
    }

    private fun addedIngredientsModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()

        data1.title = "Tomato"
        data1.description = "0.5 kg"
        data1.isOpen = false
        data1.type = "AddedIng"
        data1.image = R.drawable.tomato_ing_image

        data2.title = "Tomato"
        data2.description = "0.5 kg"
        data2.isOpen = false
        data2.type = "AddedIng"
        data2.image = R.drawable.tomato_ing_image

        dataList.add(data1)
        dataList.add(data2)

        /*ingredientsRecipeAdapter = IngredientsRecipeAdapter(dataList, requireActivity())
        binding!!.rcyAddedIngredientsRecipes.adapter = ingredientsRecipeAdapter*/
    }

    private fun missingIngredientsModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()

        data1.title = "Olive Oil"
        data1.description = "1 Tbsp"
        data1.isOpen = false
        data1.type = "MissingIng"
        data1.image = R.drawable.olive_image

        data2.title = "Garlic Mayo"
        data2.description = "3 Tbsp"
        data2.isOpen = false
        data2.type = "MissingIng"
        data2.image = R.drawable.garlic_mayo_image

        data3.title = "Olive Oil"
        data3.description = "3 Tbsp"
        data3.isOpen = false
        data3.type = "MissingIng"
        data3.image = R.drawable.olive_oil_image2

        data4.title = "Olive Oil"
        data4.description = "1 kg"
        data4.isOpen = false
        data4.type = "MissingIng"
        data4.image = R.drawable.olive_chicken_image

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)

      /*  ingredientsRecipeAdapter = IngredientsRecipeAdapter(dataList, requireActivity())
        binding!!.rcyIngredientsRecipe.adapter = ingredientsRecipeAdapter*/
    }



}
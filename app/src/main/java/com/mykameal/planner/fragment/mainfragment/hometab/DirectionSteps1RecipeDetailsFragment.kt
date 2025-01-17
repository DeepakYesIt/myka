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
import com.mykameal.planner.adapter.AdapterPrepareCookItem
import com.mykameal.planner.databinding.FragmentDirectionStepsRecipeDetailsBinding
import com.mykameal.planner.model.DataModel

class DirectionSteps1RecipeDetailsFragment : Fragment() {

    private var binding:FragmentDirectionStepsRecipeDetailsBinding?=null
    private var totalProgressValue:Int=0
    private var adapterPrepareCookItem: AdapterPrepareCookItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentDirectionStepsRecipeDetailsBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                findNavController().navigate(R.id.recipeDetailsFragment)
                findNavController().navigateUp()
            }
        })

        binding!!.progressBar.max=2
        totalProgressValue=2
        updateProgress(1)

        ingredientsModel()

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imgStep1RecipeDetails.setOnClickListener{
//            findNavController().navigate(R.id.recipeDetailsFragment)
            findNavController().navigateUp()
        }

        binding!!.relNextStep.setOnClickListener{
            findNavController().navigate(R.id.directionSteps2RecipeDetailsFragmentFragment)
        }
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar.progress = progress
        binding!!.tvProgressText.text = "$progress /$totalProgressValue"
    }

    private fun ingredientsModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()

        data1.title = "Olive Oil"
        data1.description = "1 Tbsp"
        data1.isOpen = false
        data1.type = "PrepareCook"
        data1.image = R.drawable.olive_image

        data2.title = "Garlic Mayo"
        data2.description = "3 Tbsp"
        data2.isOpen = false
        data2.type = "PrepareCook"
        data2.image = R.drawable.garlic_mayo_image

        data3.title = "Olive Oil"
        data3.description = "3 Tbsp"
        data3.isOpen = false
        data3.type = "PrepareCook"
        data3.image = R.drawable.olive_oil_image2

        data4.title = "Olive Oil"
        data4.description = "1 kg"
        data4.isOpen = false
        data4.type = "PrepareCook"
        data4.image = R.drawable.olive_chicken_image

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)

        adapterPrepareCookItem = AdapterPrepareCookItem(dataList, requireActivity())
        binding!!.rcyPrepareToCook.adapter = adapterPrepareCookItem
    }


}
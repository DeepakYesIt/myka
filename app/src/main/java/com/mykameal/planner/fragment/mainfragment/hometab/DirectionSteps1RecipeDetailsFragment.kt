package com.mykameal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterPrepareCookItem
import com.mykameal.planner.databinding.FragmentDirectionStepsRecipeDetailsBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.RecipeDetailsViewModel
import com.mykameal.planner.model.DataModel

class DirectionSteps1RecipeDetailsFragment : Fragment() {

    private var binding:FragmentDirectionStepsRecipeDetailsBinding?=null
    private var totalProgressValue:Int=0
    private var adapterPrepareCookItem: AdapterPrepareCookItem? = null
    private lateinit var viewModel: RecipeDetailsViewModel
    private var mealType: String = ""
    private var uri: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentDirectionStepsRecipeDetailsBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RecipeDetailsViewModel::class.java]

        mealType = arguments?.getString("mealType", "").toString()
        uri = arguments?.getString("uri", "").toString()

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

        setData()

        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        if (viewModel.getRecipeData()?.get(0)!!.recipe?.images?.SMALL?.url != null) {
            Glide.with(requireContext())
                .load(viewModel.getRecipeData()?.get(0)!!.recipe?.images?.SMALL?.url)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility = View.GONE
                        return false
                    }
                })
                .into(binding!!.imageData)
        } else {
            binding!!.layProgess.root.visibility = View.GONE
        }

        if (viewModel.getRecipeData()?.get(0)!!.recipe?.label != null) {
            binding!!.tvTitle.text = "" + viewModel.getRecipeData()?.get(0)!!.recipe?.label
        }

        if (viewModel.getRecipeData()?.get(0)!!.recipe?.source != null) {
            binding!!.tvBy.text = "By " + viewModel.getRecipeData()?.get(0)!!.recipe?.source
        }

        if (viewModel.getRecipeData()?.get(0)!!.recipe?.ingredients != null && viewModel.getRecipeData()?.get(0)!!.recipe?.ingredients!!.size > 0) {
            adapterPrepareCookItem = AdapterPrepareCookItem(viewModel.getRecipeData()?.get(0)!!.recipe?.ingredients, requireActivity())
            binding!!.rcyPrepareToCook.adapter = adapterPrepareCookItem
            binding!!.rcyPrepareToCook.visibility = View.VISIBLE
        } else {
            binding!!.rcyPrepareToCook.visibility = View.GONE
        }


    }

    private fun initialize() {

        binding!!.imgStep1RecipeDetails.setOnClickListener{
//            findNavController().navigate(R.id.recipeDetailsFragment)
            findNavController().navigateUp()
        }

        binding!!.relNextStep.setOnClickListener{
            val bundle=Bundle()
            bundle.putString("uri",uri)
            bundle.putString("mealType",mealType)
//            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment,bundle)
            findNavController().navigate(R.id.directionSteps2RecipeDetailsFragmentFragment,bundle)
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

        /*adapterPrepareCookItem = AdapterPrepareCookItem(dataList, requireActivity())
        binding!!.rcyPrepareToCook.adapter = adapterPrepareCookItem*/
    }


}
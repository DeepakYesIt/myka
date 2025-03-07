package com.mykaimeal.planner.fragment.mainfragment.hometab.missingingredientsfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterMissingIngredientAvailableItem
import com.mykaimeal.planner.adapter.AdapterMissingIngredientsItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentMissingIngredientsBinding
import com.mykaimeal.planner.fragment.mainfragment.hometab.missingingredientsfragment.model.MissingIngredientModel
import com.mykaimeal.planner.fragment.mainfragment.hometab.missingingredientsfragment.model.MissingIngredientModelData
import com.mykaimeal.planner.fragment.mainfragment.hometab.missingingredientsfragment.viewmodel.MissingIngredientViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MissingIngredientsFragment : Fragment(), OnItemSelectListener {
    private var binding: FragmentMissingIngredientsBinding? = null
    private var adapterMissingIngredientsItem: AdapterMissingIngredientsItem? = null
    private var adapterMissingIngAvailItem: AdapterMissingIngredientAvailableItem? = null
    private var selectAll:Boolean?=false
    private val missingIngredientList = mutableListOf<MissingIngredientModelData>()
    private val availableIngredientList = mutableListOf<MissingIngredientModelData>()
    private var shcId:String?=""
    private var recipeUri:String?=""
    private var foodIds = mutableListOf<String>()
    private var foodName = mutableListOf<String>()
    private var statusType = mutableListOf<String>()

    private lateinit var missingIngredientViewModel: MissingIngredientViewModel

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

        missingIngredientViewModel =
            ViewModelProvider(requireActivity())[MissingIngredientViewModel::class.java]

        shcId = arguments?.getString("schId", "").toString()
        recipeUri = arguments?.getString("uri", "").toString()

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
            if (missingIngredientViewModel.getRecipeData()?.size!!> 0) {
                selectAll = !selectAll!! // Toggle the selectAll value
                // Update the drawable based on the selectAll state
                val drawableRes = if (selectAll as Boolean) R.drawable.orange_checkbox_images else R.drawable.orange_uncheck_box_images
              binding!!.checkBoxImg.setImageResource(drawableRes)
                // Update the status of each ingredient dynamically
                missingIngredientViewModel.getRecipeData()!!
                    .forEach { ingredient -> ingredient.status = selectAll as Boolean
                }
                // Notify adapter with updated data
                adapterMissingIngredientsItem?.updateList(missingIngredientViewModel.getRecipeData()!!)
            }
        }

        binding!!.tvAddToBasket.setOnClickListener{
            if (statusType.size!=null){
                statusType.clear()
            }
            if (BaseApplication.isOnline(requireActivity())) {
                if (missingIngredientViewModel.getRecipeData()?.size!!  > 0) {
                    try {
                        // Iterate through the ingredients and add them to the array if status is true
                        missingIngredientViewModel.getRecipeData()?.forEach { ingredientsModel ->
                            if (ingredientsModel.status) {
                                foodIds.add(ingredientsModel.foodId.toString())
                                foodName.add(ingredientsModel.food.toString())
                                statusType.add("0")
                            }
                        }
                        // Log the final JSON data
                        Log.d("final data", "******$foodIds")
                        Log.d("final data", "******$foodName")
                        addToCartApi()
                    } catch (e: Exception) {
                        BaseApplication.alertError(requireContext(), e.message, false)
                    }
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }


        binding!!.tvPurchasedBtn.setOnClickListener{
            if (statusType.size!=null){
                statusType.clear()
            }
            if (BaseApplication.isOnline(requireActivity())) {
                if (missingIngredientViewModel.getRecipeData()?.size!!  > 0) {
                    try {
                        // Iterate through the ingredients and add them to the array if status is true
                        missingIngredientViewModel.getRecipeData()?.forEach { ingredientsModel ->
                            if (ingredientsModel.status) {
                                foodIds.add(ingredientsModel.foodId.toString())
                                foodName.add(ingredientsModel.food.toString())
                                statusType.add("1")
                            }
                        }
                        // Log the final JSON data
                        Log.d("final data", "******$foodIds")
                        Log.d("final data", "******$foodName")
                        addToCartApi()
                    } catch (e: Exception) {
                        BaseApplication.alertError(requireContext(), e.message, false)
                    }
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        if (BaseApplication.isOnline(requireActivity())) {
            missingIngredientApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun missingIngredientApi() {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                missingIngredientViewModel.getMissingIngredientsApi({
                    BaseApplication.dismissMe()
                    handleApiMissingIngResponse(it)
                }, recipeUri,shcId)
            }
    }

    private fun handleApiMissingIngResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessMissingResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessMissingResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, MissingIngredientModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                showDataInUi(apiModel.data)
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

    private fun showDataInUi(data: MutableList<MissingIngredientModelData>?) {
        try {
            if (data!=null && data.size>0){
                missingIngredientViewModel.setRecipeData(data)

                // Assuming you have a response object of type MissingIngredientModel
                data.forEach { ingredient ->
                    if (ingredient.is_missing == 0) {
                        missingIngredientList.add(ingredient) // Add to missing ingredients list
                    } else {
                        availableIngredientList.add(ingredient) // Add to available ingredients list
                    }
                }

                Log.d("dffdsss","ffdfdd"+missingIngredientList.size)
                Log.d("dffd","22222:___"+availableIngredientList.size)

                if (missingIngredientList!=null && missingIngredientList.size>0){
                    binding!!.rcyIngredientsRecipe.visibility=View.VISIBLE
                    adapterMissingIngredientsItem = AdapterMissingIngredientsItem(data, requireActivity(),this)
                    binding!!.rcyIngredientsRecipe.adapter = adapterMissingIngredientsItem
                }else{
                    findNavController().navigateUp()
                    binding!!.rcyIngredientsRecipe.visibility=View.GONE
                }

                if (availableIngredientList!=null && availableIngredientList.size>0){
                    binding!!.rcyAddedIngredientsRecipes.visibility=View.VISIBLE
                    adapterMissingIngAvailItem = AdapterMissingIngredientAvailableItem(availableIngredientList, requireActivity())
                    binding!!.rcyAddedIngredientsRecipes.adapter = adapterMissingIngAvailItem
                    binding!!.relAddedIngredients.visibility=View.VISIBLE
                }else{
                    binding!!.rcyAddedIngredientsRecipes.visibility=View.GONE
                    binding!!.relAddedIngredients.visibility=View.GONE
                }
            }else{
//                binding!!.llSearchRecipientIng.visibility=View.GONE
            }
        }catch (e:Exception){
            Log.d("MissingIngredient@@@@","Data List:------"+e.message)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun addToCartApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            missingIngredientViewModel.addToCartUrlApi({
                BaseApplication.dismissMe()
                handleCartApiResponse(it)
            }, foodIds, shcId,foodName,statusType)
        }
    }

    private fun handleCartApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessCartResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessCartResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                Toast.makeText(requireContext(), apiModel.message, Toast.LENGTH_LONG).show()
                if (statusType[0]=="0"){
                    findNavController().navigateUp()
                }else{
                    if (BaseApplication.isOnline(requireActivity())) {
                        missingIngredientApi()
                    } else {
                        BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
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

    override fun itemSelect(position: Int?, status: String?, type: String?) {
        missingIngredientViewModel.getRecipeData()?.forEachIndexed { index, ingredient ->
            if (index == position) {
                ingredient.status = missingIngredientViewModel.getRecipeData()?.get(position)!!.status != true
            }
        }
        // Notify adapter with updated data
        adapterMissingIngredientsItem?.updateList(missingIngredientViewModel.getRecipeData()!!)

        selectAll = missingIngredientViewModel.getRecipeData()?.all { it.status } == true

        // Update the drawable based on the selectAll state
        val drawableRes = if (selectAll as Boolean) R.drawable.orange_checkbox_images else R.drawable.orange_uncheck_box_images
        binding!!.checkBoxImg.setImageResource(drawableRes)

    }
}
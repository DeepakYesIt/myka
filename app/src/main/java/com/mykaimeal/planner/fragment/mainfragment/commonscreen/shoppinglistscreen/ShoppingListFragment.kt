package com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.BasketYourRecipeAdapter
import com.mykaimeal.planner.adapter.IngredientsShoppingAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentShoppingListBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model.ShoppingListModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model.ShoppingListModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.viewmodel.ShoppingListViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingListFragment : Fragment(), OnItemClickListener, OnItemSelectListener {
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var adapterShoppingAdapter: IngredientsShoppingAdapter
    private var adapterRecipe: BasketYourRecipeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShoppingListBinding.inflate(layoutInflater, container, false)

        shoppingListViewModel =
            ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        getShoppingList()

        return binding.root
    }

    private fun initialize() {

        binding.textSeeAll3.setOnClickListener {
            findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
        }

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlAddPlanButton.setOnClickListener {
            findNavController().navigate(R.id.addMoreItemsFragment)
        }

        adapterInitialize()

    }

    private fun getShoppingList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            shoppingListViewModel.getShoppingListUrl {
                BaseApplication.dismissMe()
                handleApiShoppingListResponse(it)
            }
        }
    }

    private fun handleApiShoppingListResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessShoppingResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessShoppingResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, ShoppingListModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
                    showDataShoppingUI(apiModel.data)
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

    private fun showDataShoppingUI(data: ShoppingListModelData) {

        if (data.recipe != null && data.recipe.size > 0) {
            adapterRecipe = BasketYourRecipeAdapter(data.recipe, requireActivity(), this)
            binding.rcvYourRecipes.adapter = adapterRecipe
        }

        if (data.ingredient!=null && data.ingredient.size>0){
            if (data.ingredient != null && data.ingredient.size > 0) {
                adapterShoppingAdapter = IngredientsShoppingAdapter(data.ingredient, requireActivity(), this)
                binding.rcvIngredients.adapter = adapterShoppingAdapter
            }
        }
    }


    private fun adapterInitialize() {

    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {

    }

}
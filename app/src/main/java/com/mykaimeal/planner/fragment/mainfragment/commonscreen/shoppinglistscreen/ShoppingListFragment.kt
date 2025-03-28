package com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen

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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Ingredient
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Recipes
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model.ShoppingListModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model.ShoppingListModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.viewmodel.ShoppingListViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingListFragment : Fragment(), OnItemClickListener, OnItemSelectListener {
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var adapterShoppingAdapter: IngredientsShoppingAdapter
    private var adapterRecipe: BasketYourRecipeAdapter? = null
    private var tvCounter: TextView? = null
    private var quantity: Int = 1
    private var recipe: MutableList<Recipes>? = null
    private var ingredientList: MutableList<Ingredient>? = null

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

        if (BaseApplication.isOnline(requireContext())) {
            getShoppingList()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        return binding.root
    }

    private fun updateValue() {
        tvCounter!!.text = String.format("%02d", quantity)

    }

    private fun initialize() {

        binding.textSeeAll3.setOnClickListener {
            findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
        }

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlAddMore.setOnClickListener {

            addItemDialog()
            /*     findNavController().navigate(R.id.addMoreItemsFragment)*/
        }
    }

    private fun addItemDialog() {
        val dialogRemoveDay: Dialog = context?.let { Dialog(it) }!!
        dialogRemoveDay.setContentView(R.layout.alert_dialog_add_new_item)
        dialogRemoveDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogRemoveDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogCancelBtn = dialogRemoveDay.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val imageCross = dialogRemoveDay.findViewById<ImageView>(R.id.imageCross)
        val imageMinus = dialogRemoveDay.findViewById<ImageView>(R.id.imageMinus)
        val imagePlus = dialogRemoveDay.findViewById<ImageView>(R.id.imagePlus)
        tvCounter = dialogRemoveDay.findViewById(R.id.tvCounter)
        val tvDialogAddBtn = dialogRemoveDay.findViewById<TextView>(R.id.tvDialogAddBtn)
        dialogRemoveDay.show()
        dialogRemoveDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogRemoveDay.dismiss()
        }

        imageMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateValue()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Minimum serving atleast value is one",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        imagePlus.setOnClickListener {
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        imageCross.setOnClickListener {
            dialogRemoveDay.dismiss()
        }

        tvDialogAddBtn.setOnClickListener {
            dialogRemoveDay.dismiss()
        }
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
            binding.rlYourRecipes.visibility = View.VISIBLE
            recipe = data.recipe
            adapterRecipe = BasketYourRecipeAdapter(data.recipe, requireActivity(), this)
            binding.rcvYourRecipes.adapter = adapterRecipe
        } else {
            binding.rlYourRecipes.visibility = View.GONE
        }

        if (data.ingredient != null && data.ingredient.size > 0) {
            ingredientList=data.ingredient
            adapterShoppingAdapter = IngredientsShoppingAdapter(data.ingredient, requireActivity(), this)
            binding.rcvIngredients.adapter = adapterShoppingAdapter

        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {
        if (type == "YourRecipe") {
            if (status == "Minus") {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddRecipeServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else if (status == "Plus") {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddRecipeServing(position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else {
                removeRecipeBasketDialog(status, position)
            }
        } else if (type == "ShoppingIngredients") {
            if (status == "Minus") {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else if (status == "Plus") {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }
    }

    private fun removeAddIngServing(position: Int?, type: String) {
        val item = position?.let { ingredientList?.get(it) }
        if (type.equals("plus", true) || type.equals("minus", true)) {
            var count = item?.sch_id
            val foodId = item?.food_id
            count = when (type.lowercase()) {
                "plus" -> count!! + 1
                "minus" -> count!! - 1
                else -> count // No change if `apiType` doesn't match
            }
            increaseIngRecipe(foodId, count.toString(), item, position)
        }
    }

    private fun increaseIngRecipe(
        foodId: String?,
        quantity: String,
        item: Ingredient?,
        position: Int?
    ) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            shoppingListViewModel.basketIngIncDescUrl({
                BaseApplication.dismissMe()
                handleApiIngResponse(it, item, quantity, position)
            }, foodId, quantity)
        }
    }

    private fun handleApiIngResponse(
        result: NetworkResult<String>,
        item: Ingredient?,
        quantity: String,
        position: Int?
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessIngResponse(
                result.data.toString(),
                item,
                quantity,
                position
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessIngResponse(
        data: String,
        item: Ingredient?,
        quantity: String,
        position: Int?
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                item?.sch_id = quantity.toInt()
                if (item != null) {
                    ingredientList?.set(position!!, item)
                }
                // Update the adapter
                if (ingredientList != null) {
                    adapterShoppingAdapter.updateList(ingredientList!!)
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

    private fun removeAddRecipeServing(position: Int?, type: String) {
        val item = position?.let { recipe?.get(it) }
        if (type.equals("plus", true) || type.equals("minus", true)) {
            var count = item?.serving?.toInt()
            val uri = item?.uri
            count = when (type.lowercase()) {
                "plus" -> count!! + 1
                "minus" -> count!! - 1
                else -> count // No change if `apiType` doesn't match
            }
            increaseQuantityRecipe(uri, count.toString(), item, position)
        }
    }

    private fun increaseQuantityRecipe(
        uri: String?,
        quantity: String,
        item: Recipes?,
        position: Int?
    ) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            shoppingListViewModel.basketYourRecipeIncDescUrl({
                BaseApplication.dismissMe()
                handleApiQuantityResponse(it, item, quantity, position)
            }, uri, quantity)
        }
    }

    private fun handleApiQuantityResponse(
        result: NetworkResult<String>,
        item: Recipes?,
        quantity: String,
        position: Int?
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessQuantityResponse(
                result.data.toString(),
                item,
                quantity,
                position
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessQuantityResponse(
        data: String,
        item: Recipes?,
        quantity: String,
        position: Int?
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                item?.serving = quantity.toInt().toString()
                if (item != null) {
                    recipe?.set(position!!, item)
                }
                // Update the adapter
                if (recipe != null) {
                    adapterRecipe?.updateList(recipe)
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

    private fun removeRecipeBasketDialog(recipeId: String?, position: Int?) {
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
            if (BaseApplication.isOnline(requireActivity())) {
                removeBasketRecipeApi(recipeId.toString(), dialogAddItem, position)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun removeBasketRecipeApi(recipeId: String, dialogRemoveDay: Dialog, position: Int?) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            shoppingListViewModel.removeBasketUrlApi({
                BaseApplication.dismissMe()
                handleApiRemoveBasketResponse(it, position, dialogRemoveDay)
            }, recipeId)
        }
    }

    private fun handleApiRemoveBasketResponse(
        result: NetworkResult<String>,
        position: Int?,
        dialogRemoveDay: Dialog
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessRemoveBasketResponse(
                result.data.toString(),
                position,
                dialogRemoveDay
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessRemoveBasketResponse(
        data: String,
        position: Int?,
        dialogRemoveDay: Dialog
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                dialogRemoveDay.dismiss()
                if (recipe != null) {
                    recipe!!.removeAt(position!!)
                }

                // Update the adapter
                if (recipe != null) {
                    adapterRecipe?.updateList(recipe)
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

}
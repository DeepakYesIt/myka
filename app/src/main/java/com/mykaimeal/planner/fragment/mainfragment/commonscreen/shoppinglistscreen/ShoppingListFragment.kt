package com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.BasketYourRecipeAdapter
import com.mykaimeal.planner.adapter.IngredientsAdapterItem
import com.mykaimeal.planner.adapter.IngredientsShoppingAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentShoppingListBinding
import com.mykaimeal.planner.fragment.commonfragmentscreen.ingredientDislikes.model.DislikedIngredientsModel
import com.mykaimeal.planner.fragment.commonfragmentscreen.ingredientDislikes.model.DislikedIngredientsModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Ingredient
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Recipes
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model.ShoppingListModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.model.ShoppingListModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.viewmodel.ShoppingListViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingListFragment : Fragment(), OnItemClickListener, OnItemSelectListener {
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var adapterShoppingAdapter: IngredientsShoppingAdapter
    private var adapterRecipe: BasketYourRecipeAdapter? = null
    private var tvCounter: TextView? = null
    private var quantity: Int = 1
    private var recipe: MutableList<Recipes> = mutableListOf()
    private var ingredientList: MutableList<Ingredient> = mutableListOf()
    private var foodIds = mutableListOf<String>()
    private var schIds = mutableListOf<String>()
    private var foodName = mutableListOf<String>()
    private var statusType = mutableListOf<String>()
    private lateinit var commonWorkUtils: CommonWorkUtils
    private lateinit var textListener: TextWatcher
    private lateinit var rlWriteNameHere: RelativeLayout
    private var textChangedJob: Job? = null
    private var dislikedIngredientData: MutableList<DislikedIngredientsModelData>? = null
    private var popupWindow: PopupWindow? = null
    private var tvLabel:EditText?=null
    private var ingredientsAdapterItem: IngredientsAdapterItem? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShoppingListBinding.inflate(layoutInflater, container, false)

        shoppingListViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]
        commonWorkUtils = CommonWorkUtils(requireActivity())

        adapterRecipe = BasketYourRecipeAdapter(recipe, requireActivity(), this)
        binding.rcvYourRecipes.adapter = adapterRecipe

        adapterShoppingAdapter = IngredientsShoppingAdapter(ingredientList, requireActivity(), this)
        binding.rcvIngredients.adapter = adapterShoppingAdapter

        backButton()

        initialize()

        if (BaseApplication.isOnline(requireContext())) {
            getShoppingList()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        return binding.root
    }


    private fun backButton(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }

    @SuppressLint("DefaultLocale")
    private fun updateValue() {
        tvCounter!!.text = String.format("%02d", quantity)
    }

    private fun initialize() {

        /*    binding.textSeeAll3.setOnClickListener {
                findNavController().navigate(R.id.shoppingMissingIngredientsFragment)
            }*/

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rlAddMore.setOnClickListener {
            addItemDialog()
        }

        binding.textCheckoutTesco.setOnClickListener{
            addToCartUrlApi()
        }
    }

    private fun addItemDialog() {
        val context = requireContext()
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.alert_dialog_add_new_item)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        val tvDialogCancelBtn = dialog.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val imageCross = dialog.findViewById<ImageView>(R.id.imageCross)
        val imageMinus = dialog.findViewById<ImageView>(R.id.imageMinus)
        val imagePlus = dialog.findViewById<ImageView>(R.id.imagePlus)
        tvCounter = dialog.findViewById(R.id.tvCounter)
        tvLabel = dialog.findViewById(R.id.tvLabel)
        val tvDialogAddBtn = dialog.findViewById<TextView>(R.id.tvDialogAddBtn)
        rlWriteNameHere = dialog.findViewById(R.id.rlWriteNameHere)

        // TextWatcher with debounce
        var searchFor = ""
        textListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if (searchText != searchFor) {
                    searchFor = searchText
                    textChangedJob?.cancel()
                    textChangedJob = lifecycleScope.launch {
                        delay(1000)
                        if (searchText == searchFor) {
                            searchable(searchText)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        tvLabel?.addTextChangedListener(textListener)

        dialog.setOnDismissListener {
            tvLabel?.removeTextChangedListener(textListener)
        }

        imageMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateValue()
            } else {
                Toast.makeText(requireActivity(), ErrorMessage.servingError, Toast.LENGTH_LONG).show()
            }
        }

        imagePlus.setOnClickListener {
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        imageCross.setOnClickListener {
            dialog.dismiss()
        }

        tvDialogCancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        tvDialogAddBtn.setOnClickListener {
            val inputName = tvLabel?.text.toString().trim()
            val quantityText = tvCounter?.text.toString()
            val schId = quantityText.toIntOrNull()

            if (inputName.isEmpty()) {
                commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.enterIngName, false)
                return@setOnClickListener
            }

            val newIngredient = Ingredient(
                created_at = null,
                deleted_at = "",
                food_id = null,
                id = null,
                market_id = "",
                name = inputName,
                price = "",
                pro_id = null,
                pro_img = null,
                pro_name = inputName,
                pro_price = "Not available",
                product_id = null,
                quantity = quantityText,
                sch_id = schId,
                status = null,
                updated_at = null,
                user_id = null
            )

            ingredientList.let {
                it.add(newIngredient)
                adapterShoppingAdapter.notifyItemInserted(it.size - 1)
            }

            dialog.dismiss()

            // Optional: Add API logic here
        }

        dialog.show()
    }

    private fun searchable(editText: String) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            shoppingListViewModel.getDislikeSearchIngredients({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        try {
                            val dietaryModel = gson.fromJson(it.data, DislikedIngredientsModel::class.java)
                            if (dietaryModel.code == 200 && dietaryModel.success) {
                                if (dietaryModel.data != null) {
                                    showDataInUi(dietaryModel.data)
                                }
                            } else {
                                popupWindow?.dismiss()
                                if (dietaryModel.code == ErrorMessage.code) {
                                    showAlertFunction(dietaryModel.message, true)
                                } else {
                                    showAlertFunction(dietaryModel.message, false)
                                }
                            }
                        } catch (e: Exception) {
                            popupWindow?.dismiss()
                            Log.d("IngredientDislike@@@@", "message:--" + e.message)
                        }
                    }

                    is NetworkResult.Error -> {
                        popupWindow?.dismiss()
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        popupWindow?.dismiss()
                        showAlertFunction(it.message, false)
                    }
                }
            }, editText, "Shopping")
        }
    }

    private fun showDataInUi(searchModelData: MutableList<DislikedIngredientsModelData>) {
        try {
            dislikedIngredientData = searchModelData
            loadSearch()

        } catch (e: Exception) {
            popupWindow?.dismiss()
            Log.d("AddMeal", "message:--" + e.message)
        }
    }

    private fun loadSearch() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val popupView: View? = inflater?.inflate(R.layout.item_select_layoutdrop, null)
        // Allows dismissing the popup when touching outside
        popupWindow?.isOutsideTouchable = true
        popupWindow = PopupWindow(popupView, rlWriteNameHere.width, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.showAsDropDown(rlWriteNameHere, 0, 0, Gravity.CENTER)
        val rcyData = popupView?.findViewById<RecyclerView>(R.id.rcy_data)
        ingredientsAdapterItem = dislikedIngredientData?.let { IngredientsAdapterItem(it, requireActivity(), this) }
        rcyData!!.adapter = ingredientsAdapterItem
    }


    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    private fun addToCartUrlApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            shoppingListViewModel.addShoppingCartUrlApi({
                BaseApplication.dismissMe()
                handleCartApiResponse(it)
            }, foodIds, schIds, foodName, statusType)
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

        getShoppingList()
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
                handleError(apiModel.code,apiModel.message)
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    private fun handleError(code: Int, message: String) {
        if (code == ErrorMessage.code) {
            showAlert(message, true)
        } else {
            showAlert(message, false)
        }
    }

    private fun showDataShoppingUI(data: ShoppingListModelData) {


        recipe.clear()
        ingredientList.clear()

        data.recipe?.let {
            recipe.addAll(it)
        }

        data.ingredient?.let {
            ingredientList.addAll(it)
        }

        if (recipe.size > 0) {
            binding.rlYourRecipes.visibility = View.VISIBLE
            adapterRecipe?.updateList(recipe)
        } else {
            binding.rlYourRecipes.visibility = View.GONE
        }

        if (ingredientList.size > 0) {
            adapterShoppingAdapter.updateList(ingredientList)
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

        if (type.equals("IngredientsItem",true)){
            tvLabel?.setText(status.toString().trim())
            popupWindow?.dismiss()
        }

    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {
        if (type.equals("YourRecipe",true)) {
            if (status.equals("Minus",true)) {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddRecipeServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else if (status.equals("Plus",true)) {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddRecipeServing(position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else {
                removeRecipeBasketDialog(status, position)
            }
        } else if (type.equals("ShoppingIngredients",true)) {
            if (status.equals("Minus",true)) {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "minus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else if (status.equals("Plus",true)) {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "plus")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            } else if (status.equals("Delete",true)) {
                if (BaseApplication.isOnline(requireActivity())) {
                    removeAddIngServing(position, "Delete")
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
        } else {
            val foodId = item?.food_id
            increaseIngRecipe(foodId, "0", item, position)
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
                if (quantity != "0") {
                    // Toggle the is_like value
                    item?.sch_id = quantity.toInt()
                    if (item != null) {
                        ingredientList?.set(position!!, item)
                    }
                    // Update the adapter
                    if (ingredientList != null) {
                        adapterShoppingAdapter.updateList(ingredientList!!)
                    }
                }
                getShoppingList()
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
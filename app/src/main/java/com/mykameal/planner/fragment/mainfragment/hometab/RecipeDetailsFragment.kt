package com.mykameal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mykameal.planner.OnItemSelectListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterRecipeItem
import com.mykameal.planner.adapter.ChooseDayAdapter
import com.mykameal.planner.adapter.CookWareAdapter
import com.mykameal.planner.adapter.IngredientsRecipeAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentRecipeDetailsBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.RecipeDetailsViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.Data
import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.RecipeDetailsApiResponse
import com.mykameal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykameal.planner.messageclass.ErrorMessage
import com.mykameal.planner.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class RecipeDetailsFragment : Fragment(), OnItemSelectListener {

    private var binding: FragmentRecipeDetailsBinding? = null
    private var ingredientsRecipeAdapter: IngredientsRecipeAdapter? = null
    private var cookWareAdapter: CookWareAdapter? = null
    private var adapterRecipeItem: AdapterRecipeItem? = null
    val dataList = ArrayList<DataModel>()
    private val dataList1 = ArrayList<DataModel>()
    private val dataList2 = ArrayList<DataModel>()
    private var rcyChooseDaySch: RecyclerView? = null
    private var tvWeekRange: TextView? = null
    private var chooseDayAdapter: ChooseDayAdapter? = null
    private val calendar = Calendar.getInstance()
    private var selectAll: Boolean = false
    private var quantity: Int = 1
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private lateinit var viewModel: RecipeDetailsViewModel
    private var uri: String = ""
    private var localData: MutableList<Data> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecipeDetailsBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RecipeDetailsViewModel::class.java]

        uri = arguments?.getString("uri", "").toString()

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.GONE

        setupBackNavigation()


        ingredientsModel()
        initialize()

        // When screen load then api call
        fetchDataOnLoad()

        return binding!!.root
    }

    private fun fetchDataOnLoad() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchRecipeDetailsData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchRecipeDetailsData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.recipeDetailsRequest({
                BaseApplication.dismissMe()
                handleApiResponse(it)
            }, uri)
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleBasketApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessBasketResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessBasketResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                 Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
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

    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, RecipeDetailsApiResponse::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null && apiModel.data.size > 0) {
                    showData(apiModel.data)
                }else{
                    binding!!.layBottom.visibility = View.GONE
                    binding!!.webView.visibility = View.GONE
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

    @SuppressLint("SetTextI18n")
    private fun showData(data: MutableList<Data>) {
        localData.clear()
        localData.addAll(data)

        if (localData[0].recipe?.images?.SMALL?.url != null) {
            Glide.with(requireContext())
                .load(localData[0].recipe?.images?.SMALL?.url)
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

        if (localData[0].recipe?.label != null) {
            binding!!.tvTitle.text = "" + localData[0].recipe?.label
        }

        if (localData[0].recipe?.calories != null) {
            binding!!.tvCalories.text = "" + localData[0].recipe?.calories?.toInt()
        }

        if (localData[0].recipe?.totalNutrients?.FAT?.quantity != null) {
            binding!!.tvFat.text = "" + localData[0].recipe?.totalNutrients?.FAT?.quantity?.toInt()
        }

        if (localData[0].recipe?.totalNutrients?.PROCNT?.quantity != null) {
            binding!!.tvProtein.text =
                "" + localData[0].recipe?.totalNutrients?.PROCNT?.quantity?.toInt()
        }

        if (localData[0].recipe?.totalNutrients?.CHOCDF?.quantity != null) {
            binding!!.tvCarbs.text =
                "" + localData[0].recipe?.totalNutrients?.CHOCDF?.quantity?.toInt()
        }


        if (localData[0].recipe?.totalTime != null) {
            binding!!.tvTotaltime.text = "" + localData[0].recipe?.totalTime + " min "
        }

        if (localData[0].recipe?.ingredients != null && localData[0].recipe?.ingredients!!.size > 0) {
            ingredientsRecipeAdapter =
                IngredientsRecipeAdapter(localData[0].recipe?.ingredients, requireActivity(), this)
            binding!!.rcyIngCookWareRecipe.adapter = ingredientsRecipeAdapter
            binding!!.layBottom.visibility = View.VISIBLE
        }else{
            binding!!.layBottom.visibility = View.GONE
        }


    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }


    private fun initialize() {

        binding!!.imgPlusValue.setOnClickListener {
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        binding!!.imgMinusValue.setOnClickListener {
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

        binding!!.tvAddToPlan.setOnClickListener {
            chooseDayDialog()
        }

        binding!!.relBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.llIngredients.setOnClickListener {
            binding!!.textIngredients.setBackgroundResource(R.drawable.select_bg)
            binding!!.textCookWare.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textRecipe.setBackgroundResource(R.drawable.unselect_bg)

            binding!!.textIngredients.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.textCookWare.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textRecipe.setTextColor(Color.parseColor("#3C4541"))

            /*binding!!.relServingsPeople.visibility = View.VISIBLE
            binding!!.layBottomPlanBasket.visibility = View.VISIBLE
            binding!!.relIngSelectAll.visibility = View.VISIBLE*/
            /* binding!!.relCookware.visibility = View.GONE
             binding!!.relRecipe.visibility = View.GONE
             binding!!.textStepInstructions.visibility = View.GONE*/
            binding!!.layBottom.visibility = View.VISIBLE
            binding!!.webView.visibility = View.GONE
            if (localData.size > 0) {
                // Update the drawable based on the selectAll state
                val drawableRes =
                    if (selectAll) R.drawable.orange_checkbox_images else R.drawable.orange_uncheck_box_images
                binding?.tvSelectAllBtn?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    drawableRes,
                    0
                )
                // Notify adapter with updated data
                ingredientsRecipeAdapter?.updateList(localData[0].recipe?.ingredients!!)
                binding!!.rcyIngCookWareRecipe.adapter = ingredientsRecipeAdapter
            }
        }

        binding!!.llCookWare.setOnClickListener {
            binding!!.textIngredients.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textCookWare.setBackgroundResource(R.drawable.select_bg)
            binding!!.textRecipe.setBackgroundResource(R.drawable.unselect_bg)

            binding!!.textIngredients.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textCookWare.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.textRecipe.setTextColor(Color.parseColor("#3C4541"))

            /*binding!!.relServingsPeople.visibility = View.GONE
            binding!!.layBottomPlanBasket.visibility = View.GONE
            binding!!.relIngSelectAll.visibility = View.GONE*/
            /* binding!!.relCookware.visibility = View.VISIBLE
             binding!!.relRecipe.visibility = View.GONE
             binding!!.textStepInstructions.visibility = View.GONE*/
            binding!!.layBottom.visibility = View.GONE

            loadUrl()
//            cookWareModel()

        }

        binding!!.llRecipe.setOnClickListener {
            binding!!.textIngredients.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textCookWare.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textRecipe.setBackgroundResource(R.drawable.select_bg)

            binding!!.textIngredients.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textCookWare.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textRecipe.setTextColor(Color.parseColor("#FFFFFF"))

            /*  binding!!.relServingsPeople.visibility = View.GONE
              binding!!.layBottomPlanBasket.visibility = View.GONE
              binding!!.relIngSelectAll.visibility = View.GONE*/
            /* binding!!.relCookware.visibility = View.GONE
             binding!!.relRecipe.visibility = View.VISIBLE
             binding!!.textStepInstructions.visibility = View.VISIBLE*/

            binding!!.layBottom.visibility = View.GONE
            loadUrl()
//            recipeModel()

        }



        binding!!.textStepInstructions.setOnClickListener {
            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
        }

        binding!!.tvSelectAllBtn.setOnClickListener {
            if (localData.size > 0) {
                selectAll = !selectAll // Toggle the selectAll value
                // Update the drawable based on the selectAll state
                val drawableRes =
                    if (selectAll) R.drawable.orange_checkbox_images else R.drawable.orange_uncheck_box_images
                binding?.tvSelectAllBtn?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    drawableRes,
                    0
                )

                // Update the status of each ingredient dynamically
                localData[0].recipe?.ingredients?.forEach { ingredient ->
                    ingredient.status = selectAll
                }
                // Notify adapter with updated data
                ingredientsRecipeAdapter?.updateList(localData[0].recipe?.ingredients!!)
            }
        }


        binding!!.layBasket.setOnClickListener {

            if (BaseApplication.isOnline(requireActivity())) {
                if (localData.size>0){
                    try {
                        // Create a JsonObject for the main JSON structure
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("serving", binding!!.tvValues.text.toString())
                        // Create a JsonArray for ingredients
                        val jsonArray = JsonArray()
                        // Iterate through the ingredients and add them to the array if status is true
                        localData[0].recipe?.ingredients?.forEach { ingredientsModel ->
                            if (ingredientsModel.status) {
                                // Create a JsonObject for each ingredient
                                val ingredientObject = JsonObject()
                                ingredientObject.addProperty("name", ingredientsModel.text)
                                ingredientObject.addProperty("image", ingredientsModel.image)
                                ingredientObject.addProperty("food", ingredientsModel.food)
                                ingredientObject.addProperty("quantity", ingredientsModel.quantity)
                                ingredientObject.addProperty("foodCategory", ingredientsModel.foodCategory)
                                ingredientObject.addProperty("measure", ingredientsModel.measure)
                                // Add the ingredient object to the array
                                jsonArray.add(ingredientObject)
                            }
                        }
                        // Add the ingredients array to the main JSON object
                        jsonObject.add("ingredients", jsonArray)
                        // Log the final JSON data
                        Log.d("final data", "******$jsonObject")
                        addBasketDetailsApi(jsonObject)
                    }catch (e:Exception){
                        BaseApplication.alertError(requireContext(), e.message, false)
                    }
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun addBasketDetailsApi(jsonObject: JsonObject) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.recipeAddBasketRequest({
                BaseApplication.dismissMe()
                handleBasketApiResponse(it)
            }, jsonObject)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl() {
        binding!!.webView.visibility = View.VISIBLE
        if (localData.size > 0) {
            val webSettings: WebSettings = binding!!.webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.loadsImagesAutomatically = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.allowContentAccess = true
            webSettings.allowFileAccess = true
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // Set WebViewClient to handle page loading within the WebView
            binding!!.webView.webViewClient = WebViewClient()

           // Load the URL if it is not null or empty
            val url = localData[0].recipe?.url?.replace("http:", "https:")
            Log.d("url", "****$url")
            if (!url.isNullOrEmpty()) {
                binding!!.webView.loadUrl(url)
            } else {
                Log.e("WebViewError", "URL is null or empty")
            }

        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateValue() {
        binding!!.tvValues.text = String.format("%02d", quantity)
    }

    private fun chooseDayDialog() {
        val dialogChooseDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseDay.setContentView(R.layout.alert_dialog_choose_day)
        dialogChooseDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rcyChooseDaySch = dialogChooseDay.findViewById(R.id.rcyChooseDaySch)
        tvWeekRange = dialogChooseDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseDay.show()
        updateWeekRange()
        dialogChooseDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        cookingScheduleModel()

        rlDoneBtn.setOnClickListener {
            chooseDayMealTypeDialog()
            dialogChooseDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }
    }

    private fun changeWeek(weeks: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        updateWeekRange()
    }

    private fun cookingScheduleModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()

        data1.title = "Monday"
        data1.isOpen = false
        data1.type = "CookingSchedule"

        data2.title = "Tuesday"
        data2.isOpen = false
        data2.type = "CookingSchedule"

        data3.title = "Wednesday"
        data3.isOpen = false
        data3.type = "CookingSchedule"

        data4.title = "Thursday"
        data4.isOpen = false
        data4.type = "CookingSchedule"

        data5.title = "Friday"
        data5.isOpen = false
        data5.type = "CookingSchedule"

        data6.title = "Saturday"
        data6.isOpen = false
        data6.type = "CookingSchedule"

        data7.title = "Sunday"
        data7.isOpen = false
        data7.type = "CookingSchedule"

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)

        chooseDayAdapter = ChooseDayAdapter(dataList, requireActivity())
        rcyChooseDaySch!!.adapter = chooseDayAdapter

    }

    @SuppressLint("SetTextI18n")
    private fun updateWeekRange() {
        val startOfWeek = calendar.apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }.time

        val endOfWeek = calendar.apply {
            add(Calendar.DAY_OF_WEEK, 6)
        }.time

        tvWeekRange!!.text = "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"
        calendar.add(Calendar.DAY_OF_WEEK, -6) // Reset endOfWeek calculation
    }

    private fun chooseDayMealTypeDialog() {
        val dialogChooseMealDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseMealDay.setContentView(R.layout.alert_dialog_choose_day_meal_type)
        dialogChooseMealDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseMealDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvWeekRange = dialogChooseMealDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseMealDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseMealDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseMealDay.findViewById<ImageView>(R.id.btnNext)
        // button event listener
        val tvBreakfast = dialogChooseMealDay.findViewById<TextView>(R.id.tvBreakfast)
        val tvLunch = dialogChooseMealDay.findViewById<TextView>(R.id.tvLunch)
        val tvDinner = dialogChooseMealDay.findViewById<TextView>(R.id.tvDinner)
        val tvSnacks = dialogChooseMealDay.findViewById<TextView>(R.id.tvSnacks)
        val tvTeatime = dialogChooseMealDay.findViewById<TextView>(R.id.tvTeatime)





        dialogChooseMealDay.show()
        updateWeekRange()
        dialogChooseMealDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        var type = ""

        fun updateSelection(selectedType: String, selectedView: TextView, allViews: List<TextView>) {
            type = selectedType
            allViews.forEach { view ->
                val drawable = if (view == selectedView) R.drawable.radio_select_icon else R.drawable.radio_unselect_icon
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
            }
        }

        val allViews = listOf(tvBreakfast, tvLunch, tvDinner, tvSnacks, tvTeatime)

        tvBreakfast.setOnClickListener {
            updateSelection("Breakfast", tvBreakfast, allViews)
        }

        tvLunch.setOnClickListener {
            updateSelection("Lunch", tvLunch, allViews)
        }

        tvDinner.setOnClickListener {
            updateSelection("Dinner", tvDinner, allViews)
        }

        tvSnacks.setOnClickListener {
            updateSelection("Snacks", tvSnacks, allViews)
        }

        tvTeatime.setOnClickListener {
            updateSelection("Teatime", tvTeatime, allViews)
        }

        rlDoneBtn.setOnClickListener {

            dialogChooseMealDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }
    }

    private fun ingredientsModel() {
        dataList.clear()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Olive Oil"
        data1.description = "1 Tbsp"
        data1.isOpen = false
        data1.type = "IngDetails"
        data1.image = R.drawable.olive_image

        data2.title = "Garlic Mayo"
        data2.description = "3 Tbsp"
        data2.isOpen = false
        data2.type = "IngDetails"
        data2.image = R.drawable.garlic_mayo_image

        data3.title = "Olive Oil"
        data3.description = "3 Tbsp"
        data3.isOpen = false
        data3.type = "IngDetails"
        data3.image = R.drawable.olive_oil_image2

        data4.title = "Olive Oil"
        data4.description = "1 kg"
        data4.isOpen = false
        data4.type = "IngDetails"
        data4.image = R.drawable.olive_chicken_image

        data5.title = "Tomato"
        data5.description = "0.5 kg"
        data5.isOpen = false
        data5.type = "IngDetails"
        data5.image = R.drawable.tomato_ing_image

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)


        /*ingredientsRecipeAdapter = IngredientsRecipeAdapter(dataList, requireActivity())
        binding!!.rcyIngCookWareRecipe.adapter = ingredientsRecipeAdapter*/
    }

    private fun cookWareModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Cooker"
        data1.isOpen = false
        data1.type = "CookWare"
        data1.image = R.drawable.cooker_image

        data2.title = "Cutting board"
        data2.isOpen = false
        data2.type = "CookWare"
        data2.image = R.drawable.cutting_board_image

        data3.title = "Grater"
        data3.isOpen = false
        data3.type = "CookWare"
        data3.image = R.drawable.grater_image

        data4.title = "Peeler"
        data4.isOpen = false
        data4.type = "CookWare"
        data4.image = R.drawable.peeler_image

        data5.title = "Mixing bowl"
        data5.isOpen = false
        data5.type = "CookWare"
        data5.image = R.drawable.mixing_bowl_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)
        dataList1.add(data4)
        dataList1.add(data5)

        cookWareAdapter = CookWareAdapter(dataList1, requireActivity())
        binding!!.rcyIngCookWareRecipe.adapter = cookWareAdapter
    }

    private fun recipeModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()

        data1.title = "Step 1:"
        data1.description = "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispum"
        data1.isOpen = false
        data1.type = "Recipe"

        data2.title = "Step 2:"
        data2.description =
            "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum Lorem ispum"
        data2.isOpen = false
        data2.type = "Recipe"

        data3.title = "Step 3:"
        data3.description =
            "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum Lorem ispumLorem ispum Lorem ispum"
        data3.isOpen = false
        data3.type = "Recipe"

        data4.title = "Step 4:"
        data4.description =
            "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum"
        data4.isOpen = false
        data4.type = "Recipe"

        dataList2.add(data1)
        dataList2.add(data2)
        dataList2.add(data3)
        dataList2.add(data4)

        adapterRecipeItem = AdapterRecipeItem(dataList2, requireActivity())
        binding!!.rcyIngCookWareRecipe.adapter = adapterRecipeItem
    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {

        localData[0].recipe?.ingredients?.forEachIndexed { index, ingredient ->
            if (index == position) {
                ingredient.status = localData[0].recipe?.ingredients?.get(position)?.status != true
            }
        }
        // Notify adapter with updated data
        ingredientsRecipeAdapter?.updateList(localData[0].recipe?.ingredients!!)

        selectAll = localData[0].recipe?.ingredients?.all { it.status } == true

        // Update the drawable based on the selectAll state
        val drawableRes =
            if (selectAll) R.drawable.orange_checkbox_images else R.drawable.orange_uncheck_box_images
        binding?.tvSelectAllBtn?.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

    }

}
package com.mykaimeal.planner.fragment.mainfragment.cookedtab.addmealfragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.activity.SplashActivity
import com.mykaimeal.planner.adapter.SearchAdapterItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentAddMealCookedBinding
import com.mykaimeal.planner.fragment.mainfragment.cookedtab.addmealfragment.viewmodel.AddMealCookedViewModel
import com.mykaimeal.planner.fragment.mainfragment.plantab.ImagesDeserializer
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.model.Recipe
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.model.SearchModel
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.model.SearchModelData
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.ImagesModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddMealCookedFragment : Fragment(),OnItemClickListener {
    private var binding: FragmentAddMealCookedBinding?=null
    private lateinit var addMealCookedViewModel: AddMealCookedViewModel
    private var searchAdapterItem:SearchAdapterItem?=null
    private var recipes: List<Recipe>?=null
    private var quantity: Int = 1
    private var lastSelectedDate: Long? = null
    private var selectedDate:String=""
    private var status:String=""
    private var clickable:String=""
    private var mealType:String=""
    private var recipeUri:String=""
    private var planType:String="1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddMealCookedBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        addMealCookedViewModel = ViewModelProvider(this)[AddMealCookedViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialize() {

        binding!!.relDateCalendar.setOnClickListener{
            openDialog()
        }

        binding!!.imageBackAddMeal.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.textFridge.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFridge.setTextColor(Color.WHITE)
            binding!!.textFreezer.setTextColor(Color.BLACK)
            planType="1"

            if (status=="2"){
                binding!!.textFridge.text="Fridge (1)"
                binding!!.textFreezer.text="Freezer (0)"
            }else{
                binding!!.textFridge.text="Fridge (0)"
                binding!!.textFreezer.text="Freezer (0)"
            }

        }

        binding!!.textFreezer.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFridge.setTextColor(Color.BLACK)
            binding!!.textFreezer.setTextColor(Color.WHITE)
            planType="2"

            if (status=="2"){
                binding!!.textFridge.text="Fridge (0)"
                binding!!.textFreezer.text="Freezer (1)"
            }else{
                binding!!.textFridge.text="Fridge (0)"
                binding!!.textFreezer.text="Freezer (0)"
            }

        }

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
                Toast.makeText(requireActivity(), "Minimum serving atleast value is one", Toast.LENGTH_LONG).show()
            }
        }

        binding!!.relSearch.setOnClickListener{
            binding!!.cardViewSearchRecipe.visibility=View.VISIBLE
            if (BaseApplication.isOnline(requireActivity())) {
                searchRecipeApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        binding!!.testAddMeals.setOnClickListener{
            if (clickable=="2"){
                if (BaseApplication.isOnline(requireActivity())) {
                    addMealsApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

        }

    }

    private fun addMealsApi() {
        // Create a JsonObject for the main JSON structure
        val jsonObject = JsonObject()
        if (recipeUri!= null) {
            jsonObject.addProperty("type", mealType)
            jsonObject.addProperty("plan_type", planType)
            jsonObject.addProperty("uri", recipeUri)
            jsonObject.addProperty("date", selectedDate)
            jsonObject.addProperty("serving", String.format("%02d", quantity))
        }

        Log.d("json object ", "******$jsonObject")

        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            addMealCookedViewModel.recipeAddToPlanRequest({
                BaseApplication.dismissMe()
                handleApiAddToPlanResponse(it)
            }, jsonObject)
        }
    }

    private fun handleApiAddToPlanResponse(
        result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessAddToPlanResponse(
                result.data.toString())

            is NetworkResult.Error -> showAlertFunction(result.message, false)
            else -> showAlertFunction(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessAddToPlanResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMeal List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                binding!!.imageLogo.setImageResource(R.drawable.add_meal_icon_success)

                lifecycleScope.launch {
                    delay(SplashActivity.SPLASH_DELAY)
                    findNavController().navigate(R.id.cookedFragment)
                }
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlertFunction(apiModel.message, true)
                } else {
                    showAlertFunction(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlertFunction(e.message, false)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateValue() {
        binding!!.tvServing.text = "serves"+String.format("%02d", quantity)
    }

    private fun openDialog(){

        val dialog = Dialog(requireActivity())
        // Set custom layout
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)

        dialog.setOnShowListener {
            calendarView?.date = lastSelectedDate ?: Calendar.getInstance().timeInMillis
        }
        // Get today's date
        val today = Calendar.getInstance()
       // Set the minimum date to today
        calendarView?.minDate = today.timeInMillis

        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Create a Calendar instance
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            calendar.set(year, month, dayOfMonth)
            lastSelectedDate = calendar.timeInMillis // Store the selected date

            // Format the selected date to "YYYY-MM-DD"
            val dateFormatForApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDateApi = dateFormatForApi.format(calendar.time)

            selectedDate=formattedDateApi
            // Format the date to "17 January 2025"
            val dateFormatForShow = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            val formattedDateShow = dateFormatForShow.format(calendar.time)

            binding!!.tvDateCooked.text=formattedDateShow

            checkable()
            // Dismiss the dialog
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun checkable() {
        if (selectedDate!=""){
            if (status=="2"){
                clickable="2"
                binding!!.testAddMeals.setBackgroundResource(R.drawable.green_btn_background)
            }else{
                binding!!.testAddMeals.setBackgroundResource(R.drawable.gray_btn_unselect_background)
            }
        }else{
            binding!!.testAddMeals.setBackgroundResource(R.drawable.gray_btn_unselect_background)
        }
    }

    private fun searchRecipeApi() {
        binding!!.layProgress.root.visibility=View.VISIBLE
        lifecycleScope.launch {
            addMealCookedViewModel.recipeSearchApi({
                binding!!.layProgress.root.visibility=View.GONE
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                          /*  val gson = Gson()*/
                            val gson = GsonBuilder()
                                .registerTypeAdapter(ImagesModel::class.java, ImagesDeserializer())
                                .create()

                            val searchModel = gson.fromJson(it.data, SearchModel::class.java)
                            if (searchModel.code == 200 && searchModel.success) {
                                showDataInUi(searchModel.data)
                            } else {
                                if (searchModel.code == ErrorMessage.code) {
                                    showAlertFunction(searchModel.message, true)
                                }else{
                                    showAlertFunction(searchModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("AddMeal","message:--"+e.message)
                        }
                    }
                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }
                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            },binding!!.etCookedDishes.text.toString().trim())
        }
    }

    private fun showDataInUi(searchModelData: SearchModelData) {
        try {
            if (searchModelData!=null){
                if (searchModelData.recipes!=null && searchModelData.recipes.size>0){
                    recipes=searchModelData.recipes
                    binding!!.rcySearchCooked.visibility=View.VISIBLE
                    binding!!.tvNoData.visibility=View.GONE
                    searchAdapterItem = SearchAdapterItem(searchModelData.recipes, requireActivity(),this)
                    binding!!.rcySearchCooked.adapter = searchAdapterItem
                }else{
                    binding!!.rcySearchCooked.visibility=View.GONE
                    binding!!.tvNoData.visibility=View.VISIBLE
                }
            }
        }catch (e:Exception){
            Log.d("AddMeal","message:--"+e.message)
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun itemClick(position: Int?, uri: String?, type: String?) {

        if (planType=="1"){
            binding!!.textFridge.text="Fridge (1)"
            binding!!.textFreezer.text="Freezer (0)"
        }else{
            binding!!.textFridge.text="Fridge (0)"
            binding!!.textFreezer.text="Freezer (1)"
        }

        mealType=type.toString()
        recipeUri= uri.toString()
        status="2"
        binding!!.cardViewSearchRecipe.visibility=View.GONE
        binding!!.cardViewRecipe.visibility=View.VISIBLE

        if (recipes!![position!!].recipe!=null){
            if (recipes!![position].recipe?.image!=null){
                if (recipes!![position].recipe?.images?.SMALL?.url!=null){
                    val imageUrl = recipes!![position].recipe?.images?.SMALL?.url
                    Glide.with(requireActivity())
                        .load(imageUrl)
                        .error(R.drawable.no_image)
                        .placeholder(R.drawable.no_image)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding!!.layProgess.root.visibility= View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding!!.layProgess.root.visibility= View.GONE
                                return false
                            }
                        })
                        .into(binding!!.imgIngRecipe)


                    Glide.with(requireActivity())
                        .load(imageUrl)
                        .error(R.drawable.add_meal_icon)
                        .placeholder(R.drawable.add_meal_icon)
                        .into(binding!!.imageLogo)
                }else{
                    binding!!.layProgess.root.visibility= View.GONE
                }
            }
        }

        binding!!.tvTitleName.text=type.toString()

        checkable()

       /* recipes!![position!!].recipe.image*/

    }
}
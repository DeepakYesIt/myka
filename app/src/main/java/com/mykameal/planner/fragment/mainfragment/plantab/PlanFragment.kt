package com.mykameal.planner.fragment.mainfragment.plantab

import PlanApiResponse
import PlanByDateApiResponse
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
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.yesitlabs.mykaapp.OnItemSelectPlanTypeListener
import com.yesitlabs.mykaapp.adapter.AdapterPlanBreakByDateFast
import com.mykameal.planner.adapter.CalendarDayDateAdapter
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate.BreakfastModelPlanByDate
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate.DataPlayByDate
import com.yesitlabs.mykaapp.model.DateModel
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterPlanBreakFast
import com.mykameal.planner.adapter.ChooseDayAdapter
import com.mykameal.planner.adapter.ImageViewPagerAdapter
import com.mykameal.planner.apiInterface.BaseUrl
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.databinding.FragmentPlanBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.PlanViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.BreakfastModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.Data
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.RecipesModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykameal.planner.messageclass.ErrorMessage
import com.mykameal.planner.model.CalendarDataModel
import com.mykameal.planner.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class PlanFragment : Fragment(), OnItemClickListener, OnItemSelectPlanTypeListener {

    private var binding: FragmentPlanBinding? = null
    private val calendar = Calendar.getInstance()
    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var dataList2: MutableList<DataModel> = mutableListOf()
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var tvWeekRange: TextView? = null
    private var planBreakFastAdapter: AdapterPlanBreakFast? = null
    private var status: Boolean = true
    private var clickable: String? = ""
    private lateinit var viewModel: PlanViewModel
    private var recipesModel: RecipesModel? = null
    private var recipesDateModel: DataPlayByDate? = null

    // Separate adapter instances for each RecyclerView
    var breakfastAdapter: AdapterPlanBreakFast? = null
    var AdapterPlanBreakByDateFast: AdapterPlanBreakByDateFast? = null
    var lunchAdapter: AdapterPlanBreakFast? = null
    var AdapterlunchByDateFast: AdapterPlanBreakByDateFast? = null
    var dinnerAdapter: AdapterPlanBreakFast? = null
    var AdapterdinnerByDateFast: AdapterPlanBreakByDateFast? = null
    var snackesAdapter: AdapterPlanBreakFast? = null
    var AdaptersnackesByDateFast: AdapterPlanBreakByDateFast? = null
    var teaTimeAdapter: AdapterPlanBreakFast? = null
    var AdapterteaTimeByDateFast: AdapterPlanBreakByDateFast? = null
    private lateinit var sessionManagement: SessionManagement

    lateinit var adapter: ImageViewPagerAdapter
    val dataList = arrayListOf<DataModel>()
    private lateinit var layonboarding_indicator: LinearLayout
    var currentDate = Date() // Current date

    // Define global variables
    private lateinit var startDate: Date
    private lateinit var endDate: Date

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[PlanViewModel::class.java]
        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE
        sessionManagement = SessionManagement(requireContext())
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        (activity as MainActivity?)?.changeBottom("plan")

        if (sessionManagement.getImage()!=null){
            Glide.with(requireContext())
                .load(BaseUrl.imageBaseUrl+sessionManagement.getImage())
                .placeholder(R.drawable.mask_group_icon)
                .error(R.drawable.mask_group_icon)
                .into(binding!!.imageProfile)
        }


        if (sessionManagement.getUserName()!=null){
            binding?.tvName?.text =sessionManagement.getUserName()+"’s week"
        }


        planBreakFastModel()
        planLunchModel()
        planDinnerModel()

        setUpListener()

        // When screen load then api call
        fetchDataOnLoad()

        val imageList = Arrays.asList<Int>(
            R.drawable.ic_food_image,
            R.drawable.ic_food_image,
            R.drawable.ic_food_image
        )
        adapter = ImageViewPagerAdapter(requireContext(), imageList)


        // Display current week dates
        showWeekDates()


        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    fun showWeekDates() {
        Log.d("currentDate :- ", "******$currentDate")
        val (startDate, endDate) = getWeekDates(currentDate)
        this.startDate=startDate
        this.endDate=endDate

        println("Week Start Date: ${formatDate(startDate)}")
        println("Week End Date: ${formatDate(endDate)}")

        // Get all dates between startDate and endDate
        val daysBetween = getDaysBetween(startDate, endDate)

        // Print the dates
        println("Days between ${startDate} and ${endDate}:")
        daysBetween.forEach { println(it) }
        binding!!.tvDate.text = BaseApplication.formatonlyMonthYear(startDate)
        binding!!.textWeekRange.text = ""+formatDate(startDate)+"-"+formatDate(endDate)

        tvWeekRange?.text = ""+formatDate(startDate)+"-"+formatDate(endDate)
        // Update the RecyclerView
        binding!!.recyclerViewWeekDays.adapter = CalendarDayDateAdapter(getDaysBetween(startDate, endDate)) {
            // Handle item click if needed
            if (BaseApplication.isOnline(requireActivity())) {
                dataFatchByDate(it)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }

        }

    }

    private fun dataFatchByDate(date: String) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.planDateRequest({
                BaseApplication.dismissMe()
                handleApiPlanDateResponse(it)
            }, date)
        }
    }


    private fun getDaysBetween(startDate: Date, endDate: Date): MutableList<DateModel> {
        val dateList = mutableListOf<DateModel>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format for the date
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Format for the day name (e.g., Monday)

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            val date = dateFormat.format(calendar.time)  // Get the formatted date (yyyy-MM-dd)
            val dayName = dayFormat.format(calendar.time)  // Get the day name (Monday, Tuesday, etc.)

            val localDate= DateModel()
            localDate.day=dayName
            localDate.date=date
            // Combine both the day name and the date
//            dateList.add("$dayName, $date")
            dateList.add(localDate)


            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dateList
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
            viewModel.planRequest({
                BaseApplication.dismissMe()
                handleApiResponse(it)
            }, "q")
        }
    }

    private fun handleApiPlanDateResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessPlanDateResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }
    private fun handleApiAddToPlanResponse(
        result: NetworkResult<String>,
        dialogChooseMealDay: Dialog
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessAddToPlanResponse(result.data.toString(),dialogChooseMealDay)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, PlanApiResponse::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
                    showData(apiModel.data)
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
    private fun handleSuccessPlanDateResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, PlanByDateApiResponse::class.java)
            Log.d("@@@ PlanDate List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
                    showDataAccordingDate(apiModel.data)
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
    private fun handleSuccessAddToPlanResponse(data: String, dialogChooseMealDay: Dialog) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                 dataList.clear()
                 dialogChooseMealDay.dismiss()
                 Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()
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
    private fun handleLikeAndUnlikeSuccessResponse(
        data: String,
        item: BreakfastModel,
        adapter: AdapterPlanBreakFast?,
        type: String,
        mealList: MutableList<BreakfastModel>,
        position: Int?
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                item.is_like = if (item.is_like == 0) 1 else 0
                mealList[position!!] = item
                // Update the adapter
                adapter?.updateList(mealList, type)
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
    private fun handleBasketSuccessResponse(
        data: String
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()
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



    private fun showData(data: Data) {

        recipesModel = data.recipes

        if (recipesModel != null) {
            fun setupMealAdapter(
                mealRecipes: MutableList<BreakfastModel>?,
                recyclerView: RecyclerView,
                type: String
            ): AdapterPlanBreakFast? {
                return if (mealRecipes != null && mealRecipes.isNotEmpty()) {
                    val adapter = AdapterPlanBreakFast(mealRecipes, requireActivity(), this, type)
                    recyclerView.adapter = adapter
                    adapter
                } else {
                    null
                }
            }

            // Breakfast
            if (recipesModel?.Breakfast != null && recipesModel?.Breakfast?.size!! > 0) {
                breakfastAdapter = setupMealAdapter(recipesModel?.Breakfast, binding!!.rcyBreakFast, "BreakFast")
                binding!!.linearBreakfast.visibility = View.VISIBLE
            } else {
                binding!!.linearBreakfast.visibility = View.GONE
            }

            // Lunch
            if (recipesModel?.Lunch != null && recipesModel?.Lunch?.size!! > 0) {
                lunchAdapter = setupMealAdapter(recipesModel?.Lunch, binding!!.rcyLunch, "Lunch")
                binding!!.linearLunch.visibility = View.VISIBLE
            } else {
                binding!!.linearLunch.visibility = View.GONE
            }

            // Dinner
            if (recipesModel?.Dinner != null && recipesModel?.Dinner?.size!! > 0) {
                dinnerAdapter = setupMealAdapter(recipesModel?.Dinner, binding!!.rcyDinner, "Dinner")
                binding!!.linearDinner.visibility = View.VISIBLE
            } else {
                binding!!.linearDinner.visibility = View.GONE
            }


            // Snacks
            if (recipesModel?.Snack != null && recipesModel?.Snack?.size!! > 0) {
                snackesAdapter = setupMealAdapter(recipesModel?.Snack, binding!!.rcySnacks, "Snacks")
                binding!!.linearSnacks.visibility = View.VISIBLE
            } else {
                binding!!.linearSnacks.visibility = View.GONE
            }

            // TeaTime
            if (recipesModel?.Teatime != null && recipesModel?.Teatime?.size!! > 0) {
                teaTimeAdapter = setupMealAdapter(recipesModel?.Teatime, binding!!.rcyTeatime, "TeaTime")
                binding!!.linearTeatime.visibility = View.VISIBLE
            } else {
                binding!!.linearTeatime.visibility = View.GONE
            }

        }

    }


    private fun showDataAccordingDate(data: DataPlayByDate?) {

        recipesDateModel= null
        recipesDateModel = data

        if (recipesDateModel != null) {
            fun setupMealAdapter(mealRecipes: MutableList<BreakfastModelPlanByDate>?, recyclerView: RecyclerView, type: String): AdapterPlanBreakByDateFast? {
                return if (mealRecipes != null && mealRecipes.isNotEmpty()) {
                    val adapter = AdapterPlanBreakByDateFast(mealRecipes, requireActivity(), this, type)
                    recyclerView.adapter = adapter
                    adapter
                } else {
                    null
                }
            }

            fun setupMealTopAdapter(
                mealRecipes: MutableList<BreakfastModel>?,
                recyclerView: RecyclerView,
                type: String
            ): AdapterPlanBreakFast? {
                return if (mealRecipes != null && mealRecipes.isNotEmpty()) {
                    val adapter = AdapterPlanBreakFast(mealRecipes, requireActivity(), this, type)
                    recyclerView.adapter = adapter
                    adapter
                } else {
                    null
                }
            }

            // Breakfast
            if (recipesDateModel?.Breakfast != null && recipesDateModel?.Breakfast?.size!! > 0) {
                AdapterPlanBreakByDateFast = setupMealAdapter(recipesDateModel!!.Breakfast!!, binding!!.rcyBreakFast, "BreakFast")
                binding!!.linearBreakfast.visibility = View.VISIBLE
            } else {
                // Breakfast
                if (recipesModel?.Breakfast != null && recipesModel?.Breakfast?.size!! > 0) {
                    breakfastAdapter = setupMealTopAdapter(recipesModel?.Breakfast, binding!!.rcyBreakFast, "BreakFast")
                    binding!!.linearBreakfast.visibility = View.VISIBLE
                } else {
                    binding!!.linearBreakfast.visibility = View.GONE
                }
            }

            // Lunch
            if (recipesDateModel?.Lunch != null && recipesDateModel?.Lunch?.size!! > 0) {
                AdapterlunchByDateFast = setupMealAdapter(data?.Lunch, binding!!.rcyLunch, "Lunch")
                binding!!.linearLunch.visibility = View.VISIBLE
            } else {
                if (recipesModel?.Lunch != null && recipesModel?.Lunch?.size!! > 0) {
                    lunchAdapter = setupMealTopAdapter(recipesModel?.Lunch, binding!!.rcyLunch, "Lunch")
                    binding!!.linearLunch.visibility = View.VISIBLE
                } else {
                    binding!!.linearLunch.visibility = View.GONE
                }
            }

            // Dinner
            if (recipesDateModel?.Dinner != null && recipesDateModel?.Dinner?.size!! > 0) {
                AdapterdinnerByDateFast = setupMealAdapter(data?.Dinner, binding!!.rcyDinner, "Dinner")
                binding!!.linearDinner.visibility = View.VISIBLE
            } else {
                // Dinner
                if (recipesModel?.Dinner != null && recipesModel?.Dinner?.size!! > 0) {
                    dinnerAdapter = setupMealTopAdapter(recipesModel?.Dinner, binding!!.rcyDinner, "Dinner")
                    binding!!.linearDinner.visibility = View.VISIBLE
                } else {
                    binding!!.linearDinner.visibility = View.GONE
                }
            }

            // Snacks
            if (recipesDateModel?.Snack != null && recipesDateModel?.Snack?.size!! > 0) {
                AdaptersnackesByDateFast = setupMealAdapter(data?.Snack, binding!!.rcySnacks, "Snacks")
                binding!!.linearSnacks.visibility = View.VISIBLE
            } else {
                // Snacks
                if (recipesModel?.Snack != null && recipesModel?.Snack?.size!! > 0) {
                    snackesAdapter = setupMealTopAdapter(recipesModel?.Snack, binding!!.rcySnacks, "Snacks")
                    binding!!.linearSnacks.visibility = View.VISIBLE
                } else {
                    binding!!.linearSnacks.visibility = View.GONE
                }
            }

            // TeaTime
            if (recipesDateModel?.Teatime != null && recipesDateModel?.Teatime?.size!! > 0) {
                AdapterteaTimeByDateFast =setupMealAdapter(data?.Teatime, binding!!.rcyTeatime, "TeaTime")
                binding!!.linearTeatime.visibility = View.VISIBLE
            } else {
                // TeaTime
                if (recipesModel?.Teatime != null && recipesModel?.Teatime?.size!! > 0) {
                    teaTimeAdapter = setupMealTopAdapter(recipesModel?.Teatime, binding!!.rcyTeatime, "TeaTime")
                    binding!!.linearTeatime.visibility = View.VISIBLE
                } else {
                    binding!!.linearTeatime.visibility = View.GONE
                }
            }

        }

    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun setUpListener() {
        binding!!.tvAddAnotherMealBtn.setOnClickListener {
            addAnotherMealDialog()
        }

        binding!!.tvSwap.setOnClickListener {
            dialogDailyInspiration()
        }

        binding!!.tvSwap2.setOnClickListener {
            dialogDailyInspiration()
        }

        binding!!.imageProfile.setOnClickListener {
            findNavController().navigate(R.id.settingProfileFragment)
        }

        binding!!.imgHearRedIcons.setOnClickListener {
            findNavController().navigate(R.id.cookBookFragment)
        }

        binding!!.imgBasketIcon.setOnClickListener {
            findNavController().navigate(R.id.basketScreenFragment)
        }

        binding!!.rlAddDayToBasket.setOnClickListener {
            if (clickable == "") {

            } else {
                findNavController().navigate(R.id.basketScreenFragment)
            }
        }

        binding!!.tvConfirmBtn.setOnClickListener {
           /* binding!!.llCalculateBmr.visibility = View.VISIBLE
            binding!!.relBreakFastsss.visibility = View.VISIBLE
            binding!!.relBreakFastsLunch.visibility = View.VISIBLE
            binding!!.rcyBreakFast.visibility = View.GONE
            binding!!.rcyLunch.visibility = View.GONE*/
        }



        binding!!.relMonthYear.setOnClickListener {
            openDialog()
        }

        binding!!.imagePrevious.setOnClickListener {
//            changeWeekRange(-1)
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, -1) // Move to next week
            currentDate = calendar.time

            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }

        binding!!.imageNext.setOnClickListener {
            /*changeWeekRange(1)*/
            // Simulate clicking the "Next" button to move to the next week
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week
            currentDate = calendar.time

            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }

    }


    private fun getWeekDates(currentDate: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        // Set the calendar to the start of the week (Monday)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time

        // Set the calendar to the end of the week (Saturday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time
        return Pair(startOfWeek, endOfWeek)
    }

    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun openDialog(){
        val dialog = Dialog(requireActivity())
        // Set custom layout
        dialog.setContentView(R.layout.dialog_calendar)

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)
        calendarView.setOnDateChangeListener { view: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            val selectedDate = dayOfMonth.toString() + "-"+(month + 1)+"-" + year
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val date = calendar.time  // This is the Date object
            // Format the Date object to the desired string format
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            val currentDateString = dateFormat.format(date)  // This is the formatted string
            // To convert the string back to a Date object:
            currentDate = dateFormat.parse(currentDateString)!!  // This is the Date object
            // Display current week dates
            showWeekDates()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun dialogDailyInspiration() {
        val dialog = Dialog(requireContext(), R.style.BottomSheetDialog)
        dialog.apply {
            setCancelable(true)
            setContentView(R.layout.alert_dialog_daily_inspiration)
            window?.attributes = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }

            layonboarding_indicator = findViewById<LinearLayout>(R.id.layonboarding_indicator)
            val viewPager = findViewById<ViewPager2>(R.id.viewPager)
            val llBreakfast = findViewById<LinearLayout>(R.id.llBreakfast)
            val llLunch = findViewById<LinearLayout>(R.id.llLunch)
            val llDinner = findViewById<LinearLayout>(R.id.llDinner)
            val rlAddPlanButton = findViewById<RelativeLayout>(R.id.rlAddPlanButton)
            val rlAddCartButton = findViewById<RelativeLayout>(R.id.rlAddCartButton)
            val textBreakfast = findViewById<TextView>(R.id.textBreakfast)
            val textDinner = findViewById<TextView>(R.id.textDinner)
            val textLunch = findViewById<TextView>(R.id.textLunch)
            val viewBreakfast = findViewById<View>(R.id.viewBreakfast)
            val viewLunch = findViewById<View>(R.id.viewLunch)
            val viewDinner = findViewById<View>(R.id.viewDinner)

            llBreakfast.setOnClickListener {
                viewBreakfast.visibility = View.VISIBLE
                viewLunch.visibility = View.GONE
                viewDinner.visibility = View.GONE

                textBreakfast.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                textDinner.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            }

            llLunch.setOnClickListener {
                viewBreakfast.visibility = View.GONE
                viewLunch.visibility = View.VISIBLE
                viewDinner.visibility = View.GONE
                textBreakfast.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                textDinner.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            }

            llDinner.setOnClickListener {
                viewBreakfast.visibility = View.GONE
                viewLunch.visibility = View.GONE
                viewDinner.visibility = View.VISIBLE
                textBreakfast.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                textDinner.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
            }

            rlAddPlanButton.setOnClickListener {
                chooseDayDialog(0, "")
                dismiss()
            }

            rlAddCartButton.setOnClickListener {
                findNavController().navigate(R.id.basketScreenFragment)
                dismiss()
            }

            // Set up ViewPager with images
            viewPager.adapter = adapter
            // Set up ViewPager with images
            setUpOnBoardingIndicator()
            currentOnBoardingIndicator(0)
            viewPager.setAdapter(adapter)

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentOnBoardingIndicator(position)
                }
            })


            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            show()
        }
    }



    private fun setUpOnBoardingIndicator() {
        val indicator = arrayOfNulls<ImageView>(adapter!!.getItemCount())
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(10, 0, 10, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView(requireActivity())
            indicator[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.default_dot
                )
            )
            indicator[i]!!.layoutParams = layoutParams
            layonboarding_indicator.addView(indicator[i])
        }
    }

    private fun currentOnBoardingIndicator(index: Int) {
        val childCount: Int = layonboarding_indicator.getChildCount()
        for (i in 0 until childCount) {
            val imageView = layonboarding_indicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.selected_dot
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.default_dot
                    )
                )
            }
        }
    }

    private fun getDaysOfWeek(): List<CalendarDataModel.Day> {

        val days = mutableListOf<CalendarDataModel.Day>()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0..6) {
            days.add(
                CalendarDataModel.Day(
                    dayName = SimpleDateFormat("E", Locale.getDefault()).format(calendar.time),
                    date = calendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        calendar.add(Calendar.DAY_OF_WEEK, -7) // Reset to start of week
        return days

    }

    private fun planBreakFastModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()

        data1.title = "Bread"
        data1.isOpen = false
        data1.rating = "4.1(121)"
        data1.price = "3.2"
        data1.type = "BreakFastPlan"
        data1.image = R.drawable.bread_breakfast_image

        data2.title = "Juice"
        data2.isOpen = false
        data2.rating = "4.4(128)"
        data2.price = "3.4"
        data2.type = "BreakFastPlan"
        data2.image = R.drawable.fresh_juice_glass_image

        data3.title = "Bar-B-Q"
        data3.isOpen = false
        data3.rating = "4.3(125)"
        data3.price = "3.5"
        data3.type = "BreakFastPlan"
        data3.image = R.drawable.bar_b_q_breakfast_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)


    }

    private fun planLunchModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()

        data1.title = "Bread"
        data1.isOpen = false
        data1.rating = "4.1(121)"
        data1.price = "3.2"
        data1.type = "BreakFastPlan"
        data1.image = R.drawable.bread_lunch_image

        data2.title = "Juice"
        data2.isOpen = false
        data2.rating = "4.4(128)"
        data2.price = "3.4"
        data2.type = "BreakFastPlan"
        data2.image = R.drawable.bar_b_q_breakfast_image

        data3.title = "Bar-B-Q"
        data3.isOpen = false
        data3.rating = "4.3(125)"
        data3.price = "3.5"
        data3.type = "BreakFastPlan"
        data3.image = R.drawable.bar_b_q_breakfast_image

        dataList2.add(data1)
        dataList2.add(data2)
        dataList2.add(data3)

//        planBreakFastAdapter = AdapterPlanBreakFast(dataList2, requireActivity(),this)
//        binding!!.rcyLunch.adapter = planBreakFastAdapter
    }

    private fun planDinnerModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()

        data1.title = "Bread"
        data1.isOpen = false
        data1.rating = "4.1(121)"
        data1.price = "3.4"
        data1.type = "BreakFastPlan"
        data1.image = R.drawable.bread_dinner_image

        data2.title = "Juice"
        data2.isOpen = false
        data2.rating = "4.4(128)"
        data2.price = "3.5"
        data2.type = "BreakFastPlan"
        data2.image = R.drawable.fresh_juice_glass_image

        data3.title = "Bar-B-Q"
        data3.isOpen = false
        data3.rating = "4.3(125)"
        data3.price = "3.2"
        data3.type = "BreakFastPlan"
        data3.image = R.drawable.bar_b_q_breakfast_image

        dataList3.add(data1)
        dataList3.add(data2)
        dataList3.add(data3)

//        planBreakFastAdapter = AdapterPlanBreakFast(dataList3, requireActivity(),this)
//        binding!!.rcyDinner.adapter = planBreakFastAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun chooseDayDialog(position: Int?, typeAdapter: String?) {
        val dialogChooseDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseDay.setContentView(R.layout.alert_dialog_choose_day)
        dialogChooseDay.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogChooseDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rcyChooseDaySch = dialogChooseDay.findViewById<RecyclerView>(R.id.rcyChooseDaySch)
        tvWeekRange = dialogChooseDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseDay.show()
        dialogChooseDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        showWeekDates()
        dataList.clear()
        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        for (day in daysOfWeek) {
            val data = DataModel().apply {
                title = day
                isOpen = false
                type = "CookingSchedule"
                date = ""
            }
            dataList.add(data)
        }


        rcyChooseDaySch!!.adapter = ChooseDayAdapter(dataList, requireActivity())


        rlDoneBtn.setOnClickListener {
            chooseDayMealTypeDialog(position,typeAdapter)
            dialogChooseDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, -1) // Move to next week
            currentDate = calendar.time
            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }

        btnNext.setOnClickListener {
            // Simulate clicking the "Next" button to move to the next week
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week
            currentDate = calendar.time
            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }


    }



    private fun cookingScheduleModel() {

    }



    private fun chooseDayMealTypeDialog(position: Int?, typeAdapter: String?) {
        val dialogChooseMealDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseMealDay.setContentView(R.layout.alert_dialog_choose_day_meal_type)
        dialogChooseMealDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseMealDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogChooseMealDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        // button event listener
        val tvBreakfast = dialogChooseMealDay.findViewById<TextView>(R.id.tvBreakfast)
        val tvLunch = dialogChooseMealDay.findViewById<TextView>(R.id.tvLunch)
        val tvDinner = dialogChooseMealDay.findViewById<TextView>(R.id.tvDinner)
        val tvSnacks = dialogChooseMealDay.findViewById<TextView>(R.id.tvSnacks)
        val tvTeatime = dialogChooseMealDay.findViewById<TextView>(R.id.tvTeatime)
        dialogChooseMealDay.show()
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
            if (BaseApplication.isOnline(requireActivity())) {
                addToPlan(dialogChooseMealDay,type,position,typeAdapter)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

    }

    private fun addToPlan(dialogChooseMealDay: Dialog, selectType: String, position: Int?, typeAdapter: String?) {
        // Map the type to the corresponding list and adapter
        val (mealList, adapter) = when (typeAdapter) {
            "BreakFast" -> recipesModel?.Breakfast to breakfastAdapter
            "Lunch" -> recipesModel?.Lunch to lunchAdapter
            "Dinner" -> recipesModel?.Dinner to dinnerAdapter
            "Snacks" -> recipesModel?.Dinner to snackesAdapter
            "TeaTime" -> recipesModel?.Dinner to teaTimeAdapter
            else -> null to null
        }

        // Create a JsonObject for the main JSON structure
        val jsonObject = JsonObject()

        // Safely get the item and position
        val item = mealList?.get(position!!)
        if (item != null) {
            if (item.recipe?.uri!=null){
                jsonObject.addProperty("type", selectType)
                jsonObject.addProperty("uri", item.recipe.uri)
                // Create a JsonArray for ingredients
                val jsonArray = JsonArray()
                val latestList=getDaysBetween(startDate, endDate)
                for (i in dataList.indices) {
                    val data=DataModel()
                    data.isOpen=dataList[i].isOpen
                    data.title=dataList[i].title
                    data.date=latestList[i].date
                    dataList[i] = data
                }
                // Iterate through the ingredients and add them to the array if status is true
                dataList.forEach { data ->
                    if (data.isOpen) {
                        // Create a JsonObject for each ingredient
                        val ingredientObject = JsonObject()
                        ingredientObject.addProperty("date", data.date)

                        ingredientObject.addProperty("day", data.title)
                        // Add the ingredient object to the array
                        jsonArray.add(ingredientObject)
                    }
                }

                // Add the ingredients array to the main JSON object
                jsonObject.add("slot", jsonArray)
            }
        }

        Log.d("json object ", "******$jsonObject")

        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.recipeAddToPlanRequest({
                BaseApplication.dismissMe()
                handleApiAddToPlanResponse(it,dialogChooseMealDay)
            }, jsonObject)
        }
    }

    private fun addAnotherMealDialog() {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_add_another_meal)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val rlAddToPlan = dialogAddItem.findViewById<RelativeLayout>(R.id.rlAddToPlan)
        val tvChooseDessert = dialogAddItem.findViewById<TextView>(R.id.tvChooseDessert)
        val rlSelectDessert = dialogAddItem.findViewById<RelativeLayout>(R.id.rlSelectDessert)
        val relSelectedSnack = dialogAddItem.findViewById<RelativeLayout>(R.id.relSelectedSnack)
        val rlSelectBrunch = dialogAddItem.findViewById<RelativeLayout>(R.id.rlSelectBrunch)
        val rlSelectSnack = dialogAddItem.findViewById<RelativeLayout>(R.id.rlSelectSnack)
        dialogAddItem.show()
        dialogAddItem.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlAddToPlan.setOnClickListener {
            dialogAddItem.dismiss()
        }

        rlSelectDessert.setOnClickListener {
            if (status) {
                status = false
                val drawableEnd =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(
                    0,
                    0,
                    drawableEnd.intrinsicWidth,
                    drawableEnd.intrinsicHeight
                )
                tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
                relSelectedSnack.visibility = View.VISIBLE
            } else {
                status = true
                val drawableEnd =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(
                    0,
                    0,
                    drawableEnd.intrinsicWidth,
                    drawableEnd.intrinsicHeight
                )
                tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
                relSelectedSnack.visibility = View.GONE
            }
        }

        rlSelectSnack.setOnClickListener {
            tvChooseDessert.text = "Snack"
            val drawableEnd =
                ContextCompat.getDrawable(requireActivity(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
            relSelectedSnack.visibility = View.GONE
            status = true
        }

        rlSelectBrunch.setOnClickListener {
            tvChooseDessert.text = "Brunch"
            val drawableEnd =
                ContextCompat.getDrawable(requireActivity(), R.drawable.drop_down_icon)
            val drawableStart = ContextCompat.getDrawable(requireActivity(), R.drawable.gender_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            drawableStart!!.setBounds(
                0,
                0,
                drawableStart.intrinsicWidth,
                drawableStart.intrinsicHeight
            )
            tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
            relSelectedSnack.visibility = View.GONE
            status = true
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        when (status) {
            "1" -> {
                chooseDayDialog(position,type)
            }
            "2" -> {
                if (BaseApplication.isOnline(requireActivity())) {
                    toggleIsLike(type!!,position,"basket")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
            status -> {
                val bundle = Bundle()
                bundle.putString("uri", type)
                bundle.putString("mealType",status)
                findNavController().navigate(R.id.recipeDetailsFragment, bundle)
            }
            "4" -> {
                if (BaseApplication.isOnline(requireActivity())) {
                    toggleIsLike(type!!,position,"like")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
        }
    }

    private fun toggleIsLike(type: String, position: Int?, apiType: String) {
        // Map the type to the corresponding list and adapter
        val (mealList, adapter) = when (type) {
            "BreakFast" -> recipesModel?.Breakfast to breakfastAdapter
            "Lunch" -> recipesModel?.Lunch to lunchAdapter
            "Dinner" -> recipesModel?.Dinner to dinnerAdapter
            "Snacks" -> recipesModel?.Dinner to snackesAdapter
            "TeaTime" -> recipesModel?.Dinner to teaTimeAdapter
            else -> null to null
        }
        // Safely get the item and position
        val item = mealList?.get(position!!)
        if (item != null) {
            if (item.recipe?.uri!=null){
                if (apiType.equals("basket",true)){
                    addBasketData(item.recipe.uri)
                }else{
                    val newLikeStatus = if (item.is_like == 0) "1" else "0"
                    recipeLikeAndUnlikeData(item, adapter, type, mealList, position, newLikeStatus)
                }
            }
        }
    }



    private fun recipeLikeAndUnlikeData(
        item: BreakfastModel,
        adapter: AdapterPlanBreakFast?,
        type: String,
        mealList: MutableList<BreakfastModel>,
        position: Int?,
        likeType: String
    ) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.likeUnlikeRequest({
                BaseApplication.dismissMe()
                handleLikeAndUnlikeApiResponse(it,item,adapter,type,mealList,position)
            }, item.recipe?.uri!!,likeType,"")
        }
    }

    private fun addBasketData(uri: String) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.addBasketRequest({
                BaseApplication.dismissMe()
                handleBasketApiResponse(it)
            }, uri,"")
        }
    }

    private fun handleLikeAndUnlikeApiResponse(
        result: NetworkResult<String>,
        item: BreakfastModel,
        adapter: AdapterPlanBreakFast?,
        type: String,
        mealList: MutableList<BreakfastModel>,
        position: Int?
    ) {
        when (result) {
            is NetworkResult.Success -> handleLikeAndUnlikeSuccessResponse(result.data.toString(),item,adapter,type,mealList,position)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleBasketApiResponse(
        result: NetworkResult<String>
    ) {
        when (result) {
            is NetworkResult.Success -> handleBasketSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    override fun itemSelectPlayByDate(position: Int?, status: String?, type: String?) {
        dialogDailyInspiration()
        when (status) {
            "1" -> {
                dialogDailyInspiration()
            }
        }
    }

}
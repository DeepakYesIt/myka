package com.yesitlabs.mykaapp.fragment.mainfragment.plantab

import PlanApiResponse
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
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
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterPlanBreakFast
import com.yesitlabs.mykaapp.adapter.CalendarDayAdapter
import com.yesitlabs.mykaapp.adapter.ChooseDayAdapter
import com.yesitlabs.mykaapp.adapter.ImageViewPagerAdapter
import com.yesitlabs.mykaapp.apiInterface.BaseUrl
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.commonworkutils.WeekDaysCalculator
import com.yesitlabs.mykaapp.databinding.FragmentPlanBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.PlanViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.BreakfastModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.Data
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.RecipesModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.CalendarDataModel
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class PlanFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentPlanBinding? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private var calendarDayAdapter: CalendarDayAdapter? = null

    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var dataList2: MutableList<DataModel> = mutableListOf()
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var tvWeekRange: TextView? = null
    private var planBreakFastAdapter: AdapterPlanBreakFast? = null
    private var status: Boolean = true
    private var clickable: String? = ""
    private lateinit var viewModel: PlanViewModel
    private var recipesModel: RecipesModel? = null

    // Separate adapter instances for each RecyclerView
    var breakfastAdapter: AdapterPlanBreakFast? = null
    var lunchAdapter: AdapterPlanBreakFast? = null
    var dinnerAdapter: AdapterPlanBreakFast? = null
    var snackesAdapter: AdapterPlanBreakFast? = null
    var teaTimeAdapter: AdapterPlanBreakFast? = null
    private lateinit var sessionManagement: SessionManagement

    lateinit var adapter: ImageViewPagerAdapter
    val dataList = arrayListOf<DataModel>()
    private lateinit var layonboarding_indicator: LinearLayout

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

        updateWeek()





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
            viewModel.planRequest({
                BaseApplication.dismissMe()
                handleApiResponse(it)
            }, "q")
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
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
                breakfastAdapter = setupMealAdapter(data.recipes?.Breakfast, binding!!.rcyBreakFast, "BreakFast")
                binding!!.linearBreakfast.visibility = View.VISIBLE
            } else {
                binding!!.linearBreakfast.visibility = View.GONE
            }

            // Lunch
            if (recipesModel?.Lunch != null && recipesModel?.Lunch?.size!! > 0) {
                lunchAdapter = setupMealAdapter(data.recipes?.Lunch, binding!!.rcyLunch, "Lunch")
                binding!!.linearLunch.visibility = View.VISIBLE
            } else {
                binding!!.linearLunch.visibility = View.GONE
            }

            // Dinner
            if (recipesModel?.Dinner != null && recipesModel?.Dinner?.size!! > 0) {
                dinnerAdapter = setupMealAdapter(data.recipes?.Dinner, binding!!.rcyDinner, "Dinner")
                binding!!.linearDinner.visibility = View.VISIBLE
            } else {
                binding!!.linearDinner.visibility = View.GONE
            }


            // Snacks
            if (recipesModel?.Snack != null && recipesModel?.Snack?.size!! > 0) {
                snackesAdapter = setupMealAdapter(data.recipes?.Snack, binding!!.rcySnacks, "Snacks")
                binding!!.linearSnacks.visibility = View.VISIBLE
            } else {
                binding!!.linearSnacks.visibility = View.GONE
            }

            // TeaTime
            if (recipesModel?.Teatime != null && recipesModel?.Teatime?.size!! > 0) {
                teaTimeAdapter = setupMealAdapter(data.recipes?.Teatime, binding!!.rcyTeatime, "TeaTime")
                binding!!.linearTeatime.visibility = View.VISIBLE
            } else {
                binding!!.linearTeatime.visibility = View.GONE
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
            binding!!.llCalculateBmr.visibility = View.VISIBLE
            binding!!.relBreakFastsss.visibility = View.VISIBLE
            binding!!.relBreakFastsLunch.visibility = View.VISIBLE
            binding!!.rcyBreakFast.visibility = View.GONE
            binding!!.rcyLunch.visibility = View.GONE
        }



        binding!!.relMonthYear.setOnClickListener {
            openDialog()
        }



    }

    private fun openDialog(){

        val dialog = Dialog(requireActivity())

        // Set custom layout
        dialog.setContentView(R.layout.dialog_calendar)

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)

        calendarView.setOnDateChangeListener { view: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            val selectedDate = dayOfMonth.toString() + "-"+(month + 1)+"-" + year
            val list = WeekDaysCalculator.getWeekDays(selectedDate)
            val resultList = mutableListOf<Pair<String,String>>()
            list.forEach{
                Log.d("TESTING_LAWCO", it.toString())
                val arr = it.split("-")
                resultList.add(Pair(arr[0],arr[1]))
            }
            resultList.forEach {
                Log.d("TESTING_LAWCO", it.first+" "+it.second)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun dialogDailyInspiration() {
        val dialog = Dialog(requireContext(), R.style.BottomSheetDialog)
        dialog?.apply {
            setCancelable(false)
            setContentView(R.layout.alert_dialog_daily_inspiration)
            window?.attributes = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }


            layonboarding_indicator = findViewById<LinearLayout>(R.id.layonboarding_indicator)
//           var tabLayout = findViewById<TabLayout>(R.id.tabLayoutForIndicator)
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
                chooseDayDialog()
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

    private fun updateWeek() {
        val startOfWeek = calendar.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }.time

        val endOfWeek = calendar.apply {
            add(Calendar.DAY_OF_WEEK, 6)
        }.time

        binding!!.textWeekRange.text = "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"
        binding!!.recyclerViewWeekDays.adapter = calendarDayAdapter
        binding!!.recyclerViewWeekDays.adapter = CalendarDayAdapter(getDaysOfWeek()) {
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

    private fun chooseDayDialog() {
        val dialogChooseDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseDay.setContentView(R.layout.alert_dialog_choose_day)
        dialogChooseDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rcyChooseDaySch = dialogChooseDay.findViewById<RecyclerView>(R.id.rcyChooseDaySch)
        tvWeekRange = dialogChooseDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseDay.show()
        dialogChooseDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

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
        updateWeekRange()


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





    }

    private fun updateWeekRange() {
        val startOfWeek = calendar.apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }.time

        val endOfWeek = calendar.apply {
            add(Calendar.DAY_OF_WEEK, 6)
        }.time


        Log.d("start date ****$startOfWeek","End Date ******"+endOfWeek)

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
//        tvWeekRange = dialogChooseMealDay.findViewById(R.id.tvWeekRange)
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
//        updateWeekRange()
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
            clickable = "clicked"
            binding!!.rlAddDayToBasket.setBackgroundResource(R.drawable.green_btn_background)
            binding!!.rlAddDayToBasket.isClickable = true
            dialogChooseMealDay.dismiss()
        }

        /*btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }*/

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
                chooseDayDialog()
            }
            "2" -> {
                if (BaseApplication.isOnline(requireActivity())) {
                    toggleIsLike(type!!,position,"basket")
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
            "3" -> {
                val bundle = Bundle()
                bundle.putString("uri", type)
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
            }, item.recipe?.uri!!,likeType)
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

}
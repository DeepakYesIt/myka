package com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterFoodListItem
import com.mykameal.planner.adapter.CalendarDayDateAdapter
import com.mykameal.planner.adapter.CalendarDayIndicatorAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentCookedBinding
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModel
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModelData
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.viewmodel.CookedTabViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import com.mykameal.planner.model.CalendarDataModel
import com.mykameal.planner.model.DateModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CookedFragment : Fragment(), OnItemClickListener {
    private var binding: FragmentCookedBinding? = null
    private var cookedTabViewModel: CookedTabViewModel? = null

    private var foodListBreakFastAdapter: AdapterFoodListItem? = null
    private var foodListLunchAdapter: AdapterFoodListItem? = null
    private var foodListDinnerAdapter: AdapterFoodListItem? = null
    private var foodListSnacksAdapter: AdapterFoodListItem? = null
    private var foodListTeaTimeAdapter: AdapterFoodListItem? = null
    private var calendarDayAdapter: CalendarDayIndicatorAdapter? = null
    private var planType: String = "1"
    private val calendar = Calendar.getInstance()
    private var isOpened: Boolean? = null
    private var currentDate = Date() // Current date
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    // Define global variables
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private var currentDateSelected: String = ""
    private var calendarAdapter: CalendarDayDateAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCookedBinding.inflate(inflater, container, false)
        (activity as MainActivity?)?.changeBottom("cooked")

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE

        currentDateSelected = BaseApplication.currentDateFormat().toString()
        cookedTabViewModel = ViewModelProvider(this)[CookedTabViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        toggleFridgeToFreezer()

        updateWeek()

        binding!!.imageBreakFastCreate.setOnClickListener {
            findNavController().navigate(R.id.addMealCookedFragment)
        }

        binding!!.imageLunchCreate.setOnClickListener {
            findNavController().navigate(R.id.addMealCookedFragment)
        }


        binding!!.imageDinnerCreate.setOnClickListener {
            findNavController().navigate(R.id.addMealCookedFragment)
        }

        binding!!.imagePrevious.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, -1) // Move to next week
            currentDate = calendar.time

            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }

        binding!!.imageNext.setOnClickListener {
            // Simulate clicking the "Next" button to move to the next week
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week
            currentDate = calendar.time

            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }

        binding!!.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.relCalendarYear.setOnClickListener {
            if (isOpened == true) {
                binding!!.llCalendarViewEvents.visibility = View.GONE
                isOpened = false
            } else {
                binding!!.llCalendarViewEvents.visibility = View.VISIBLE
                isOpened = true
            }
        }

        binding!!.textAddMeals.setOnClickListener {
            findNavController().navigate(R.id.addMealCookedFragment)
        }

        // Display current week dates
        showWeekDates()

        if (BaseApplication.isOnline(requireActivity())) {
            cookedTabApi(currentDateSelected)
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showWeekDates() {
        val (startDate, endDate) = getWeekDates(currentDate)
        this.startDate = startDate
        this.endDate = endDate

        // Get all dates between startDate and endDate
        val daysBetween = getDaysBetween(startDate, endDate)

        daysBetween.forEach { println(it) }
        binding!!.textMonthAndYear.text = BaseApplication.formatonlyMonthYear(startDate)
        binding!!.textWeekRange.text = "" + formatDate(startDate) + "-" + formatDate(endDate)
        // Update the RecyclerView
        calendarAdapter = CalendarDayDateAdapter(getDaysBetween(startDate, endDate)) {
            // Handle item click if needed
            val dateList = getDaysBetween(startDate, endDate)
            // Update the status of the item at the target position
            dateList.forEachIndexed { index, dateModel ->
                if (index == it) {
                    dateModel.status = true
                } else {
                    dateModel.status = false
                }
            }
            currentDateSelected = dateList[it].date
            Log.d("Date ", "*****$dateList")
            // Notify the adapter to refresh the changed position
            calendarAdapter!!.updateList(dateList)
            if (BaseApplication.isOnline(requireActivity())) {
                cookedTabApi(currentDateSelected)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
        // Update the RecyclerView
        binding!!.recyclerViewWeekDays.adapter = calendarAdapter

        /*       binding!!.recyclerViewWeekDays.adapter = CalendarDayDateAdapter(getDaysBetween(startDate, endDate)) {
                   val dateList = getDaysBetween(startDate, endDate)
                   // Update the status of the item at the target position
                   dateList.forEachIndexed { index, dateModel ->
                       if (index==it){
                           dateModel.status=true
                       }else{
                           dateModel.status=false
                       }
                   }
                   // Handle item click if needed
                   currentDateSelected= dateList[it].date
                   calendarAdapter!!.updateList(dateList)
               }*/
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

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getDaysBetween(startDate: Date, endDate: Date): MutableList<DateModel> {
        val dateList = mutableListOf<DateModel>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format for the date
        val dayFormat =
            SimpleDateFormat("EEEE", Locale.getDefault()) // Format for the day name (e.g., Monday)

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            val date = dateFormat.format(calendar.time)  // Get the formatted date (yyyy-MM-dd)
            val dayName =
                dayFormat.format(calendar.time)  // Get the day name (Monday, Tuesday, etc.)

            val localDate = DateModel()
            localDate.day = dayName
            localDate.date = date
            // Combine both the day name and the date
//            dateList.add("$dayName, $date")
            dateList.add(localDate)


            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dateList
    }

    private fun cookedTabApi(date: String) {
        BaseApplication.showMe(requireActivity())
        lifecycleScope.launch {
            cookedTabViewModel!!.cookedDateRequest({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val cookedModel = gson.fromJson(it.data, CookedTabModel::class.java)
                        if (cookedModel.code == 200 && cookedModel.success) {
                            showDataInUi(cookedModel.data)
                        } else {
                            if (cookedModel.code == ErrorMessage.code) {
                                showAlertFunction(cookedModel.message, true)
                            } else {
                                showAlertFunction(cookedModel.message, false)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }, date, planType)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDataInUi(cookedTabModelData: CookedTabModelData) {
        try {
            if (cookedTabModelData != null) {

                if (cookedTabModelData.fridge != null && cookedTabModelData.freezer != null) {
                    if (cookedTabModelData.fridge == 0 && cookedTabModelData.freezer == 0) {
                        binding!!.llEmptyFridge.visibility = View.VISIBLE
                        binding!!.llFilledFridge.visibility = View.GONE
                    } else {
                        binding!!.llEmptyFridge.visibility = View.GONE
                        binding!!.llFilledFridge.visibility = View.VISIBLE

                    }
                    binding!!.textFreezer.text =
                        "Freezer (" + cookedTabModelData.freezer.toString() + ")"
                    binding!!.textFridge.text = "Fridge (" + cookedTabModelData.fridge.toString() + ")"
                }

                if (cookedTabModelData.Breakfast != null && cookedTabModelData.Breakfast.size > 0) {
                    binding!!.rlBreakfast.visibility = View.VISIBLE
                    foodListBreakFastAdapter = AdapterFoodListItem(cookedTabModelData.Breakfast, "Breakfast", requireActivity(), this)
                    binding!!.rcvBreakfast.adapter = foodListBreakFastAdapter
                } else {
                    binding!!.rlBreakfast.visibility = View.GONE
                }

                if (cookedTabModelData.Lunch != null && cookedTabModelData.Lunch.size > 0) {
                    binding!!.rlLunch.visibility = View.VISIBLE
                    foodListLunchAdapter =
                        AdapterFoodListItem(cookedTabModelData.Lunch, "Lunch", requireActivity(), this)
                    binding!!.rcvLunch.adapter = foodListLunchAdapter
                } else {
                    binding!!.rlLunch.visibility = View.GONE
                }

                if (cookedTabModelData.Dinner != null && cookedTabModelData.Dinner.size > 0) {
                    binding!!.relDinner.visibility = View.VISIBLE
                    foodListDinnerAdapter = AdapterFoodListItem(
                        cookedTabModelData.Dinner,
                        "Dinner",
                        requireActivity(),
                        this
                    )
                    binding!!.rcvDinner.adapter = foodListDinnerAdapter
                } else {
                    binding!!.relDinner.visibility = View.GONE
                }


                if (cookedTabModelData.Snacks != null) {
                    if (cookedTabModelData.Snacks!!.size != null && cookedTabModelData.Snacks.size > 0) {
                        binding!!.relSnacks.visibility = View.VISIBLE
                        foodListSnacksAdapter = AdapterFoodListItem(
                            cookedTabModelData.Snacks,
                            "Snacks",
                            requireActivity(),
                            this
                        )
                        binding!!.rcvSnacks.adapter = foodListSnacksAdapter
                    } else {
                        binding!!.relSnacks.visibility = View.GONE
                    }
                }

                if (cookedTabModelData.Teatime != null) {
                    if (cookedTabModelData.Teatime!!.size != null && cookedTabModelData.Teatime.size > 0) {
                        binding!!.relTeaTime.visibility = View.VISIBLE
                        foodListTeaTimeAdapter = AdapterFoodListItem(
                            cookedTabModelData.Teatime,
                            "Teatime",
                            requireActivity(),
                            this
                        )
                        binding!!.rcvTeaTime.adapter = foodListTeaTimeAdapter
                    } else {
                        binding!!.relTeaTime.visibility = View.GONE
                    }
                }
            }
        }catch (e:Exception){
            Log.d("CookedScreen","message:--"+e.message)
        }
    }


    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    private fun updateWeek() {
        val startOfWeek = calendar.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }.time

        val endOfWeek = calendar.apply {
            add(Calendar.DAY_OF_WEEK, 6)
        }.time

        binding!!.textWeekRange.text =
            "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"
        binding!!.recyclerViewWeekDays.adapter = calendarDayAdapter
        binding!!.recyclerViewWeekDays.adapter = CalendarDayIndicatorAdapter(getDaysOfWeek()) {
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


    private fun toggleFridgeToFreezer() {

        binding!!.textFridge.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFridge.setTextColor(Color.WHITE)
            binding!!.textFreezer.setTextColor(Color.BLACK)
            binding!!.llFilledFridge.visibility = View.VISIBLE
            binding!!.llEmptyFridge.visibility = View.GONE
            planType = "1"

            if (BaseApplication.isOnline(requireActivity())) {
                cookedTabApi(currentDateSelected)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        binding!!.textFreezer.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFridge.setTextColor(Color.BLACK)
            binding!!.textFreezer.setTextColor(Color.WHITE)
            binding!!.llFilledFridge.visibility = View.GONE
            binding!!.llEmptyFridge.visibility = View.GONE
            planType = "2"


            if (BaseApplication.isOnline(requireActivity())) {
                cookedTabApi(currentDateSelected)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    override fun itemClick(position: Int?, status: String?, cookedId: String?) {
        Toast.makeText(requireActivity(), "Typessss:---  " + status, Toast.LENGTH_LONG).show()
        removeMealDialog(cookedId, position, status)

    }

    private fun removeMealDialog(cookedId: String?, position: Int?, status: String?) {
        val dialogRemoveDay: Dialog = context?.let { Dialog(it) }!!
        dialogRemoveDay.setContentView(R.layout.alert_dialog_remove_cooked_meals)
        dialogRemoveDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogRemoveDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogNoBtn = dialogRemoveDay.findViewById<TextView>(R.id.tvDialogNoBtn)
        val tvDialogYesBtn = dialogRemoveDay.findViewById<TextView>(R.id.tvDialogYesBtn)
        dialogRemoveDay.show()
        dialogRemoveDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogNoBtn.setOnClickListener {
            dialogRemoveDay.dismiss()
        }

        tvDialogYesBtn.setOnClickListener {

            if (BaseApplication.isOnline(requireActivity())) {
                removeCookBookApi(cookedId.toString(), dialogRemoveDay, position, status)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun removeCookBookApi(
        cookedId: String,
        dialogRemoveDay: Dialog,
        position: Int?,
        status: String?
    ) {
        BaseApplication.showMe(requireActivity())
        lifecycleScope.launch {
            cookedTabViewModel!!.removeMealApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val cookedModel = gson.fromJson(it.data, CookedTabModel::class.java)
                        if (cookedModel.code == 200 && cookedModel.success) {
                            when (status) {
                                "Breakfast" -> foodListBreakFastAdapter?.removeItem(position!!)
                                "Lunch" -> foodListLunchAdapter?.removeItem(position!!)
                                "Dinner" -> foodListDinnerAdapter?.removeItem(position!!)
                                "Snacks" -> foodListSnacksAdapter?.removeItem(position!!)
                                "Teatime" -> foodListTeaTimeAdapter?.removeItem(position!!)
                            }
                            dialogRemoveDay.dismiss()
                        } else {
                            if (cookedModel.code == ErrorMessage.code) {
                                showAlertFunction(cookedModel.message, true)
                            } else {
                                showAlertFunction(cookedModel.message, false)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }

                    else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }, cookedId)
        }
    }

}
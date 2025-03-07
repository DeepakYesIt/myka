package com.mykaimeal.planner.fragment.mainfragment.hometab.fullcookedScheduleFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mykaimeal.planner.OnItemLongClickListener
import com.mykaimeal.planner.OnItemSelectUnSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.CalendarDayAdapter
import com.mykaimeal.planner.adapter.CalendarDayDateAdapter
import com.mykaimeal.planner.adapter.IngredientsBreakFastAdapter
import com.mykaimeal.planner.adapter.IngredientsDinnerAdapter
import com.mykaimeal.planner.adapter.IngredientsLunchAdapter
import com.mykaimeal.planner.adapter.IngredientsSnacksAdapter
import com.mykaimeal.planner.adapter.IngredientsTeaTimeAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentFullCookedScheduleBinding
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.CreateRecipeSuccessModel
import com.mykaimeal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.Breakfast
import com.mykaimeal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModel
import com.mykaimeal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModelData
import com.mykaimeal.planner.fragment.mainfragment.hometab.fullcookedScheduleFragment.viewmodel.FullCookingScheduleViewModel
import com.mykaimeal.planner.fragment.mainfragment.plantab.ImagesDeserializer
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.CookBookListResponse
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.ImagesModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.CalendarDataModel
import com.mykaimeal.planner.model.DataModel
import com.mykaimeal.planner.model.DateModel
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

@AndroidEntryPoint
class FullCookedScheduleFragment : Fragment(), OnItemSelectUnSelectListener,
    OnItemLongClickListener {

    private var binding: FragmentFullCookedScheduleBinding? = null
    private var ingredientBreakFastAdapter: IngredientsBreakFastAdapter? = null
    private var ingredientLunchAdapter: IngredientsLunchAdapter? = null
    private var ingredientDinnerAdapter: IngredientsDinnerAdapter? = null
    private var ingredientSnacksAdapter: IngredientsSnacksAdapter? = null
    private var ingredientTeaTimeAdapter: IngredientsTeaTimeAdapter? = null
    private var tvWeekRange: TextView? = null
    private var calendarDayAdapter: CalendarDayAdapter? = null
    private var calendarAdapter: CalendarDayDateAdapter? = null

    private var recipesDateModel: CookedTabModelData? = null
    private lateinit var fUllCookingScheduleViewModel: FullCookingScheduleViewModel
    private val calendar = Calendar.getInstance()
    private val dateFormat1 = SimpleDateFormat("dd MMM", Locale.getDefault())
    private lateinit var sessionManagement: SessionManagement
    private lateinit var commonWorkUtils: CommonWorkUtils


    // Define global variables
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private var dropDate: String? = ""
    private var dropDay: String? = null
    private var currentDate = Date() // Current date
    private var currentDateSelected: String = ""
    private lateinit var spinnerActivityLevel: PowerSpinnerView
    private var cookbookList: MutableList<com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data> =
        mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFullCookedScheduleBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE

        sessionManagement = SessionManagement(requireContext())
        commonWorkUtils = CommonWorkUtils(requireContext())
        currentDateSelected = BaseApplication.currentDateFormat().toString()
        fUllCookingScheduleViewModel = ViewModelProvider(requireActivity())[FullCookingScheduleViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        if (cookbookList!=null){
            cookbookList.clear()
        }

        val data = com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data(
                "", "", 0, "", "Favourites", 0, "", 0)
        cookbookList.add(0, data)

        if (sessionManagement.getUserName() != null) {
            binding?.tvName?.text = sessionManagement.getUserName()+"'s week"
        }

        if (BaseApplication.isOnline(requireActivity())) {
            dataFetchByDate(currentDateSelected)
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        onClickFalseEnabled()
   /*     fullCookSchBreakFastModel()
        fullCookSchLunchModel()
        fullCookSchDinnerModel()*/
        initialize()

        // Display current week dates
        showWeekDates()

        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    fun showWeekDates() {
        Log.d("currentDate :- ", "******$currentDate")

        // Define the date format (update to match your `date` string format)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedCurrentDate =
            dateFormat.format(currentDate) // Format currentDate to match the string format

        // Get the start and end dates of the week
        val (startDate, endDate) = getWeekDates(currentDate)
        this.startDate = startDate
        this.endDate = endDate

        // Get all dates between startDate and endDate
        val daysBetween = getDaysBetween(startDate, endDate)

        // Mark the current date as selected in the list
        val updatedDaysBetween = daysBetween.map { dateModel ->
            dateModel.apply {
                status = (date == formattedCurrentDate) // Compare formatted strings
            }
        }

        // Print the dates for debugging
        println("Days between $startDate and $endDate:")
        updatedDaysBetween.forEach { println(it) }

        // Update UI with formatted date ranges
//        binding?.tvCustomDates?.text = BaseApplication.formatonlyMonthYear(startDate)
        binding?.tvCustomDates?.text ="${formatDate(startDate)} - ${formatDate(endDate)}"
        binding?.textWeekRange?.text = "${formatDate(startDate)} - ${formatDate(endDate)}"
        tvWeekRange?.text = "${formatDate(startDate)} - ${formatDate(endDate)}"

        // Initialize the adapter with the updated date list
        calendarAdapter = CalendarDayDateAdapter(updatedDaysBetween.toMutableList()) { selectedPosition ->
            // Update the list to reflect the selected date
            updatedDaysBetween.forEachIndexed { index, dateModel ->
                dateModel.status = (index == selectedPosition)
            }
            Log.d("Date ", "*****$updatedDaysBetween")

            // Notify the adapter to refresh the data
            calendarAdapter?.updateList(updatedDaysBetween.toMutableList())

            // Update the current date selection
            currentDateSelected = updatedDaysBetween[selectedPosition].date

            // Fetch data for the selected date if online
            if (BaseApplication.isOnline(requireActivity())) {
                dataFetchByDate(currentDateSelected)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

        // Update the RecyclerView with the adapter
        binding?.recyclerViewWeekDays?.adapter = calendarAdapter

        binding!!.recyclerViewWeekDays.setOnDragListener { view, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Accept the drag only if the MIME type matches

                    dragEvent.clipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Optional: Highlight the RecyclerView background
//                    view.setBackgroundColor(Color.LTGRAY)
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    val recyclerView = view as RecyclerView // Cast the view to RecyclerView
                    val childView = recyclerView.findChildViewUnder(
                        dragEvent.x,
                        dragEvent.y
                    ) // Call findChildViewUnder on RecyclerView
                    val targetPosition = if (childView != null) {
                        recyclerView.getChildAdapterPosition(childView) // Get the position of the child
                    } else {
                        RecyclerView.NO_POSITION // Return NO_POSITION if no child is found
                    }

                    if (targetPosition != RecyclerView.NO_POSITION) {

                        // Retrieve the list of dates
                        val dateList = getDaysBetween(startDate, endDate)

                        // Update the status of the item at the target position
                        dateList.forEachIndexed { index, dateModel ->
                            if (index == targetPosition) {
                                dateModel.status = true
                            } else {
                                dateModel.status = false
                            }

                        }

                        Log.d("Date ", "*****$dateList")

                        // Notify the adapter to refresh the changed position
                        calendarAdapter!!.updateList(dateList)
                        calendarAdapter!!.notifyItemChanged(targetPosition)

                        /*calendarAdapter!!.updateList(dateList)*/

                    } else {
                        Log.d("date position ", "No valid position under drag location")
                    }
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Optional: Reset background or remove highlight
//                    view.setBackgroundColor(Color.WHITE)
                    true
                }

                DragEvent.ACTION_DROP -> {
                    // Retrieve the dragged data
                    val clipData = dragEvent.clipData
                    val draggedItem =
                        clipData.getItemAt(0).text.toString() // Assuming text represents the dragged item

                    // Find the target position in RecyclerView
                    val recyclerView = view as RecyclerView // Cast the view to RecyclerView
                    val dropX = dragEvent.x // X-coordinate of the drop
                    val dropY = dragEvent.y // Y-coordinate of the drop
                    val childView = recyclerView.findChildViewUnder(
                        dropX,
                        dropY
                    ) // Find the child view under the drop position

                    if (childView != null) {
                        // Get the adapter position of the target item
                        val dropPosition = recyclerView.getChildAdapterPosition(childView)

                        if (dropPosition != RecyclerView.NO_POSITION) {
                            Log.d("ACTION_DROP", "Item dropped at position: $dropPosition")

                            dropDate = getDaysBetween(startDate, endDate)[dropPosition].date
                            /*dropMealType*/
                            dropDay = getDaysBetween(startDate, endDate)[dropPosition].day

                            Log.d("ACTION_DROP", "*******$dropPosition")
                            Log.d("date position ", "******" + getDaysBetween(startDate, endDate)[dropPosition].date)

                            Log.d("drop date and days", "******" + getDaysBetween(startDate, endDate)[dropPosition].date +
                                    "-" + getDaysBetween(startDate, endDate)[dropPosition].day)

                            Toast.makeText(requireContext(), "date " + getDaysBetween(startDate, endDate
                                )[dropPosition].date + "-" + getDaysBetween(startDate, endDate)[dropPosition].day, Toast.LENGTH_LONG).show()

                            Log.d("ACTION_DROP", "Target position: $dropPosition")

                            // Retrieve the list of dates
                            val dateList = getDaysBetween(startDate, endDate)

                            // Update the status of the item at the target position
                            dateList.forEachIndexed { index, dateModel ->
                                if (index == dropPosition) {
                                    dateModel.status = true
                                } else {
                                    dateModel.status = false
                                }
                            }

                            Log.d("Date ", "*****$dateList")
                            // Notify the adapter to refresh the changed position
                            calendarAdapter!!.updateList(dateList)
                            // Optional: Notify the source RecyclerView to remove the dragged item
                            // notifyItemRemovedInSource(draggedItem)
                        } else {
                            Log.d("ACTION_DROP", "No valid drop position found")
                        }
                    } else {
                        Log.d("ACTION_DROP", "Dropped outside valid child views")
                    }
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Reset background or clean up
//                    view.setBackgroundColor(Color.WHITE)
                    true
                }

                else -> false
            }
        }
    }

    private fun  dataFetchByDate(date: String) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            fUllCookingScheduleViewModel.fullCookingSchedule({
                BaseApplication.dismissMe()
                handleApiPlanDateResponse(it)
            }, date, "0")
        }
    }

    private fun handleApiPlanDateResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessPlanDateResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessPlanDateResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, CookedTabModel::class.java)
            Log.d("@@@ FullSchedule List ", "message :- $data")
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

    private fun showDataAccordingDate(data: CookedTabModelData) {
        try {
            recipesDateModel = null
            recipesDateModel = data

            if (recipesDateModel != null) {
                // Breakfast
                if (recipesDateModel?.Breakfast != null && recipesDateModel?.Breakfast?.size!! > 0) {
                    setupDragScrollForRecyclerView(binding!!.rcySearchBreakFast)
                    binding!!.llBreakFast.visibility = View.VISIBLE
                    ingredientBreakFastAdapter = IngredientsBreakFastAdapter(
                        recipesDateModel?.Breakfast, requireActivity(), this, this, "BreakFast"
                    )
                    binding!!.rcySearchBreakFast.adapter = ingredientBreakFastAdapter
                } else {
                    binding!!.llBreakFast.visibility = View.GONE
                }

                ///Lunch
                if (recipesDateModel?.Lunch != null && recipesDateModel?.Lunch?.size!! > 0) {
                    setupDragScrollForRecyclerView(binding!!.rcySearchLunch)
                    binding!!.llLunch.visibility = View.VISIBLE
                    ingredientLunchAdapter = IngredientsLunchAdapter(
                        recipesDateModel?.Lunch,
                        requireActivity(),
                        this,
                        this,
                        "Lunch"
                    )
                    binding!!.rcySearchLunch.adapter = ingredientLunchAdapter
                } else {
                    binding!!.llLunch.visibility = View.GONE
                }

                // Dinner
                if (recipesDateModel?.Dinner != null && recipesDateModel?.Dinner?.size!! > 0) {
                    setupDragScrollForRecyclerView(binding!!.rcySearchDinner)
                    binding!!.llDinner.visibility = View.VISIBLE
                    ingredientDinnerAdapter = IngredientsDinnerAdapter(
                        recipesDateModel?.Dinner,
                        requireActivity(),
                        this,
                        this,
                        "Dinner"
                    )
                    binding!!.rcySearchDinner.adapter = ingredientDinnerAdapter

                } else {
                    binding!!.llDinner.visibility = View.GONE
                }

                // Snacks
                if (recipesDateModel?.Snacks != null && recipesDateModel?.Snacks?.size!! > 0) {
                    setupDragScrollForRecyclerView(binding!!.rcySearchSnacks)
                    binding!!.llSnacks.visibility = View.VISIBLE
                    ingredientSnacksAdapter = IngredientsSnacksAdapter(
                        recipesDateModel?.Snacks, requireActivity(), this,
                        this, "Snacks"
                    )
                    binding!!.rcySearchSnacks.adapter = ingredientSnacksAdapter
                } else {
                    binding!!.llSnacks.visibility = View.GONE
                }

                // TeaTime
                if (recipesDateModel?.Teatime != null && recipesDateModel?.Teatime?.size!! > 0) {
                    setupDragScrollForRecyclerView(binding!!.rcySearchTeaTime)
                    binding!!.llTeaTime.visibility = View.VISIBLE
                    ingredientTeaTimeAdapter = IngredientsTeaTimeAdapter(
                        recipesDateModel?.Teatime, requireActivity(), this,
                        this, "TeaTime"
                    )
                    binding!!.rcySearchTeaTime.adapter = ingredientTeaTimeAdapter
                } else {
                    binding!!.llTeaTime.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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

    private fun initialize() {

        binding!!.relCalendarView.setOnClickListener {
            openDialog()
        }

        // Initialize RecyclerView and Adapter
        setupRecyclerView()

        ///relCookChangeSchedule
        binding!!.rlChangeCookSchedule.setOnClickListener {
            if (validate()){
                if (BaseApplication.isOnline(requireActivity())) {
                    updateMealApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

//            chooseDayMealTypeDialog()
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

        binding!!.rlMainFullCooked.setOnLongClickListener {
            onClickEnabled()
            true
        }

        binding!!.rlMainFullCooked.setOnClickListener {
            onClickFalseEnabled()
        }

        binding!!.imgBackCookingSchedule.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /// add validation based on valid email or phone
    private fun validate(): Boolean {
        // Check if email/phone is empty
        if (dropDate=="") {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.changeScheduleDate, false)
            return false
        }

        return true
    }

    private fun updateMealApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "")
        jsonObject.addProperty("id", "")
        jsonObject.addProperty("date",dropDate)
        jsonObject.addProperty("day", dropDay)
        Log.d("json object ", "******$jsonObject")

        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            fUllCookingScheduleViewModel.updateMealUrl({
                BaseApplication.dismissMe()
                handleApiUpdateScheduleResponse(it)
            }, jsonObject)
        }
    }

    private fun handleApiUpdateScheduleResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleUpdateScheduleResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleUpdateScheduleResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, CreateRecipeSuccessModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                dataFetchByDate(currentDateSelected)
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

    private fun setupRecyclerView() {
        calendarDayAdapter = CalendarDayAdapter(emptyList()) { day ->
            // Handle item clicks if needed
            Toast.makeText(context, "Clicked on ${day.dayName}, ${day.date}", Toast.LENGTH_SHORT).show()
        }
        binding!!.recyclerViewWeekDays.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding!!.recyclerViewWeekDays.adapter = calendarDayAdapter

        binding!!.recyclerViewWeekDays.setOnDragListener { view, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Accept the drag only if the MIME type matches
                    dragEvent.clipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Optional: Highlight the RecyclerView background
//                    view.setBackgroundColor(Color.LTGRAY)
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    val recyclerView = view as RecyclerView // Cast the view to RecyclerView
                    val childView = recyclerView.findChildViewUnder(
                        dragEvent.x,
                        dragEvent.y
                    ) // Call findChildViewUnder on RecyclerView
                    val targetPosition = if (childView != null) {
                        recyclerView.getChildAdapterPosition(childView) // Get the position of the child
                    } else {
                        RecyclerView.NO_POSITION // Return NO_POSITION if no child is found
                    }

                    if (targetPosition != RecyclerView.NO_POSITION) {
//                        highlightDropPosition(targetPosition)
                        Log.d("ACTION_DRAG_LOCATION", "Target position: $targetPosition")
                    } else {
                        Log.d("ACTION_DRAG_LOCATION", "No valid position under drag location")
                    }
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    // Optional: Reset background or remove highlight
//                    view.setBackgroundColor(Color.WHITE)
                    true
                }
//                DragEvent.ACTION_DROP -> {
//                    // Retrieve the dragged data
//                    val clipData = dragEvent.clipData
//                    val draggedItem = clipData.getItemAt(0).text.toString() // Assuming title represents the dragged item
//
//                    /*// Update data in target RecyclerView
//                    (view.adapter as TargetAdapter).addItem(draggedItem)
//
//                    // Notify the source RecyclerView to remove the dragged item
//                    notifyItemRemovedInSource(draggedItem)*/
//                    Log.d("ACTION_DROP","*******")
//                    true
//                }
                DragEvent.ACTION_DROP -> {
                    // Retrieve the dragged data
                    val clipData = dragEvent.clipData
                    val draggedItem = clipData.getItemAt(0).text.toString() // Assuming text represents the dragged item

                    // Find the target position in RecyclerView
                    val recyclerView = view as RecyclerView // Cast the view to RecyclerView
                    val dropX = dragEvent.x // X-coordinate of the drop
                    val dropY = dragEvent.y // Y-coordinate of the drop
                    val childView = recyclerView.findChildViewUnder(
                        dropX,
                        dropY
                    ) // Find the child view under the drop position

                    if (childView != null) {
                        // Get the adapter position of the target item
                        val dropPosition = recyclerView.getChildAdapterPosition(childView)

                        if (dropPosition != RecyclerView.NO_POSITION) {
                            Log.d("ACTION_DROP", "Item dropped at position: $dropPosition")
                            Log.d("ACTION_DROP", "*******$dropPosition")
                            // Optional: Notify the source RecyclerView to remove the dragged item
                            // notifyItemRemovedInSource(draggedItem)
                        } else {
                            Log.d("ACTION_DROP", "No valid drop position found")
                        }
                    } else {
                        Log.d("ACTION_DROP", "Dropped outside valid child views")
                    }
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Reset background or clean up
//                    view.setBackgroundColor(Color.WHITE)
                    true
                }

                else -> false
            }
        }
    }

    private fun openDialog() {
        val dialog = Dialog(requireActivity())

        // Set custom layout
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Convert selected date into a Calendar instance
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)

            // Calculate start and end of the week
            val startOfWeek = calendar.clone() as Calendar
            startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            // Calculate the end of the week (Sunday)
            val endOfWeek = calendar.clone() as Calendar
            endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            endOfWeek.add(Calendar.DAY_OF_WEEK, 6) // Add 6 days to Monday to reach Sunday

            // Format the dates for display
            val startDate = dateFormat1.format(startOfWeek.time)
            val endDate = dateFormat1.format(endOfWeek.time)

//            // Update the Week Range Text
            binding!!.tvCustomDates.text = "$startDate - $endDate"
            // Calculate the days for the adapter and update it
            val daysOfWeek = getDaysOfWeekForSelectedDate(selectedCalendar)
            calendarDayAdapter!!.updateData(daysOfWeek)
            // Dismiss the dialog after selection
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getDaysOfWeekForSelectedDate(selectedCalendar: Calendar): List<CalendarDataModel.Day> {
        val days = mutableListOf<CalendarDataModel.Day>()

        // Set the calendar to the start of the week (Monday)
        val weekCalendar = selectedCalendar.clone() as Calendar
        weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Loop through the week (Monday to Sunday)
        for (i in 0..6) {
            days.add(
                CalendarDataModel.Day(
                    dayName = SimpleDateFormat(
                        "E",
                        Locale.getDefault()
                    ).format(weekCalendar.time), date = weekCalendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        return days
    }


    private fun setupDragScrollForRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    binding!!.scroll.fullScroll(0)
                    true
                }

                DragEvent.ACTION_DROP -> {
                    // Handle the drop action here if needed
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Cleanup if needed
                    true
                }

                else -> false
            }
        }
    }


    override fun itemSelectUnSelect(id: Int?, status: String?, type: String?, position: Int?) {
        if (status == "heart") {
            if (BaseApplication.isOnline(requireActivity())) {
                toggleIsLike(type ?: "", position)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        } else if (status == "minus") {
            /*if (status == "1") {*/
            removeDayDialog(id, position, type)
            /*}*/
        } else if (status == "missingIng") {
            val (mealList) = when (type) {
                "BreakFast" -> recipesDateModel!!.Breakfast to ingredientBreakFastAdapter
                "Lunch" -> recipesDateModel!!.Lunch to ingredientLunchAdapter
                "Dinner" -> recipesDateModel?.Dinner to ingredientDinnerAdapter
                "Snacks" -> recipesDateModel!!.Snacks to ingredientSnacksAdapter
                "TeaTime" -> recipesDateModel!!.Teatime to ingredientTeaTimeAdapter
                else -> null to null
            }

            // Safely get the item and position
            val item = mealList?.get(position!!)
            if (item != null) {
                if (item.recipe?.uri != null) {
                    val bundle = Bundle().apply {
                        putString("uri",item.recipe.uri)
                        putString("schId",item.id.toString())
                    }
                    findNavController().navigate(R.id.missingIngredientsFragment,bundle)
                }
            }
        } else {
            val (mealList) = when (type) {
                "BreakFast" -> recipesDateModel!!.Breakfast to ingredientBreakFastAdapter
                "Lunch" -> recipesDateModel!!.Lunch to ingredientLunchAdapter
                "Dinner" -> recipesDateModel?.Dinner to ingredientDinnerAdapter
                "Snacks" -> recipesDateModel!!.Snacks to ingredientSnacksAdapter
                "TeaTime" -> recipesDateModel!!.Teatime to ingredientTeaTimeAdapter
                else -> null to null
            }

            // Safely get the item and position
            val item = mealList?.get(position!!)
            if (item != null) {
                if (item.recipe?.uri != null) {
                    val bundle = Bundle().apply {
                        putString("uri",item.recipe.uri)
                        putString("mealType",item.recipe.mealType.toString())
                    }
                    findNavController().navigate(R.id.recipeDetailsFragment, bundle)
                }
            }

            /*findNavController().navigate(R.id.recipeDetailsFragment)*/
        }
    }

    private fun toggleIsLike(type: String, position: Int?) {
        // Map the type to the corresponding list and adapter
        val (mealList) = when (type) {
            "BreakFast" -> recipesDateModel!!.Breakfast to ingredientBreakFastAdapter
            "Lunch" -> recipesDateModel!!.Lunch to ingredientLunchAdapter
            "Dinner" -> recipesDateModel?.Dinner to ingredientDinnerAdapter
            "Snacks" -> recipesDateModel!!.Snacks to ingredientSnacksAdapter
            "TeaTime" -> recipesDateModel!!.Teatime to ingredientTeaTimeAdapter
            else -> null to null
        }

        // Safely get the item and position
        val item = mealList?.get(position!!)
        if (item != null) {
            if (item.recipe?.uri != null) {
                val newLikeStatus = if (item.is_like == 0) "1" else "0"
                if (newLikeStatus.equals("0", true)) {
                    recipeLikeAndUnlikeData(item, type, mealList, position, newLikeStatus, "", null)
                } else {
                    addFavTypeDialog(item, type, mealList, position, newLikeStatus)
                }
            }
        }
    }

    private fun recipeLikeAndUnlikeData(
        item: Breakfast,
        type: String,
        mealList: MutableList<Breakfast>,
        position: Int?,
        likeType: String,
        cookBookType: String,
        dialogAddRecipe: Dialog?
    ) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            fUllCookingScheduleViewModel.likeUnlikeRequest({
                BaseApplication.dismissMe()
                handleLikeAndUnlikeApiResponse(it, item, type, mealList, position, dialogAddRecipe)
            }, item.recipe?.uri.toString(), likeType, cookBookType)

        }
    }

    private fun handleLikeAndUnlikeApiResponse(
        result: NetworkResult<String>, item: Breakfast, type: String, mealList: MutableList<Breakfast>, position: Int?, dialogAddRecipe: Dialog?) {
        when (result) {
            is NetworkResult.Success -> handleLikeAndUnlikeSuccessResponse(
                result.data.toString(),
                item,
                type,
                mealList,
                position,
                dialogAddRecipe
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleLikeAndUnlikeSuccessResponse(
        data: String,
        item: Breakfast,
        type: String,
        mealList: MutableList<Breakfast>,
        position: Int?,
        dialogAddRecipe: Dialog?
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                dialogAddRecipe?.dismiss()
                // Toggle the is_like value
                item.is_like = if (item.is_like == 0) 1 else 0
                mealList[position!!] = item

                when (type) {
                    "Breakfast" -> ingredientBreakFastAdapter?.updateList(mealList)
                    "Lunch" -> ingredientLunchAdapter?.updateList(mealList)
                    "Dinner" -> ingredientDinnerAdapter?.updateList(mealList)
                    "Snacks" -> ingredientSnacksAdapter?.updateList(mealList)
                    "Teatime" -> ingredientTeaTimeAdapter?.updateList(mealList)
                }

                // Update the adapter
                /* adapter?.updateList(mealList, type)*/

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

    private fun addFavTypeDialog(
        item: Breakfast, type: String,
        mealList: MutableList<Breakfast>, position: Int?, likeType: String
    ) {
        val dialogAddRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogAddRecipe.setContentView(R.layout.alert_dialog_add_recipe)
        dialogAddRecipe.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogAddRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogAddRecipe.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        spinnerActivityLevel = dialogAddRecipe.findViewById(R.id.spinnerActivityLevel)
        val relCreateNewCookBook =
            dialogAddRecipe.findViewById<RelativeLayout>(R.id.relCreateNewCookBook)
        val relFavourites = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relFavourites)
        val imgCheckBoxOrange = dialogAddRecipe.findViewById<ImageView>(R.id.imgCheckBoxOrange)

        spinnerActivityLevel.setItems(cookbookList.map { it.name })

        dialogAddRecipe.show()
        dialogAddRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        getCookBookList()

        relCreateNewCookBook.setOnClickListener {
            relCreateNewCookBook.setBackgroundResource(R.drawable.light_green_rectangular_bg)
            imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
            dialogAddRecipe.dismiss()
            val bundle = Bundle()
            bundle.putString("value", "New")
            bundle.putString("uri", item.recipe?.uri)
            findNavController().navigate(R.id.createCookBookFragment, bundle)
        }


        rlDoneBtn.setOnClickListener {
            if (spinnerActivityLevel.text.toString().equals("", true)) {
                BaseApplication.alertError(requireContext(), ErrorMessage.selectCookBookError, false)
            } else {
                val cookBookType = cookbookList[spinnerActivityLevel.selectedIndex].id
                recipeLikeAndUnlikeData(
                    item,
                    type,
                    mealList,
                    position,
                    likeType,
                    cookBookType.toString(),
                    dialogAddRecipe
                )
            }
        }
    }

    private fun getCookBookList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            fUllCookingScheduleViewModel.getCookBookRequest {
                BaseApplication.dismissMe()
                handleApiCookBookResponse(it)
            }
        }
    }

    private fun handleApiCookBookResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessCookBookResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessCookBookResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, CookBookListResponse::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null && apiModel.data.size > 0) {
                    cookbookList.retainAll { it == cookbookList[0] }
                    cookbookList.addAll(apiModel.data)
                    // OR directly modify the original list
                    spinnerActivityLevel.setItems(cookbookList.map { it.name })
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

    private fun removeDayDialog(id: Int?, position: Int?, type: String?) {
        val dialogRemoveDay: Dialog = context?.let { Dialog(it) }!!
        dialogRemoveDay.setContentView(R.layout.alert_dialog_remove_day)
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
                removeCookBookApi(id.toString(), dialogRemoveDay, position, type)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
            dialogRemoveDay.dismiss()
        }
    }

    private fun removeCookBookApi(cookedId: String, dialogRemoveDay: Dialog,
        position: Int?, status: String?) {
        BaseApplication.showMe(requireActivity())
        lifecycleScope.launch {
            fUllCookingScheduleViewModel.removeMealApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val cookedModel = gson.fromJson(it.data, CookedTabModel::class.java)
                        if (cookedModel.code == 200 && cookedModel.success) {
                            when (status) {
                                "Breakfast" -> ingredientBreakFastAdapter?.removeItem(position!!)
                                "Lunch" -> ingredientLunchAdapter?.removeItem(position!!)
                                "Dinner" -> ingredientDinnerAdapter?.removeItem(position!!)
                                "Snacks" -> ingredientSnacksAdapter?.removeItem(position!!)
                                "Teatime" -> ingredientTeaTimeAdapter?.removeItem(position!!)
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

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun itemLongClick(position: Int?, status: String?, type: String?) {
        onClickEnabled()
    }

    private fun onClickEnabled() {
        ingredientBreakFastAdapter?.setZiggleEnabled(true)
        ingredientLunchAdapter?.setZiggleEnabled(true)
        ingredientDinnerAdapter?.setZiggleEnabled(true)
        ingredientSnacksAdapter?.setZiggleEnabled(true)
        ingredientTeaTimeAdapter?.setZiggleEnabled(true)
    }

    private fun onClickFalseEnabled() {
        ingredientBreakFastAdapter?.setZiggleEnabled(false)
        ingredientLunchAdapter?.setZiggleEnabled(false)
        ingredientDinnerAdapter?.setZiggleEnabled(false)
        ingredientSnacksAdapter?.setZiggleEnabled(false)
        ingredientTeaTimeAdapter?.setZiggleEnabled(false)
    }

}
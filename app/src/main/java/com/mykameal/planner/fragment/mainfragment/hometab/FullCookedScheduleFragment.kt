package com.mykameal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.OnItemLongClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.CalendarDayAdapter
import com.mykameal.planner.adapter.CalendarDayDateAdapter
import com.mykameal.planner.adapter.IngredientsBreakFastAdapter
import com.mykameal.planner.adapter.IngredientsDinnerAdapter
import com.mykameal.planner.adapter.IngredientsLunchAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.databinding.FragmentFullCookedScheduleBinding
import com.mykameal.planner.model.CalendarDataModel
import com.mykameal.planner.model.DataModel
import com.mykameal.planner.model.DateModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FullCookedScheduleFragment : Fragment(), OnItemClickListener, OnItemLongClickListener {

    private var binding: FragmentFullCookedScheduleBinding? = null
    private var ingredientBreakFastAdapter: IngredientsBreakFastAdapter? = null
    private var ingredientLunchAdapter: IngredientsLunchAdapter? = null
    private var ingredientDinnerAdapter: IngredientsDinnerAdapter? = null
    private var tvWeekRange: TextView? = null
    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var dataList2: MutableList<DataModel> = mutableListOf()
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var calendarDayAdapter: CalendarDayAdapter? = null
    private var calendarAdapter: CalendarDayDateAdapter? = null
    private var statuses: String? = ""
    private var checkStatus: Boolean? = false

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val dateFormat1 = SimpleDateFormat("dd MMM", Locale.getDefault())

    // Define global variables
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    var currentDate = Date() // Current date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFullCookedScheduleBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        binding!!.rlMainFullCooked.setOnClickListener {
            onClickFalseEnabled()
        }

        onClickFalseEnabled()
        fullCookSchBreakFastModel()
        fullCookSchLunchModel()
        fullCookSchDinnerModel()
        initialize()


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
        binding!!.tvCustomDates.text = BaseApplication.formatonlyMonthYear(startDate)
        binding!!.textWeekRange.text = ""+formatDate(startDate)+"-"+formatDate(endDate)

        tvWeekRange?.text = ""+formatDate(startDate)+"-"+formatDate(endDate)
        calendarAdapter=CalendarDayDateAdapter(getDaysBetween(startDate, endDate)) {
            /*// Handle item click if needed
            if (BaseApplication.isOnline(requireActivity())) {
                dataFatchByDate(it)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }*/

        }
        // Update the RecyclerView
        binding!!.recyclerViewWeekDays.adapter =  calendarAdapter

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
                    val childView = recyclerView.findChildViewUnder(dragEvent.x, dragEvent.y) // Call findChildViewUnder on RecyclerView
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
                            if (index==targetPosition){
                                dateModel.status=true
                            }else{
                                dateModel.status=false
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
                    val draggedItem = clipData.getItemAt(0).text.toString() // Assuming text represents the dragged item

                    // Find the target position in RecyclerView
                    val recyclerView = view as RecyclerView // Cast the view to RecyclerView
                    val dropX = dragEvent.x // X-coordinate of the drop
                    val dropY = dragEvent.y // Y-coordinate of the drop
                    val childView = recyclerView.findChildViewUnder(dropX, dropY) // Find the child view under the drop position

                    if (childView != null) {
                        // Get the adapter position of the target item
                        val dropPosition = recyclerView.getChildAdapterPosition(childView)

                        if (dropPosition != RecyclerView.NO_POSITION) {
                            Log.d("ACTION_DROP", "Item dropped at position: $dropPosition")

                            Log.d("ACTION_DROP", "*******$dropPosition")

                            Log.d("date position ", "******"+getDaysBetween(startDate, endDate)[dropPosition].date)

                            Log.d("drop date and days","******"+getDaysBetween(startDate, endDate)[dropPosition].date+"-"+getDaysBetween(startDate, endDate)[dropPosition].day)
                            Toast.makeText(requireContext(),"date "+getDaysBetween(startDate, endDate)[dropPosition].date+"-"+getDaysBetween(startDate, endDate)[dropPosition].day,Toast.LENGTH_LONG).show()
                            Log.d("ACTION_DROP", "Target position: $dropPosition")

                            // Retrieve the list of dates
                            val dateList = getDaysBetween(startDate, endDate)

                            // Update the status of the item at the target position
                            dateList.forEachIndexed { index, dateModel ->
                                if (index==dropPosition){
                                    dateModel.status=true
                                }else{
                                    dateModel.status=false
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

    private fun initialize() {

        binding!!.relCalendarView.setOnClickListener{
            openDialog()
        }

        // Initialize RecyclerView and Adapter
        setupRecyclerView()

//        // Initial week update
//        updateWeek()

        binding!!.rlChangeCookSchedule.setOnClickListener {
//            chooseDayMealTypeDialog()
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
        

        binding!!.rlMainFullCooked.setOnLongClickListener {
            onClickEnabled()
            true
        }

        binding!!.rlMainFullCooked.setOnClickListener {
            onClickFalseEnabled()
        }

        binding!!.imgBackCookingSchedule.setOnClickListener{
            findNavController().navigateUp()
        }

    }

    private fun setupRecyclerView() {
        calendarDayAdapter = CalendarDayAdapter(emptyList()) { day ->
            // Handle item clicks if needed
            Toast.makeText(context, "Clicked on ${day.dayName}, ${day.date}", Toast.LENGTH_SHORT).show()
        }
        binding!!.recyclerViewWeekDays.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
                    val childView = recyclerView.findChildViewUnder(dragEvent.x, dragEvent.y) // Call findChildViewUnder on RecyclerView
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
                    val childView = recyclerView.findChildViewUnder(dropX, dropY) // Find the child view under the drop position

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

    private fun openDialog(){
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
//            binding!!.textWeekRange.text = "$startDate - $endDate"
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
                    dayName = SimpleDateFormat("E", Locale.getDefault()).format(weekCalendar.time),
                    date = weekCalendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        return days
    }


    private fun changeWeekRange(weekRange: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weekRange) // Update the calendar week
        updateWeek() // Refresh the UI and adapter
    }

    private fun updateWeek() {
        val today = Calendar.getInstance()

        // Calculate start and end of the week
        val startOfWeek = calendar.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Calculate the end of the week (Sunday)
        val endOfWeek = calendar.clone() as Calendar
        endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6) // Add 6 days to Monday to reach Sunday

        // Update the week range text
        binding!!.textWeekRange.text =
            "${dateFormat.format(startOfWeek.time)} - ${dateFormat.format(endOfWeek.time)}"

        // Hide the "Previous" button if today is within the current week range
        binding!!.imagePrevious.visibility =
            if (today.time.after(startOfWeek.time) && today.time.before(endOfWeek.time) || today.time == startOfWeek.time) {
                View.GONE
            } else {
                View.VISIBLE
            }

        // Get the current week days and update the adapter
        val daysOfWeek = getDaysOfWeek()
        calendarDayAdapter!!.updateData(daysOfWeek)
    }


    private fun getDaysOfWeek(): List<CalendarDataModel.Day> {
        val days = mutableListOf<CalendarDataModel.Day>()

        // Use a separate calendar instance for calculations
        val weekCalendar = calendar.clone() as Calendar
        weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Start of the week (Monday)

        for (i in 0..6) {
            days.add(
                CalendarDataModel.Day(
                    dayName = SimpleDateFormat("E", Locale.getDefault()).format(weekCalendar.time),
                    date = weekCalendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        return days
    }

    private fun fullCookSchBreakFastModel() {
        if (dataList1 != null) {
            dataList1.clear()
        }
        val data1 = DataModel()
        val data2 = DataModel()

        data1.title = "Pasta"
        data1.isOpen = false
        data1.type = "FullCookSchBreakFast"
        data1.image = R.drawable.breakfast_images

        data2.title = "BBQ"
        data2.isOpen = true
        data2.type = "FullCookSchBreakFast"
        data2.image = R.drawable.chicken_skewers_images

        dataList1.add(data1)
        dataList1.add(data2)

        ingredientBreakFastAdapter = IngredientsBreakFastAdapter(dataList1, requireActivity(), this, this)
        binding!!.rcySearchBreakFast.adapter = ingredientBreakFastAdapter


        setupDragScrollForRecyclerView(binding!!.rcySearchBreakFast)

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


    private fun fullCookSchLunchModel() {
        if (dataList2 != null) {
            dataList2.clear()
        }
        val data1 = DataModel()
        val data2 = DataModel()

        data1.title = "Pasta"
        data1.isOpen = false
        data1.type = "FullCookSchLunch"
        data1.image = R.drawable.lunch_pasta_images

        data2.title = "Bar-B-Q"
        data2.isOpen = false
        data2.type = "FullCookSchLunch"
        data2.image = R.drawable.lunch_bar_b_q_images

        dataList2.add(data1)
        dataList2.add(data2)
        ingredientLunchAdapter = IngredientsLunchAdapter(dataList2, requireActivity(), this, this)
        binding!!.rcySearchLunch.adapter = ingredientLunchAdapter

        setupDragScrollForRecyclerView(binding!!.rcySearchLunch)

    }

    private fun fullCookSchDinnerModel() {
        if (dataList3 != null) {
            dataList3.clear()
        }
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Lasagne"
        data1.isOpen = false
        data1.type = "FullCookSchDinner"
        data1.image = R.drawable.dinner_lasagne_images

        data2.title = "stawberry"
        data2.isOpen = false
        data2.type = "FullCookSchDinner"
        data2.image = R.drawable.dinner_grilled_chicken_legs_images

        data3.title = "Juices"
        data3.isOpen = false
        data3.type = "FullCookSchDinner"
        data3.image = R.drawable.chicken_skewers_images

        data4.title = "Lasagne"
        data4.isOpen = false
        data4.type = "FullCookSchDinner"
        data4.image = R.drawable.dinner_lasagne_images

        data5.title = "stawberry"
        data5.isOpen = false
        data5.type = "FullCookSchDinner"
        data5.image = R.drawable.dinner_grilled_chicken_legs_images

        dataList3.add(data1)
        dataList3.add(data2)
        dataList3.add(data3)
        dataList3.add(data4)
        dataList3.add(data5)

        ingredientDinnerAdapter = IngredientsDinnerAdapter(dataList3, requireActivity(), this, binding!!.scroll,this)
        binding!!.rcySearchDinner.adapter = ingredientDinnerAdapter

        setupDragScrollForRecyclerView(binding!!.rcySearchDinner)




    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

        if (type == "heart") {
            addRecipeDialog(position, type)
        } else if (type == "minus") {
            if (status == "1") {
                removeDayDialog(position, type)
            }
        } else if (type == "missingIng") {
            findNavController().navigate(R.id.missingIngredientsFragment)
        } else {
            findNavController().navigate(R.id.recipeDetailsFragment)
        }
    }

    private fun addRecipeDialog(position: Int?, type: String) {

        val dialogAddRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogAddRecipe.setContentView(R.layout.alert_dialog_add_recipe)
        dialogAddRecipe.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogAddRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogAddRecipe.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val relCreateNewCookBook =
            dialogAddRecipe.findViewById<RelativeLayout>(R.id.relCreateNewCookBook)
        val relFavourites = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relFavourites)
        val imgCheckBoxOrange = dialogAddRecipe.findViewById<ImageView>(R.id.imgCheckBoxOrange)

        dialogAddRecipe.show()
        dialogAddRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        relCreateNewCookBook.setOnClickListener {
            statuses = "newCook"
            relCreateNewCookBook.setBackgroundResource(R.drawable.light_green_rectangular_bg)
            imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
            relFavourites.setBackgroundResource(0)
            checkStatus = false
        }

        imgCheckBoxOrange.setOnClickListener {
            if (checkStatus == true) {
                statuses = ""
                imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
                relFavourites.setBackgroundResource(0)
                relCreateNewCookBook.setBackgroundResource(0)
                relCreateNewCookBook.background = null
                checkStatus = false
            } else {
                relFavourites.setBackgroundResource(R.drawable.light_green_rectangular_bg)
                relCreateNewCookBook.setBackgroundResource(0)
                statuses = "favourites"
                imgCheckBoxOrange.setImageResource(R.drawable.orange_checkbox_images)
                checkStatus = true
            }
        }


        rlDoneBtn.setOnClickListener {
            if (statuses == "") {
                Toast.makeText(
                    requireActivity(),
                    "Please select atleast one of them",
                    Toast.LENGTH_LONG
                ).show()
            } else if (statuses == "favourites") {
                dialogAddRecipe.dismiss()
            } else {
                dialogAddRecipe.dismiss()
                val bundle = Bundle()
                bundle.putString("value", "New")
                findNavController().navigate(R.id.createCookBookFragment, bundle)
            }
        }
    }

    private fun removeDayDialog(position: Int?, type: String?) {
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
            if (type == "FullCookSchBreakFast") {
                dataList1.removeAt(position!!)
            } else if (type == "FullCookSchLunch") {
                dataList2.removeAt(position!!)
            } else {
                dataList3.removeAt(position!!)
            }
            /*
                        chooseDayDialog()
            */
            dialogRemoveDay.dismiss()
        }
    }


    private fun changeWeek(weeks: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        updateWeekRange()
    }

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


    override fun itemLongClick(position: Int?, status: String?, type: String?) {

        onClickEnabled()

    }

    private fun onClickEnabled() {
        ingredientBreakFastAdapter?.setZiggleEnabled(true)
        ingredientLunchAdapter?.setZiggleEnabled(true)
        ingredientDinnerAdapter?.setZiggleEnabled(true)
    }

    private fun onClickFalseEnabled() {
        ingredientBreakFastAdapter?.setZiggleEnabled(false)
        ingredientLunchAdapter?.setZiggleEnabled(false)
        ingredientDinnerAdapter?.setZiggleEnabled(false)
    }

}
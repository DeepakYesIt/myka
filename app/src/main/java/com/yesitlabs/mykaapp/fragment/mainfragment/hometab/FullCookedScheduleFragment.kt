package com.yesitlabs.mykaapp.fragment.mainfragment.hometab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.OnItemLongClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.CalendarDayAdapter
import com.yesitlabs.mykaapp.adapter.ChooseDayAdapter
import com.yesitlabs.mykaapp.adapter.IngredientsBreakFastAdapter
import com.yesitlabs.mykaapp.adapter.IngredientsDinnerAdapter
import com.yesitlabs.mykaapp.adapter.IngredientsLunchAdapter
import com.yesitlabs.mykaapp.commonworkutils.WeekDaysCalculator
import com.yesitlabs.mykaapp.databinding.FragmentFullCookedScheduleBinding
import com.yesitlabs.mykaapp.model.CalendarDataModel
import com.yesitlabs.mykaapp.model.DataModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FullCookedScheduleFragment : Fragment(), OnItemClickListener, OnItemLongClickListener {

    private var binding: FragmentFullCookedScheduleBinding? = null
    private var ingredientBreakFastAdapter: IngredientsBreakFastAdapter? = null
    private var ingredientLunchAdapter: IngredientsLunchAdapter? = null
    private var ingredientDinnerAdapter: IngredientsDinnerAdapter? = null
    private var chooseDayAdapter: ChooseDayAdapter? = null
    private var rcyChooseDaySch: RecyclerView? = null
    private var tvWeekRange: TextView? = null
    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var dataList2: MutableList<DataModel> = mutableListOf()
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var calendarDayAdapter: CalendarDayAdapter? = null
    private var statuses:String?=""
    private var checkStatus:Boolean?=false

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFullCookedScheduleBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        onClickFalseEnabled()
        fullCookSchBreakFastModel()
        fullCookSchLunchModel()
        fullCookSchDinnerModel()
        initialize()

        return binding!!.root
    }


    private fun initialize() {

        binding!!.relCalendarView.setOnClickListener {
            openDialog()
        }

        updateWeek()

        binding!!.rlChangeCookSchedule.setOnClickListener {
//            chooseDayMealTypeDialog()
        }

        binding!!.imagePrevious.setOnClickListener {
            changeWeekRange(-1)
        }

        binding!!.imageNext.setOnClickListener {
            changeWeekRange(1)
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

    private fun openDialog() {

        val dialog = Dialog(requireActivity())

        // Set custom layout
        dialog.setContentView(R.layout.dialog_calendar)

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)

        calendarView.setOnDateChangeListener { view: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            // Month is zero-based (January = 0), so add 1 for human-readable format
            val selectedDate = dayOfMonth.toString() + "-" + (month + 1) + "-" + year
            val list = WeekDaysCalculator.getWeekDays(selectedDate);
            val resultList = mutableListOf<Pair<String, String>>()
            list.forEach {
                Log.d("TESTING_LAWCO", it.toString())
                val arr = it.split("-")
                resultList.add(Pair<String, String>(arr[0], arr[1]))
            }
            resultList.forEach {
                Log.d("TESTING_LAWCO", it.first + " " + it.second)
            }
        }

        dialog.show()
    }

    private fun changeWeekRange(weekRange: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weekRange)
        updateWeek()
    }

    private fun updateWeek() {
        val today = Calendar.getInstance()

        // Set the start of the week to Monday
        val startOfWeek = calendar.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }.time

        // Set the end of the week (Sunday)
        val endOfWeek = calendar.apply {
            add(Calendar.DAY_OF_WEEK, 6)
        }.time

        // Update the week range text
        binding!!.textWeekRange.text =
            "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"

        // Hide the "Previous" button if today is within the current week range
        binding!!.imagePrevious.visibility =
            if (today.time.after(startOfWeek) && today.time.before(endOfWeek) || today.time == startOfWeek) {
                View.GONE
            } else {
                View.VISIBLE
            }

        // Update the RecyclerView
        binding!!.recyclerViewWeekDays.adapter = CalendarDayAdapter(getDaysOfWeek()) {
            // Handle item click if needed
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

  /*  private fun initialize() {

        binding!!.relCalendarView.setOnClickListener{
            openDialog()
        }

        updateWeek()

        binding!!.rlChangeCookSchedule.setOnClickListener {
//            chooseDayMealTypeDialog()
        }

        binding!!.imagePrevious.setOnClickListener {
            changeWeekRange(-1)
        }

        binding!!.imageNext.setOnClickListener {
            changeWeekRange(1)
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

    private fun openDialog(){

        val dialog = Dialog(requireActivity())

        // Set custom layout
        dialog.setContentView(R.layout.dialog_calendar)

        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)

        calendarView.setOnDateChangeListener { view: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            // Month is zero-based (January = 0), so add 1 for human-readable format
            val selectedDate = dayOfMonth.toString() + "-"+(month + 1)+"-" + year
            val list = WeekDaysCalculator.getWeekDays(selectedDate);
            val resultList = mutableListOf<Pair<String,String>>()
            list.forEach{
                Log.d("TESTING_LAWCO", it.toString())
                val arr = it.split("-")
                resultList.add(Pair<String,String>(arr[0],arr[1]))
            }
            resultList.forEach {
                Log.d("TESTING_LAWCO", it.first+" "+it.second)
            }
        }

        dialog.show()
    }

    private fun changeWeekRange(weekRange: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weekRange)
        updateWeek()
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
        binding!!.recyclerViewWeekDays.adapter = CalendarDayAdapter(getDaysOfWeek()) {
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

    }*/

    private fun fullCookSchBreakFastModel() {
        if (dataList1!=null){
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
    }

    private fun fullCookSchLunchModel() {
        if (dataList2!=null){
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
    }

    private fun fullCookSchDinnerModel() {
        if (dataList3!=null){
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

        ingredientDinnerAdapter = IngredientsDinnerAdapter(dataList3, requireActivity(), this, this)
        binding!!.rcySearchDinner.adapter = ingredientDinnerAdapter
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

        if (type == "heart") {
            addRecipeDialog(position, type)
        } else if (type == "minus") {
            if (status == "1") {
                removeDayDialog(position, type)
            }
        } else if (type=="missingIng") {
            findNavController().navigate(R.id.missingIngredientsFragment)
        }else{
            findNavController().navigate(R.id.recipeDetailsFragment)
        }
    }

    private fun addRecipeDialog(position: Int?, type: String) {

        val dialogAddRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogAddRecipe.setContentView(R.layout.alert_dialog_add_recipe)
        dialogAddRecipe.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogAddRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogAddRecipe.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val relCreateNewCookBook = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relCreateNewCookBook)
        val relFavourites = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relFavourites)
        val imgCheckBoxOrange = dialogAddRecipe.findViewById<ImageView>(R.id.imgCheckBoxOrange)

        dialogAddRecipe.show()
        dialogAddRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        relCreateNewCookBook.setOnClickListener{
            statuses="newCook"
            relCreateNewCookBook.setBackgroundResource(R.drawable.light_green_rectangular_bg)
            imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
            relFavourites.setBackgroundResource(0)
            checkStatus=false
        }

        imgCheckBoxOrange.setOnClickListener{
            if (checkStatus==true){
                statuses=""
                imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
                relFavourites.setBackgroundResource(0)
                relCreateNewCookBook.setBackgroundResource(0)
                relCreateNewCookBook.background=null
                checkStatus=false
            }else{
                relFavourites.setBackgroundResource(R.drawable.light_green_rectangular_bg)
                relCreateNewCookBook.setBackgroundResource(0)
                statuses="favourites"
                imgCheckBoxOrange.setImageResource(R.drawable.orange_checkbox_images)
                checkStatus=true
            }
        }


        rlDoneBtn.setOnClickListener{
            if (statuses==""){
                Toast.makeText(requireActivity(),"Please select atleast one of them",Toast.LENGTH_LONG).show()
            }else if (statuses=="favourites"){
                dialogAddRecipe.dismiss()
            }else{
                dialogAddRecipe.dismiss()
                val bundle=Bundle()
                bundle.putString("value","New")
                findNavController().navigate(R.id.createCookBookFragment,bundle)
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
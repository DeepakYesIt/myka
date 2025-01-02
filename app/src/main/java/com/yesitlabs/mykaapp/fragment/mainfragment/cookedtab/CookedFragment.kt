package com.yesitlabs.mykaapp.fragment.mainfragment.cookedtab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.model.FoodItem
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterFoodListItem
import com.yesitlabs.mykaapp.adapter.CalendarDayIndicatorAdapter
import com.yesitlabs.mykaapp.databinding.FragmentCookedBinding
import com.yesitlabs.mykaapp.model.CalendarDataModel
import com.yesitlabs.mykaapp.model.DataModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CookedFragment : Fragment(),OnItemClickListener {
    private var binding: FragmentCookedBinding? = null
    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var dataList2: MutableList<DataModel> = mutableListOf()
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var foodListItemAdapter: AdapterFoodListItem? = null

//    private lateinit var adapter: FoodListAdapter
//    private lateinit var adapterLunch: FoodListAdapter
//    private lateinit var adapterDinner: FoodListAdapter
//    private lateinit var adapterFreezer: FoodListAdapter
//    private lateinit var adapterLunchFreezer: FoodListAdapter
//    private lateinit var adapterDinnerFreezer: FoodListAdapter
    private var calendarDayAdapter: CalendarDayIndicatorAdapter? = null

    private lateinit var handler: Handler
    private val foodList = mutableListOf<FoodItem>()
    private val calendar = Calendar.getInstance()
    private var isOpened:Boolean?=null
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCookedBinding.inflate(inflater, container, false)
        (activity as MainActivity?)?.changeBottom("cooked")

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        updateWeek()

        binding!!.imagePrevious.setOnClickListener {
            changeWeekRange(-1)
        }

        binding!!.imageNext.setOnClickListener {
            changeWeekRange(1)
        }

        binding!!.imageBackIcon.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.relCalendarYear.setOnClickListener{
            if (isOpened==true){
                binding!!.llCalendarViewEvents.visibility=View.GONE
                isOpened=false
            }else{
                binding!!.llCalendarViewEvents.visibility=View.VISIBLE
                isOpened=true
            }
        }

        binding!!.textAddMeals.setOnClickListener{
            findNavController().navigate(R.id.addMealCookedFragment)
        }

        handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            binding!!.llEmptyFridge.visibility = View.GONE

            binding!!.llFilledFridge.visibility = View.VISIBLE
            binding!!.llFilledFreezer.visibility = View.GONE
        }, 7000)

        toggleFridgeToFreezer()
        cookedBreakFastModel()
        cookedLunchModel()
        cookedDinnerModel()
//        adapterInitialize()
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

    private fun cookedBreakFastModel() {
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

        foodListItemAdapter = AdapterFoodListItem(dataList1, requireActivity(), this)
        binding!!.rcvBreakfast.adapter = foodListItemAdapter
        binding!!.rcvBreakfastFreezer.adapter = foodListItemAdapter
    }

    private fun cookedLunchModel() {
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
        foodListItemAdapter = AdapterFoodListItem(dataList2, requireActivity(), this)
        binding!!.rcvLunch.adapter = foodListItemAdapter
        binding!!.rcvLunchFreezer.adapter = foodListItemAdapter
    }

    private fun cookedDinnerModel() {
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

        foodListItemAdapter = AdapterFoodListItem(dataList3, requireActivity(), this)
        binding!!.rcvDinner.adapter = foodListItemAdapter
        binding!!.rcvDinnerFreezer.adapter = foodListItemAdapter
    }

//    private fun adapterInitialize(){
//        adapter = FoodListAdapter(requireContext(), itemList = foodList,this)
//        adapterLunch = FoodListAdapter(requireContext(), itemList = foodList,this)
//        adapterDinner = FoodListAdapter(requireContext(), itemList = foodList,this)
//        adapterFreezer = FoodListAdapter(requireContext(), itemList = foodList,this)
//        adapterLunchFreezer = FoodListAdapter(requireContext(), itemList = foodList,this)
//        adapterDinnerFreezer = FoodListAdapter(requireContext(), itemList = foodList,this)
//
//        binding!!.rcvBreakfast.adapter = adapter
//        binding!!.rcvLunch.adapter = adapterLunch
//        binding!!.rcvDinner.adapter = adapterDinner
//        binding!!.rcvBreakfastFreezer.adapter = adapterFreezer
//        binding!!.rcvLunchFreezer.adapter = adapterLunchFreezer
//        binding!!.rcvDinnerFreezer.adapter = adapterDinnerFreezer
//
//        val initialList = List(6) { index ->
//            if (index % 2 == 0) {
//                FoodItem("Burger", R.drawable.ic_food_image, R.drawable.ic_food_clock, R.drawable.ic_apple_icon, R.drawable.ic_wishlist_icon, "1 hour ago", "Serves 3")
//            } else {
//                FoodItem("Pasta", R.drawable.ic_food_image, R.drawable.ic_food_clock, R.drawable.ic_apple_icon, R.drawable.ic_wishlist_icon, "5 days ago", "Serves 2")
//            }
//        }
//        adapter.addItems(initialList)
//        adapterLunch.addItems(initialList)
//        adapterDinner.addItems(initialList)
//        adapterFreezer.addItems(initialList)
//        adapterLunchFreezer.addItems(initialList)
//        adapterDinnerFreezer.addItems(initialList)
//    }

    private  fun toggleFridgeToFreezer(){

        binding!!.textFridge.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFridge.setTextColor(Color.WHITE)
            binding!!.textFreezer.setTextColor(Color.BLACK)
            binding!!.llFilledFridge.visibility = View.VISIBLE
            binding!!.llFilledFreezer.visibility = View.GONE
            binding!!.llEmptyFridge.visibility = View.GONE

            cookedBreakFastModel()
            cookedLunchModel()
            cookedDinnerModel()
        }

        binding!!.textFreezer.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFridge.setTextColor(Color.BLACK)
            binding!!.textFreezer.setTextColor(Color.WHITE)
            binding!!.llFilledFreezer.visibility = View.VISIBLE
            binding!!.llFilledFridge.visibility = View.GONE
            binding!!.llEmptyFridge.visibility = View.GONE

            cookedBreakFastModel()
            cookedLunchModel()
            cookedDinnerModel()

        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        removeMealDialog()

    }

    private fun removeMealDialog() {
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
                dialogRemoveDay.dismiss()
            }

    }

}
package com.yesitlabs.mykaapp.fragment.mainfragment.cookedtab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.commonworkutils.WeekDaysCalculator
import com.yesitlabs.mykaapp.databinding.FragmentAddMealCookedBinding
import java.time.DayOfWeek
import java.time.LocalDate

class AddMealCookedFragment : Fragment() {
    private var binding:FragmentAddMealCookedBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddMealCookedBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

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

        }

        binding!!.textFreezer.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFridge.setTextColor(Color.BLACK)
            binding!!.textFreezer.setTextColor(Color.WHITE)

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


}
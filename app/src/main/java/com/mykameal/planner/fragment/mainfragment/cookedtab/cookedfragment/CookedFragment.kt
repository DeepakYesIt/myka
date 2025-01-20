package com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterFoodListItem
import com.mykameal.planner.adapter.CalendarDayIndicatorAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentCookedBinding
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModel
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModelData
import com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.viewmodel.CookedTabViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import com.mykameal.planner.model.CalendarDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CookedFragment : Fragment(),OnItemClickListener {
    private var binding: FragmentCookedBinding? = null
    private var cookedTabViewModel:CookedTabViewModel?=null

    private var foodListItemAdapter: AdapterFoodListItem? = null
    private var calendarDayAdapter: CalendarDayIndicatorAdapter? = null

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

        cookedTabViewModel = ViewModelProvider(this)[CookedTabViewModel::class.java]

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

        if (BaseApplication.isOnline(requireActivity())) {
            cookedTabApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

    /*    handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            binding!!.llEmptyFridge.visibility = View.GONE

            binding!!.llFilledFridge.visibility = View.VISIBLE
            binding!!.llFilledFreezer.visibility = View.GONE
        }, 7000)*/

  /*      toggleFridgeToFreezer()
        cookedBreakFastModel()
        cookedLunchModel()
        cookedDinnerModel()*/
//        adapterInitialize()
    }

    private fun cookedTabApi() {
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
                            }else{
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
            },"","")
        }
    }

    private fun showDataInUi(cookedTabModelData: CookedTabModelData) {
        if (cookedTabModelData!=null){
            if (cookedTabModelData.Breakfast!=null && cookedTabModelData.Breakfast.size>0){
                foodListItemAdapter= AdapterFoodListItem(cookedTabModelData.Breakfast,requireActivity(),this)
                binding!!.rcvBreakfast.adapter = foodListItemAdapter
            }
        }
    }


    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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


    private  fun toggleFridgeToFreezer(){

        binding!!.textFridge.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFridge.setTextColor(Color.WHITE)
            binding!!.textFreezer.setTextColor(Color.BLACK)
            binding!!.llFilledFridge.visibility = View.VISIBLE
            binding!!.llEmptyFridge.visibility = View.GONE

        }

        binding!!.textFreezer.setOnClickListener {
            binding!!.textFridge.setBackgroundResource(R.drawable.unselected_button_bg)
            binding!!.textFreezer.setBackgroundResource(R.drawable.selected_button_bg)
            binding!!.textFridge.setTextColor(Color.BLACK)
            binding!!.textFreezer.setTextColor(Color.WHITE)
            binding!!.llFilledFridge.visibility = View.GONE
            binding!!.llEmptyFridge.visibility = View.GONE


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
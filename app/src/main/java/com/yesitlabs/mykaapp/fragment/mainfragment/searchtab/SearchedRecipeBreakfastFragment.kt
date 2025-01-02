package com.yesitlabs.mykaapp.fragment.mainfragment.searchtab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterPlanBreakFast
import com.yesitlabs.mykaapp.adapter.CalendarDayAdapter
import com.yesitlabs.mykaapp.adapter.ChooseDayAdapter
import com.yesitlabs.mykaapp.databinding.FragmentSearchedRecipeBreakfastBinding
import com.yesitlabs.mykaapp.model.DataModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SearchedRecipeBreakfastFragment : Fragment(),OnItemClickListener {

    private var binding:FragmentSearchedRecipeBreakfastBinding?=null
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var planBreakFastAdapter: AdapterPlanBreakFast? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private var chooseDayAdapter: ChooseDayAdapter? = null
    private var rcyChooseDaySch: RecyclerView? = null
    private var tvWeekRange: TextView? = null
    private var clickable:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSearchedRecipeBreakfastBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        planDinnerModel()
        initialize()

        return binding!!.root
    }

    private fun initialize() {
        binding!!.relBackSearched.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.imgHeartRed.setOnClickListener {
            findNavController().navigate(R.id.cookBookFragment)
        }

        binding!!.imgBasketIcon.setOnClickListener {
            findNavController().navigate(R.id.basketScreenFragment)
        }
    }

    private fun planDinnerModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()

        data1.title = "Bread"
        data1.isOpen = false
        data1.rating="4.1(121)"
        data1.price="3.4"
        data1.type = "BreakFastPlan"
        data1.image = R.drawable.bread_dinner_image

        data2.title = "Juice"
        data2.isOpen = false
        data2.rating="4.4(128)"
        data2.price="3.5"
        data2.type = "BreakFastPlan"
        data2.image = R.drawable.fresh_juice_glass_image

        data3.title = "Bar-B-Q"
        data3.isOpen = false
        data3.rating="4.3(125)"
        data3.price="3.2"
        data3.type = "BreakFastPlan"
        data3.image = R.drawable.bar_b_q_breakfast_image

        dataList3.add(data1)
        dataList3.add(data2)
        dataList3.add(data3)

        planBreakFastAdapter = AdapterPlanBreakFast(dataList3, requireActivity(),this)
        binding!!.rcySearchedItem.adapter = planBreakFastAdapter
    }

    private fun chooseDayDialog() {
        val dialogChooseDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseDay.setContentView(R.layout.alert_dialog_choose_day)
        dialogChooseDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rcyChooseDaySch = dialogChooseDay.findViewById(R.id.rcyChooseDaySch)
        tvWeekRange = dialogChooseDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseDay.show()
        updateWeekRange()
        dialogChooseDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        cookingScheduleModel()

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
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()

        data1.title = "Monday"
        data1.isOpen = false
        data1.type = "CookingSchedule"

        data2.title = "Tuesday"
        data2.isOpen = false
        data2.type = "CookingSchedule"

        data3.title = "Wednesday"
        data3.isOpen = false
        data3.type = "CookingSchedule"

        data4.title = "Thursday"
        data4.isOpen = false
        data4.type = "CookingSchedule"

        data5.title = "Friday"
        data5.isOpen = false
        data5.type = "CookingSchedule"

        data6.title = "Saturday"
        data6.isOpen = false
        data6.type = "CookingSchedule"

        data7.title = "Sunday"
        data7.isOpen = false
        data7.type = "CookingSchedule"

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)

        chooseDayAdapter = ChooseDayAdapter(dataList, requireActivity())
        rcyChooseDaySch!!.adapter = chooseDayAdapter
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

    private fun chooseDayMealTypeDialog() {
        val dialogChooseMealDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseMealDay.setContentView(R.layout.alert_dialog_choose_day_meal_type)
        dialogChooseMealDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseMealDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvWeekRange = dialogChooseMealDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseMealDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseMealDay.findViewById<ImageView>(R.id.btnPrevious)
        val imgBreakfastRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imgBreakfastRadio)
        val imageLunchRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageLunchRadio)
        val imageDinnerRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageDinnerRadio)
        val imageSnacksRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageSnacksRadio)
        val imageBrunchRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageBrunchRadio)
        val btnNext = dialogChooseMealDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseMealDay.show()
        updateWeekRange()
        dialogChooseMealDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        imgBreakfastRadio.setOnClickListener {
            imgBreakfastRadio.setImageResource(R.drawable.radio_select_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageLunchRadio.setOnClickListener {
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_select_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageDinnerRadio.setOnClickListener {
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_select_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageSnacksRadio.setOnClickListener{
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_select_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageBrunchRadio.setOnClickListener{
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_select_icon)
        }

        rlDoneBtn.setOnClickListener {
            clickable="clicked"

            dialogChooseMealDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (status=="1"){
            chooseDayDialog()
        }else{
            findNavController().navigate(R.id.basketScreenFragment)
        }

    }

}
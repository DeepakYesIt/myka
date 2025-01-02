package com.yesitlabs.mykaapp.fragment.mainfragment.plantab

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
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterPlanBreakFast
import com.yesitlabs.mykaapp.adapter.CalendarDayAdapter
import com.yesitlabs.mykaapp.adapter.ChooseDayAdapter
import com.yesitlabs.mykaapp.adapter.ImageViewPagerAdapter
import com.yesitlabs.mykaapp.databinding.FragmentPlanBinding
import com.yesitlabs.mykaapp.model.CalendarDataModel
import com.yesitlabs.mykaapp.model.DataModel
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Locale

class PlanFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentPlanBinding? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private var calendarDayAdapter: CalendarDayAdapter? = null
    private var chooseDayAdapter: ChooseDayAdapter? = null
    private var dataList1: MutableList<DataModel> = mutableListOf()
    private var dataList2: MutableList<DataModel> = mutableListOf()
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var rcyChooseDaySch: RecyclerView? = null
    private var tvWeekRange: TextView? = null
    private var planBreakFastAdapter: AdapterPlanBreakFast? = null
    private var status:Boolean=true
    private var clickable:String?=""

    lateinit var  adapter : ImageViewPagerAdapter

    private lateinit var layonboarding_indicator : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlanBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        (activity as MainActivity?)?.changeBottom("plan")

        planBreakFastModel()
        planLunchModel()
        planDinnerModel()

        binding!!.tvAddAnotherMealBtn.setOnClickListener{
            addAnotherMealDialog()
        }

        binding!!.tvSwap.setOnClickListener {
            dialogDailyInspiration()
        }

        binding!!.tvSwap2.setOnClickListener {
            dialogDailyInspiration()
        }

        binding!!.imageProfile.setOnClickListener{
            findNavController().navigate(R.id.settingProfileFragment)
        }

        binding!!.imgHearRedIcons.setOnClickListener{
            findNavController().navigate(R.id.cookBookFragment)
        }

        binding!!.imgBasketIcon.setOnClickListener{
            findNavController().navigate(R.id.basketScreenFragment)
        }

        binding!!.rlAddDayToBasket.setOnClickListener{
            if (clickable==""){

            }else{
                findNavController().navigate(R.id.basketScreenFragment)
            }
        }

        binding!!.tvConfirmBtn.setOnClickListener {
            binding!!.llCalculateBmr.visibility=View.VISIBLE
            binding!!.relBreakFastsss.visibility=View.VISIBLE
            binding!!.relBreakFastsLunch.visibility=View.VISIBLE
            binding!!.rcyBreakFast.visibility=View.GONE
            binding!!.rcyLunch.visibility=View.GONE
        }

        val imageList = Arrays.asList<Int>(
            R.drawable.ic_food_image,
            R.drawable.ic_food_image,
            R.drawable.ic_food_image
        )
        adapter =ImageViewPagerAdapter(requireContext(), imageList)

        updateWeek()

        return binding!!.root
    }

    private fun dialogDailyInspiration(){
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

                textBreakfast.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                textDinner.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
            }

            llLunch.setOnClickListener {
                viewBreakfast.visibility = View.GONE
                viewLunch.visibility = View.VISIBLE
                viewDinner.visibility = View.GONE
                textBreakfast.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                textDinner.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
            }

            llDinner.setOnClickListener {
                viewBreakfast.visibility = View.GONE
                viewLunch.visibility = View.GONE
                viewDinner.visibility = View.VISIBLE
                textBreakfast.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                textDinner.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
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
        }}

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

    private fun setUpOnBoardingIndicator() {
        val indicator = arrayOfNulls<ImageView>(adapter!!.getItemCount())
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(10, 0, 10, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView( requireActivity())
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
        data1.rating="4.1(121)"
        data1.price="3.2"
        data1.type = "BreakFastPlan"
        data1.image = R.drawable.bread_breakfast_image

        data2.title = "Juice"
        data2.isOpen = false
        data2.rating="4.4(128)"
        data2.price="3.4"
        data2.type = "BreakFastPlan"
        data2.image = R.drawable.fresh_juice_glass_image

        data3.title = "Bar-B-Q"
        data3.isOpen = false
        data3.rating="4.3(125)"
        data3.price="3.5"
        data3.type = "BreakFastPlan"
        data3.image = R.drawable.bar_b_q_breakfast_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)

        planBreakFastAdapter = AdapterPlanBreakFast(dataList1, requireActivity(),this)
        binding!!.rcyBreakFast.adapter = planBreakFastAdapter
    }

    private fun planLunchModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()

        data1.title = "Bread"
        data1.isOpen = false
        data1.rating="4.1(121)"
        data1.price="3.2"
        data1.type = "BreakFastPlan"
        data1.image = R.drawable.bread_lunch_image

        data2.title = "Juice"
        data2.isOpen = false
        data2.rating="4.4(128)"
        data2.price="3.4"
        data2.type = "BreakFastPlan"
        data2.image = R.drawable.bar_b_q_breakfast_image

        data3.title = "Bar-B-Q"
        data3.isOpen = false
        data3.rating="4.3(125)"
        data3.price="3.5"
        data3.type = "BreakFastPlan"
        data3.image = R.drawable.bar_b_q_breakfast_image

        dataList2.add(data1)
        dataList2.add(data2)
        dataList2.add(data3)

        planBreakFastAdapter = AdapterPlanBreakFast(dataList2, requireActivity(),this)
        binding!!.rcyLunch.adapter = planBreakFastAdapter
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
        binding!!.rcyDinner.adapter = planBreakFastAdapter
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
            binding!!.rlAddDayToBasket.setBackgroundResource(R.drawable.green_btn_background)
            binding!!.rlAddDayToBasket.isClickable=true
            dialogChooseMealDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }

    }

    private fun addAnotherMealDialog() {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_add_another_meal)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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

        rlSelectDessert.setOnClickListener{
            if (status){
                status=false
                val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.drop_up_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
                relSelectedSnack.visibility=View.VISIBLE
            }else{
                status=true
                val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.drop_down_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
                relSelectedSnack.visibility=View.GONE
            }
        }

        rlSelectSnack.setOnClickListener{
            tvChooseDessert.text="Snack"
            val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.drop_down_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
            relSelectedSnack.visibility=View.GONE
            status=true
        }

        rlSelectBrunch.setOnClickListener{
            tvChooseDessert.text="Brunch"
            val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.drop_down_icon)
            val drawableStart = ContextCompat.getDrawable(requireActivity(), R.drawable.gender_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            tvChooseDessert.setCompoundDrawables(null, null, drawableEnd, null)
            relSelectedSnack.visibility=View.GONE
            status=true
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (status=="1"){
            chooseDayDialog()
        }else if (status=="2"){
            findNavController().navigate(R.id.basketScreenFragment)
        }else{
            findNavController().navigate(R.id.recipeDetailsFragment)
        }
    }
}
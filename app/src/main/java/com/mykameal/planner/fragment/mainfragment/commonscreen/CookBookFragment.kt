package com.mykameal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.OnItemSelectListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterCookBookDetailsItem
import com.mykameal.planner.adapter.AdapterCookBookItem
import com.mykameal.planner.adapter.ChooseDayAdapter
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentCookBookBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.cookbookviewmodel.CookBookViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.CookBookListResponse
import com.mykameal.planner.messageclass.ErrorMessage
import com.mykameal.planner.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class CookBookFragment : Fragment(), OnItemClickListener, OnItemSelectListener {

    private var binding: FragmentCookBookBinding? = null
    private var adapterCookBookItem: AdapterCookBookItem? = null
    private var adapterCookBookDetailsItem: AdapterCookBookDetailsItem? = null
    private var status: Boolean? = null
    private var rcyChooseDaySch: RecyclerView? = null
    private var tvWeekRange: TextView? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private var chooseDayAdapter: ChooseDayAdapter? = null
    private lateinit var viewModel: CookBookViewModel
    var cookbookList: MutableList<com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentCookBookBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE
        viewModel = ViewModelProvider(requireActivity())[CookBookViewModel::class.java]


        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        cookbookList.clear()

        val data1= com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data("","",0,"R.drawable.add_more_cookbook_icon","Add",0,"",R.drawable.add_more_cookbook_icon)
        val data2= com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data("","",0,"R.drawable.favourites_cookbook_image","Favourites",1,"",R.drawable.favourites_cookbook_image)
        cookbookList.add(0,data1)
        cookbookList.add(1,data2)

//        cookBookModel()

        cookBookDetailsModel()

        initialize()

        if (BaseApplication.isOnline(requireActivity())) {
            getCookBookList()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        return binding!!.root
    }

    private fun getCookBookList(){
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.getCookBookRequest {
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
                if (apiModel.data!=null && apiModel.data.size>0){
//                    cookbookList.retainAll { it == cookbookList[0] || it == cookbookList[1] }
                    cookbookList.addAll(apiModel.data)
                    // OR directly modify the original list
                    adapterCookBookItem = AdapterCookBookItem(cookbookList, requireActivity(), this)
                    binding!!.rcyCookBookAdding.adapter = adapterCookBookItem

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

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun initialize() {

        binding!!.imgBackCookbook.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun cookBookModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Add"
        data1.isOpen = false
        data1.image = R.drawable.add_more_cookbook_icon
        data1.type = "CookBook"

        data2.title = "Favourites"
        data2.isOpen = false
        data2.image = R.drawable.favourites_cookbook_image
        data2.type = "CookBook"

        data3.title = "Christmas"
        data3.isOpen = false
        data3.image = R.drawable.christmas_cookbook_image
        data3.type = "CookBook"

        data4.title = "Party"
        data4.isOpen = false
        data4.image = R.drawable.party_cookbook_image
        data4.type = "CookBook"

        data5.title = "Coriander"
        data5.isOpen = false
        data5.image = R.drawable.coriander_cookbook_image
        data5.type = "CookBook"

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)

       /* adapterCookBookItem = AdapterCookBookItem(dataList, requireActivity(), this)
        binding!!.rcyCookBookAdding.adapter = adapterCookBookItem*/
    }

    private fun cookBookDetailsModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()

        data1.title = "Bread"
        data1.isOpen = false
        data1.image = R.drawable.bread_breakfast_image
        data1.type = "CookBookDetails"

        data2.title = "Bar BQ"
        data2.isOpen = false
        data2.image = R.drawable.bbq_image
        data2.type = "CookBookDetails"

        data3.title = "Salad"
        data3.isOpen = false
        data3.image = R.drawable.salaad_image
        data3.type = "CookBookDetails"

        data4.title = "Sandwich"
        data4.isOpen = false
        data4.image = R.drawable.bbq_image
        data4.type = "CookBookDetails"

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)

        adapterCookBookDetailsItem = AdapterCookBookDetailsItem(dataList, requireActivity(), this)
        binding!!.rcyCookBookDetails.adapter = adapterCookBookDetailsItem
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (type == "remove") {
            removeRecipeDialog()
        } else if (type == "move") {
            moveRecipeDialog()
        } else if (type == "plan") {
            chooseDayDialog()

//            findNavController().navigate(R.id.planFragment)
        } else {
            findNavController().navigate(R.id.basketScreenFragment)

        }
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

        val btnNext = dialogChooseMealDay.findViewById<ImageView>(R.id.btnNext)
        // button event listener
        val tvBreakfast = dialogChooseMealDay.findViewById<TextView>(R.id.tvBreakfast)
        val tvLunch = dialogChooseMealDay.findViewById<TextView>(R.id.tvLunch)
        val tvDinner = dialogChooseMealDay.findViewById<TextView>(R.id.tvDinner)
        val tvSnacks = dialogChooseMealDay.findViewById<TextView>(R.id.tvSnacks)
        val tvTeatime = dialogChooseMealDay.findViewById<TextView>(R.id.tvTeatime)





        dialogChooseMealDay.show()
        updateWeekRange()
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

            dialogChooseMealDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }
    }

    private fun removeRecipeDialog() {
        val dialogRemoveRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogRemoveRecipe.setContentView(R.layout.alert_dialog_remove_recipe)
        dialogRemoveRecipe.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogRemoveRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogCancelBtn = dialogRemoveRecipe.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogRemoveBtn = dialogRemoveRecipe.findViewById<TextView>(R.id.tvDialogRemoveBtn)
        dialogRemoveRecipe.show()
        dialogRemoveRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogRemoveRecipe.dismiss()
        }

        tvDialogRemoveBtn.setOnClickListener {
            dialogRemoveRecipe.dismiss()
        }
    }

    private fun moveRecipeDialog() {
        val dialogMoveRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogMoveRecipe.setContentView(R.layout.alert_dialog_move_dialog)
        dialogMoveRecipe.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogMoveRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlMove = dialogMoveRecipe.findViewById<RelativeLayout>(R.id.rlMove)
        val rlSelectChristmas =
            dialogMoveRecipe.findViewById<RelativeLayout>(R.id.rlSelectChristmas)
        val relSelectedSnack = dialogMoveRecipe.findViewById<RelativeLayout>(R.id.relSelectedSnack)
        val tvChristmas = dialogMoveRecipe.findViewById<TextView>(R.id.tvChristmas)
        val rlSelectBirthday = dialogMoveRecipe.findViewById<RelativeLayout>(R.id.rlSelectBirthday)
        val rlSelectParty = dialogMoveRecipe.findViewById<RelativeLayout>(R.id.rlSelectParty)
        dialogMoveRecipe.show()
        dialogMoveRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlMove.setOnClickListener {
            dialogMoveRecipe.dismiss()
        }

        rlSelectChristmas.setOnClickListener {
            if (status == true) {
                status = false
                relSelectedSnack.visibility = View.VISIBLE
            } else {
                status = true

                relSelectedSnack.visibility = View.GONE
            }
        }

        rlSelectBirthday.setOnClickListener {
            tvChristmas.text = "Birthday"
            relSelectedSnack.visibility = View.GONE
            status = true
        }

        rlSelectParty.setOnClickListener {
            tvChristmas.text = "Party"
            relSelectedSnack.visibility = View.GONE
            status = true
        }


    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {
        if (position == 0) {
            val bundle=Bundle()
            bundle.putString("value","New")
            bundle.putString("uri","")
            findNavController().navigate(R.id.createCookBookFragment,bundle)
        } else if (position != 1) {
            findNavController().navigate(R.id.christmasCollectionFragment)
        }
    }


}
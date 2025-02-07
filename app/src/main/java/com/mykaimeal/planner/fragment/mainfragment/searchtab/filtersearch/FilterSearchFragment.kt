package com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterFilterSearchItem
import com.mykaimeal.planner.databinding.FragmentFilterSearchBinding
import com.mykaimeal.planner.model.DataModel

class FilterSearchFragment : Fragment(),OnItemClickListener {

    private var binding: FragmentFilterSearchBinding?=null
    private var adapterFilterSearchItem: AdapterFilterSearchItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFilterSearchBinding.inflate(inflater, container, false)

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

        binding!!.relBackFiltered.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.relApplyBtn.setOnClickListener{
            findNavController().navigate(R.id.searchedRecipeBreakfastFragment)
        }

        mealTypeModel()
        dietModel()
        cookTimeModel()
    }

    private fun mealTypeModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()
        val data8 = DataModel()
        val data9 = DataModel()

        data1.title = "Breakfast"
        data1.isOpen = false

        data2.title = "Lunch"
        data2.isOpen = false

        data3.title = "Dinner"
        data3.isOpen = false

        data4.title = "Appitizer"
        data4.isOpen = false

        data5.title = "Breakfast"
        data5.isOpen = false

        data6.title = "Lunch"
        data6.isOpen = false

        data7.title = "Dinner"
        data7.isOpen = false

        data8.title = "Appitizer"
        data8.isOpen = false

        data9.title = "Cocktails"
        data9.isOpen = false

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)
        dataList.add(data8)
        dataList.add(data9)

        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }

//        adjustSpanCount(gridLayoutManager)// Default: 2 items per row
        adapterFilterSearchItem = AdapterFilterSearchItem(dataList, requireActivity(),this)
        binding!!.rcyMealType.layoutManager = flexboxLayoutManager
        binding!!.rcyMealType.adapter = adapterFilterSearchItem
    }


    private fun dietModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()
        val data8 = DataModel()
        val data9 = DataModel()

        data1.title = "Breakfast"
        data1.isOpen = false

        data2.title = "Lunch"
        data2.isOpen = false

        data3.title = "Dinner"
        data3.isOpen = false

        data4.title = "Appitizer"
        data4.isOpen = false

        data5.title = "Breakfast"
        data5.isOpen = false

        data6.title = "Lunch"
        data6.isOpen = false

        data7.title = "Dinner"
        data7.isOpen = false

        data8.title = "Appitizer"
        data8.isOpen = false

        data9.title = "Cocktails"
        data9.isOpen = false


        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)
        dataList.add(data8)
        dataList.add(data9)

        val gridLayoutManager = GridLayoutManager(requireActivity(), 3) // Default: 2 items per row
        adapterFilterSearchItem = AdapterFilterSearchItem(dataList, requireActivity(),this)
        binding!!.rcyDiet.layoutManager = gridLayoutManager
        binding!!.rcyDiet.adapter = adapterFilterSearchItem
//        adjustSpanCount(gridLayoutManager)

    }
    private fun cookTimeModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()
        val data8 = DataModel()
        val data9 = DataModel()
        val data10 = DataModel()

        data1.title = "Breakfast"
        data1.isOpen = false

        data2.title = "Lunch"
        data2.isOpen = false

        data3.title = "Dinner"
        data3.isOpen = false

        data4.title = "Appitizer"
        data4.isOpen = false

        data5.title = "Breakfast"
        data5.isOpen = false

        data6.title = "Lunch"
        data6.isOpen = false

        data7.title = "Dinner"
        data7.isOpen = false

        data8.title = "Appitizer"
        data8.isOpen = false

        data9.title = "Cocktails"
        data9.isOpen = false

        data10.title = "More"
        data10.isOpen = true

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)
        dataList.add(data8)
        dataList.add(data9)
        dataList.add(data10)

        val gridLayoutManager = GridLayoutManager(requireActivity(), 3) // Default: 2 items per row
        adapterFilterSearchItem = AdapterFilterSearchItem(dataList, requireActivity(),this)
        binding!!.rcyCookTime.layoutManager = gridLayoutManager
        binding!!.rcyCookTime.adapter = adapterFilterSearchItem
//        adjustSpanCount(gridLayoutManager)

    }

//    private fun adjustSpanCount(gridLayoutManager: GridLayoutManager) {
//        // Get screen width in pixels
//        val screenWidth = resources.displayMetrics.widthPixels
//
//        // Set span count based on the screen width or any other condition
//        if (screenWidth > 120) {
//            gridLayoutManager.spanCount = 3
//            // For large screens (e.g., tablets or larger devices), use 4 items per row
//
//        } else if (screenWidth > 800) {
//            // For medium-sized screens, use 3 items per row
//            gridLayoutManager.spanCount = 4
//        } else {
//            // For small screens, use 2 items per row
//            gridLayoutManager.spanCount = 3
//        }
//    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

    }

}
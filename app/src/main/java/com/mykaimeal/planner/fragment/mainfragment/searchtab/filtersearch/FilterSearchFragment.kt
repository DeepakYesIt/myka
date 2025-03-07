package com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterFilterCookTimeItem
import com.mykaimeal.planner.adapter.AdapterFilterDietItem
import com.mykaimeal.planner.adapter.AdapterFilterMealItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentFilterSearchBinding
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.FilterSearchModel
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.FilterSearchModelData
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.viewmodel.FilterSearchViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterSearchFragment : Fragment(),OnItemClickListener {

    private var binding: FragmentFilterSearchBinding?=null
    private var adapterFilterMealItem: AdapterFilterMealItem? = null
    private var adapterFilterDietItem: AdapterFilterDietItem? = null
    private var adapterFilterCookBookItem: AdapterFilterCookTimeItem? = null
    private lateinit var filterSearchViewModel: FilterSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFilterSearchBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE
        filterSearchViewModel = ViewModelProvider(this)[FilterSearchViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()
        // This Api call when the screen in loaded
        launchApi()

        return binding!!.root
    }

    private fun launchApi() {
        if (BaseApplication.isOnline(requireActivity())) {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                filterSearchViewModel.getFilterList {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> handleSuccessResponse(it.data.toString())
                        is NetworkResult.Error -> showAlert(it.message, false)
                        else -> showAlert(it.message, false)
                    }
                }
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, FilterSearchModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                if (apiModel.data!=null ){
                    showDataInUi(apiModel.data)
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

    private fun showDataInUi(data: FilterSearchModelData) {
        try {
            if (data.mealType!=null && data.mealType.size>0){
                if (data.mealType.size>5){

                }else{
                    val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
                        flexDirection = FlexDirection.ROW
                        flexWrap = FlexWrap.WRAP
                        justifyContent = JustifyContent.FLEX_START
                    }
                    adapterFilterMealItem = AdapterFilterMealItem(data.mealType, requireActivity(),this)
                    binding!!.rcyMealType.layoutManager = flexboxLayoutManager
                    binding!!.rcyMealType.adapter = adapterFilterMealItem
                }
            }

            if (data.Diet!=null && data.Diet.size>0){
                if (data.Diet.size>5){

                }else{
                    val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
                        flexDirection = FlexDirection.ROW
                        flexWrap = FlexWrap.WRAP
                        justifyContent = JustifyContent.FLEX_START
                    }
                    adapterFilterDietItem = AdapterFilterDietItem(data.Diet, requireActivity(),this)
                    binding!!.rcyDiet.layoutManager = flexboxLayoutManager
                    binding!!.rcyDiet.adapter = adapterFilterDietItem
                }
            }
            if (data.cook_time!=null && data.cook_time.size>0){
                if (data.cook_time.size>5){

                }else{
                    val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
                        flexDirection = FlexDirection.ROW
                        flexWrap = FlexWrap.WRAP
                        justifyContent = JustifyContent.FLEX_START
                    }
                    //        adjustSpanCount(gridLayoutManager)// Default: 2 items per row
                    adapterFilterCookBookItem = AdapterFilterCookTimeItem(data.cook_time, requireActivity(),this)
                    binding!!.rcyCookTime.layoutManager = flexboxLayoutManager
                    binding!!.rcyCookTime.adapter = adapterFilterCookBookItem
                }
            }

        }catch (e:Exception){
            showAlert(e.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun initialize() {

        binding!!.relBackFiltered.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.relApplyBtn.setOnClickListener{
            findNavController().navigate(R.id.searchedRecipeBreakfastFragment)
        }
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
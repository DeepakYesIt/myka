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
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.CookTime
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.Diet
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.FilterSearchModel
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.FilterSearchModelData
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.model.MealType
import com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.viewmodel.FilterSearchViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterSearchFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentFilterSearchBinding? = null
    private val binding get() = _binding!!
    private var adapterFilterMealItem: AdapterFilterMealItem? = null
    private var adapterFilterDietItem: AdapterFilterDietItem? = null
    private var adapterFilterCookBookItem: AdapterFilterCookTimeItem? = null
    private lateinit var filterSearchViewModel: FilterSearchViewModel
    private var fullListMealType: MutableList<MealType>? = null
    private var originalFullList: MutableList<Diet>? = null
    private var fullListCookTime: MutableList<CookTime>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFilterSearchBinding.inflate(inflater, container, false)

        (activity as? MainActivity)?.binding?.apply {
            llIndicator.visibility = View.GONE
            llBottomNavigation.visibility = View.GONE
        }

        filterSearchViewModel = ViewModelProvider(this)[FilterSearchViewModel::class.java]

        backButton()

        initialize()

        // This Api call when the screen in loaded
        if (BaseApplication.isOnline(requireActivity())) {
            launchApi()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        return binding.root
    }

    private fun backButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
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
                if (apiModel.data != null) {
                    showDataInUi(apiModel.data)
                }
            } else {
                handleError(apiModel.code,apiModel.message)
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }


    private fun handleError(code: Int?, message: String?) {
        if (code == ErrorMessage.code) {
            showAlert(message, true)
        } else {
            showAlert(message, false)
        }
    }

    private fun showDataInUi(data: FilterSearchModelData) {
        try {
            if (data.mealType != null && data.mealType.size > 0) {
                fullListMealType = data.mealType
                val mealTypeList = data.mealType ?: return
                val mealTypeDisplayList = if (mealTypeList.size > 5) {
                    mealTypeList.take(5).toMutableList().apply {
                        add(MealType(id = -1, image = "", name = "More", selected = true))
                    }
                } else {
                    mealTypeList
                }

                val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexDirection = FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                    justifyContent = JustifyContent.FLEX_START
                }

                adapterFilterMealItem =
                    AdapterFilterMealItem(mealTypeDisplayList, requireActivity(), this)
                binding.rcyMealType.layoutManager = flexboxLayoutManager
                binding.rcyMealType.adapter = adapterFilterMealItem
            }

            if (data.Diet != null && data.Diet.size > 0) {
                originalFullList = data.Diet
                val dietList = data.Diet ?: return
                val dietDisplayList = if (dietList.size > 5) {
                    dietList.take(5).toMutableList().apply {
                        add(Diet(name = "More", selected = true))
                    }
                } else {
                    dietList
                }

                val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexDirection = FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                    justifyContent = JustifyContent.FLEX_START
                }

                adapterFilterDietItem = AdapterFilterDietItem(dietDisplayList, requireActivity(), this)
                binding.rcyDiet.layoutManager = flexboxLayoutManager
                binding.rcyDiet.adapter = adapterFilterDietItem
            }
            if (data.cook_time != null && data.cook_time.size > 0) {

                fullListCookTime = data.cook_time
                val cookTimeList = data.cook_time ?: return

                val displayList = if (cookTimeList.size > 5) {
                    cookTimeList.take(5).toMutableList().apply {
                        add(CookTime(name = "More", value = "", selected = true))
                    }
                } else {
                    cookTimeList
                }

                val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexDirection = FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                    justifyContent = JustifyContent.FLEX_START
                }

                adapterFilterCookBookItem = AdapterFilterCookTimeItem(displayList.toMutableList(), requireActivity(), this)
                binding.rcyCookTime.layoutManager = flexboxLayoutManager
                binding.rcyCookTime.adapter = adapterFilterCookBookItem

            }

        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun initialize() {

        binding.relBackFiltered.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.relApplyBtn.setOnClickListener {
          /*  val bundle = Bundle()
            bundle.putSerializable("recently", recentlyPostData as Serializable?)*/
            findNavController().navigate(R.id.searchedRecipeBreakfastFragment)
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (type == "MealType") {
            if (status == "More") {
                adapterFilterMealItem!!.updateList(fullListMealType)    // refresh adapter with full list
            }
        } else if (type == "Diet") {
            if (status == "More") {
                adapterFilterDietItem!!.updateList(originalFullList)    // refresh adapter with full list
            }
        } else {
            if (status == "More") {
                adapterFilterCookBookItem!!.updateList(fullListCookTime)    // refresh adapter with full list
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
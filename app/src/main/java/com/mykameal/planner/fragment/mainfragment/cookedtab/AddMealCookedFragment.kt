package com.mykameal.planner.fragment.mainfragment.cookedtab

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.SearchAdapterItem
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.commonworkutils.WeekDaysCalculator
import com.mykameal.planner.databinding.FragmentAddMealCookedBinding
import com.mykameal.planner.fragment.mainfragment.cookedtab.viewmodel.AddMealCookedViewModel
import com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.model.Recipe
import com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.model.SearchModel
import com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.model.SearchModelData
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMealCookedFragment : Fragment(),OnItemClickListener {
    private var binding:FragmentAddMealCookedBinding?=null
    private lateinit var addMealCookedViewModel: AddMealCookedViewModel
    private var searchAdapterItem:SearchAdapterItem?=null
    private var recipes: List<Recipe>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddMealCookedBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        addMealCookedViewModel = ViewModelProvider(this)[AddMealCookedViewModel::class.java]

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

        binding!!.relSearch.setOnClickListener{
            binding!!.cardViewSearchRecipe.visibility=View.VISIBLE
            if (BaseApplication.isOnline(requireActivity())) {
                searchRecipeApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
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

    private fun searchRecipeApi() {
        binding!!.relProgress.visibility=View.VISIBLE
        lifecycleScope.launch {
            addMealCookedViewModel.recipeSearchApi({
                binding!!.relProgress.visibility=View.GONE
               //                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val searchModel = gson.fromJson(it.data, SearchModel::class.java)
                        if (searchModel.code == 200 && searchModel.success) {
                            showDataInUi(searchModel.data)
                        } else {
                            if (searchModel.code == ErrorMessage.code) {
                                showAlertFunction(searchModel.message, true)
                            }else{
                                showAlertFunction(searchModel.message, false)
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
            },binding!!.etCookedDishes.text.toString().trim())
        }
    }

    private fun showDataInUi(searchModelData: SearchModelData) {
        if (searchModelData!=null){
            if (searchModelData.recipes!=null && searchModelData.recipes.size>0){
                recipes=searchModelData.recipes
                binding!!.rcySearchCooked.visibility=View.VISIBLE
                binding!!.tvNoData.visibility=View.GONE
                searchAdapterItem = SearchAdapterItem(searchModelData.recipes, requireActivity(),this)
                binding!!.rcySearchCooked.adapter = searchAdapterItem
            }else{
                binding!!.rcySearchCooked.visibility=View.GONE
                binding!!.tvNoData.visibility=View.VISIBLE
            }
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        binding!!.cardViewSearchRecipe.visibility=View.GONE

        binding!!.tvTitleName.text=recipes!![position!!].recipe.label.toString()

       /* recipes!![position!!].recipe.image*/

    }
}
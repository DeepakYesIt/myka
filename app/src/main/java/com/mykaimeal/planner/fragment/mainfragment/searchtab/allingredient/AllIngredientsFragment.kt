package com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterAllIngredientsItem
import com.mykaimeal.planner.adapter.AllIngredientsCategoryItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentAllIngredientsBinding
import com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient.model.AllIngredientsModel
import com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient.model.AllIngredientsModelData
import com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient.model.IngredientList
import com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient.viewmodel.AllIngredientsViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class AllIngredientsFragment : Fragment(),View.OnClickListener,OnItemClickListener {

    private var _binding: FragmentAllIngredientsBinding?=null
    private val binding get() = _binding!!
    private var adapterAllIngItem: AdapterAllIngredientsItem? = null
    private var adapterAllIngCategoryItem: AllIngredientsCategoryItem? = null
    private lateinit var allIngredientsModelData:AllIngredientsViewModel
    private val dataList = ArrayList<DataModel>()
    private var ingredients: MutableList<IngredientList>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentAllIngredientsBinding.inflate(layoutInflater, container, false)

        (activity as? MainActivity)?.binding?.let {
            it.llIndicator.visibility = View.VISIBLE
            it.llBottomNavigation.visibility = View.VISIBLE
        }

        allIngredientsModelData = ViewModelProvider(this)[AllIngredientsViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

/*
        allIngredientsModel()
*/

        return binding.root
    }

    private fun initialize() {

        /*binding.llFruits.setOnClickListener(this)
        binding.llVegetables.setOnClickListener(this)
        binding.llDairyEgg.setOnClickListener(this)
        binding.llBakery.setOnClickListener(this)*/
        binding.imageBackIcon.setOnClickListener(this)

        binding.etIngRecipes.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable(editable.toString())
            }
        })

        // This Api call when the screen in loaded
        if (BaseApplication.isOnline(requireActivity())){
            launchApi()
        }else{
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

    }

    private fun searchable(editText: String) {
        val filteredList: MutableList<IngredientList> = java.util.ArrayList<IngredientList>()
        for (item in ingredients!!) {
            if (item.name!!.toLowerCase().contains(editText.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.size > 0) {
            adapterAllIngItem!!.filterList(filteredList)
            binding.rcyAllIngredients.visibility = View.VISIBLE
        } else {
            binding.rcyAllIngredients.visibility = View.GONE
        }
    }

    private fun launchApi() {
        if (BaseApplication.isOnline(requireActivity())) {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                allIngredientsModelData.getAllIngredientsUrl({
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> handleSuccessResponse(it.data.toString())
                        is NetworkResult.Error -> showAlert(it.message, false)
                        else -> showAlert(it.message, false)
                    }
                },"","","")
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }


    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, AllIngredientsModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
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


    private fun showDataInUi(data: AllIngredientsModelData) {
        try {
            if (data.categories!=null && data.categories.size>0){
                adapterAllIngCategoryItem = AllIngredientsCategoryItem(data.categories, requireActivity(),this)
                binding.rcyIngredientCategory.adapter = adapterAllIngCategoryItem
            }

            if (data.ingredients!=null && data.ingredients.size>0){
                ingredients=data.ingredients
                adapterAllIngItem = AdapterAllIngredientsItem(data.ingredients, requireActivity())
                binding.rcyAllIngredients.adapter = adapterAllIngItem
            }



        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

/*    private fun allIngredientsModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()
        val data8 = DataModel()

        data1.title = "Apple"
        data1.isOpen = false
        data1.type = "AllIngredients"
        data1.image = R.drawable.apple_ing_image

        data2.title = "Avacado"
        data2.isOpen = false
        data2.type = "AllIngredients"
        data2.image = R.drawable.avacado_ing_image

        data3.title = "Oranges"
        data3.isOpen = false
        data3.type = "AllIngredients"
        data3.image = R.drawable.orange_ing_image

        data4.title = "Grapes"
        data4.isOpen = false
        data4.type = "AllIngredients"
        data4.image = R.drawable.grapes_ing_image

        data5.title = "Mango"
        data5.isOpen = false
        data5.type = "AllIngredients"
        data5.image = R.drawable.banana_ing_image

        data6.title = "Guava"
        data6.isOpen = false
        data6.type = "AllIngredients"
        data6.image = R.drawable.guava_ing_image

        data7.title = "Watermelon"
        data7.isOpen = false
        data7.type = "AllIngredients"
        data7.image = R.drawable.watermelon_ing_image

        data8.title = "Apricot"
        data8.isOpen = false
        data8.type = "AllIngredients"
        data8.image = R.drawable.apricot_ing_image

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)
        dataList.add(data8)

        adapterAllIngItem = AdapterAllIngredientsItem(dataList, requireActivity())
        binding.rcyAllIngredients.adapter = adapterAllIngItem
    }*/

    override fun onClick(item: View?) {
        when(item!!.id){

            R.id.imageBackIcon->{
                findNavController().navigateUp()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        TODO("Not yet implemented")
    }


}
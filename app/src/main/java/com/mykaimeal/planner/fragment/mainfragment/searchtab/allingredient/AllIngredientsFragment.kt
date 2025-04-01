package com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterAllIngredientsItem
import com.mykaimeal.planner.databinding.FragmentAllIngredientsBinding
import com.mykaimeal.planner.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class AllIngredientsFragment : Fragment(),View.OnClickListener {

    private var _binding: FragmentAllIngredientsBinding?=null
    private val binding get() = _binding!!
    private var adapterAllIngItem: AdapterAllIngredientsItem? = null
    private val dataList = ArrayList<DataModel>()

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


        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        allIngredientsModel()

        return binding.root
    }

    private fun initialize() {

        binding.llFruits.setOnClickListener(this)
        binding.llVegetables.setOnClickListener(this)
        binding.llDairyEgg.setOnClickListener(this)
        binding.llBakery.setOnClickListener(this)
        binding.imageBackIcon.setOnClickListener(this)

        binding.etIngRecipes.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                searchable(editable.toString())
            }
        })
    }

    private fun searchable(editText: String) {
        val filteredList: MutableList<DataModel> = java.util.ArrayList<DataModel>()
        for (item in dataList) {
            if (item.title.toLowerCase().contains(editText.lowercase(Locale.getDefault()))) {
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

    private fun allIngredientsModel() {
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
    }

    override fun onClick(item: View?) {
        when(item!!.id){

            R.id.imageBackIcon->{
                findNavController().navigateUp()
            }

            R.id.llFruits->{
                    binding.textFruits.setBackgroundResource(R.drawable.select_bg)
                    binding.textVegetables.setBackgroundResource(R.drawable.unselect_bg)
                    binding.textDairyEgg.setBackgroundResource(R.drawable.unselect_bg)
                    binding.textBakery.setBackgroundResource(R.drawable.unselect_bg)

                    binding.textFruits.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.textVegetables.setTextColor(Color.parseColor("#3C4541"))
                    binding.textDairyEgg.setTextColor(Color.parseColor("#3C4541"))
                    binding.textBakery.setTextColor(Color.parseColor("#3C4541"))

            }

            R.id.llVegetables->{
                binding.textFruits.setBackgroundResource(R.drawable.unselect_bg)
                binding.textVegetables.setBackgroundResource(R.drawable.select_bg)
                binding.textDairyEgg.setBackgroundResource(R.drawable.unselect_bg)
                binding.textBakery.setBackgroundResource(R.drawable.unselect_bg)

                binding.textFruits.setTextColor(Color.parseColor("#3C4541"))
                binding.textVegetables.setTextColor(Color.parseColor("#FFFFFF"))
                binding.textDairyEgg.setTextColor(Color.parseColor("#3C4541"))
                binding.textBakery.setTextColor(Color.parseColor("#3C4541"))

            }

            R.id.llDairyEgg->{
                binding.textFruits.setBackgroundResource(R.drawable.unselect_bg)
                binding.textVegetables.setBackgroundResource(R.drawable.unselect_bg)
                binding.textDairyEgg.setBackgroundResource(R.drawable.select_bg)
                binding.textBakery.setBackgroundResource(R.drawable.unselect_bg)

                binding.textFruits.setTextColor(Color.parseColor("#3C4541"))
                binding.textVegetables.setTextColor(Color.parseColor("#3C4541"))
                binding.textDairyEgg.setTextColor(Color.parseColor("#FFFFFF"))
                binding.textBakery.setTextColor(Color.parseColor("#3C4541"))
            }

            R.id.llBakery->{
                binding.textFruits.setBackgroundResource(R.drawable.unselect_bg)
                binding.textVegetables.setBackgroundResource(R.drawable.unselect_bg)
                binding.textDairyEgg.setBackgroundResource(R.drawable.unselect_bg)
                binding.textBakery.setBackgroundResource(R.drawable.select_bg)

                binding.textFruits.setTextColor(Color.parseColor("#3C4541"))
                binding.textVegetables.setTextColor(Color.parseColor("#3C4541"))
                binding.textDairyEgg.setTextColor(Color.parseColor("#3C4541"))
                binding.textBakery.setTextColor(Color.parseColor("#FFFFFF"))

            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    

}
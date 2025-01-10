package com.mykameal.planner.fragment.mainfragment.commonscreen

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
import androidx.navigation.fragment.findNavController
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.adapter.YourRecipeAdapter
import com.mykameal.planner.databinding.FragmentBasketYourRecipeBinding
import com.mykameal.planner.model.YourRecipeItem

class BasketYourRecipeFragment : Fragment(),OnItemClickListener {

    private lateinit var binding:FragmentBasketYourRecipeBinding
    private lateinit var adapterRecipe1: YourRecipeAdapter
    private lateinit var adapterRecipe2: YourRecipeAdapter
    private lateinit var adapterRecipe3: YourRecipeAdapter

    private val listRecipe1 = mutableListOf<YourRecipeItem>()
    private val listRecipe2 = mutableListOf<YourRecipeItem>()
    private val listRecipe3 = mutableListOf<YourRecipeItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding=FragmentBasketYourRecipeBinding.inflate(layoutInflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding.root
    }

    private fun initialize() {

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        adapterInitialize()
    }

    private fun adapterInitialize(){

        adapterRecipe1 = YourRecipeAdapter(requireContext(), itemList = listRecipe1,this)
        adapterRecipe2 = YourRecipeAdapter(requireContext(), itemList = listRecipe2,this)
        adapterRecipe3 = YourRecipeAdapter(requireContext(), itemList = listRecipe3,this)

        binding.rcvYourRecipes1.adapter = adapterRecipe1
        binding.rcvYourRecipes2.adapter = adapterRecipe2
        binding.rcvYourRecipes3.adapter = adapterRecipe3

        val initialList1 = List(6) { index ->
            if (index % 2 == 0) {
                YourRecipeItem( "Pot Lentil",R.drawable.ic_food_image,  "Serves 2")
            } else {
                YourRecipeItem("Pot Rice",R.drawable.ic_food_image,  "Serves 2")
            }
        }


        adapterRecipe1.addItems(initialList1)
        adapterRecipe2.addItems(initialList1)
        adapterRecipe3.addItems(initialList1)


    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

        if (status=="2"){
            addSuperMarketDialog()
        }
    }

    private fun addSuperMarketDialog() {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_remove_recipe_basket)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val tvDialogCancelBtn = dialogAddItem.findViewById<TextView>(R.id.tvDialogCancelBtn)
        val tvDialogRemoveBtn = dialogAddItem.findViewById<TextView>(R.id.tvDialogRemoveBtn)
        dialogAddItem.show()
        dialogAddItem.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogCancelBtn.setOnClickListener {
            dialogAddItem.dismiss()
        }

        tvDialogRemoveBtn.setOnClickListener {
            dialogAddItem.dismiss()
        }
    }


}
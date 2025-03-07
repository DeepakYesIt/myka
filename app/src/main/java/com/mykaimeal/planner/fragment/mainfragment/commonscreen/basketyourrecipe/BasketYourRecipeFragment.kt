package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketyourrecipe

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
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemSelectListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.YourRecipeAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.databinding.FragmentBasketYourRecipeBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketyourrecipe.model.BasketYourRecipeModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketyourrecipe.model.BasketYourRecipeModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketyourrecipe.viewmodel.BasketYourRecipeViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasketYourRecipeFragment : Fragment(),OnItemClickListener,OnItemSelectListener {

    private lateinit var binding: FragmentBasketYourRecipeBinding
    private lateinit var basketYourRecipeViewModel: BasketYourRecipeViewModel
    private var yourRecipeAdapter: YourRecipeAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding=FragmentBasketYourRecipeBinding.inflate(layoutInflater, container, false)

        basketYourRecipeViewModel = ViewModelProvider(requireActivity())[BasketYourRecipeViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding.root
    }

    private fun initialize() {

        getYourRecipeList()

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        adapterInitialize()
    }

    private fun getYourRecipeList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketYourRecipeViewModel.getYourRecipeUrl {
                BaseApplication.dismissMe()
                handleApiyourRecipeResponse(it)
            }
        }
    }

    private fun handleApiyourRecipeResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessYourRecipeResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessYourRecipeResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, BasketYourRecipeModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success == true) {
                if (apiModel.data!= null) {
                    showDataInUI(apiModel.data)
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

    private fun showDataInUI(data: BasketYourRecipeModelData?) {

        if (data!!.Breakfast!=null && data.Dinner.size>0){
            yourRecipeAdapter = YourRecipeAdapter(data.Breakfast,requireActivity(), this,"Breakfast")
            binding.rcyBreakfast.adapter = yourRecipeAdapter
        }


        if (data!!.Lunch!=null && data.Lunch.size>0){
            yourRecipeAdapter = YourRecipeAdapter(data.Lunch,requireActivity(), this,"Lunch")
            binding.rcyBreakfast.adapter = yourRecipeAdapter
        }

        if (data!!.Dinner!=null && data.Dinner.size>0){
            yourRecipeAdapter = YourRecipeAdapter(data.Dinner,requireActivity(), this,"Dinner")
            binding.rcyBreakfast.adapter = yourRecipeAdapter
        }

        if (data!!.Snack!=null && data.Snack.size>0){
            yourRecipeAdapter = YourRecipeAdapter(data.Snack,requireActivity(), this,"Snacks")
            binding.rcyBreakfast.adapter = yourRecipeAdapter
        }

        if (data!!.Teatime!=null && data.Teatime.size>0){
            yourRecipeAdapter = YourRecipeAdapter(data.Teatime,requireActivity(), this,"Teatime")
            binding.rcyBreakfast.adapter = yourRecipeAdapter
        }

    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun adapterInitialize(){

       /* binding.rcvYourRecipes1.adapter = adapterRecipe1
        binding.rcvYourRecipes2.adapter = adapterRecipe2
        binding.rcvYourRecipes3.adapter = adapterRecipe3*/

      /*  val initialList1 = List(6) { index ->
            if (index % 2 == 0) {
                YourRecipeItem( "Pot Lentil", R.drawable.ic_food_image,  "Serves 2")
            } else {
                YourRecipeItem("Pot Rice",R.drawable.ic_food_image,  "Serves 2")
            }
        }*/


/*        adapterRecipe1.addItems(initialList1)
        adapterRecipe2.addItems(initialList1)
        adapterRecipe3.addItems(initialList1)*/


    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

        if (status=="2"){
            removeRecipeDialog()
        }
    }

    private fun removeRecipeDialog() {
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

    override fun itemSelect(position: Int?, status: String?, type: String?) {

    }


}
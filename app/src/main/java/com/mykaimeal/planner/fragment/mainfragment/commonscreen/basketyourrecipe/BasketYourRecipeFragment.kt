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
import com.mykaimeal.planner.fragment.mainfragment.cookedtab.cookedfragment.model.CookedTabModel
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

    }

    private fun getYourRecipeList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            basketYourRecipeViewModel.getYourRecipeUrl {
                BaseApplication.dismissMe()
                handleApiYourRecipeResponse(it)
            }
        }
    }

    private fun handleApiYourRecipeResponse(result: NetworkResult<String>) {
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

        if (data!!.Breakfast!=null && data.Breakfast.size>0){
            binding.rcyBreakfast.visibility=View.VISIBLE
            binding.tvBreakFast.visibility=View.VISIBLE
            yourRecipeAdapter = YourRecipeAdapter(data.Breakfast,requireActivity(), this,"Breakfast")
            binding.rcyBreakfast.adapter = yourRecipeAdapter
        }else{
            binding.rcyBreakfast.visibility=View.GONE
            binding.tvBreakFast.visibility=View.GONE
        }

        if (data!!.Lunch!=null && data.Lunch.size>0){
            binding.rcyLunch.visibility=View.VISIBLE
            binding.tvLunch.visibility=View.VISIBLE
            yourRecipeAdapter = YourRecipeAdapter(data.Lunch,requireActivity(), this,"Lunch")
            binding.rcyLunch.adapter = yourRecipeAdapter
        }else{
            binding.rcyLunch.visibility=View.GONE
            binding.tvLunch.visibility=View.GONE
        }

        if (data!!.Dinner!=null && data.Dinner.size>0){
            binding.rcyDinner.visibility=View.VISIBLE
            binding.tvDinner.visibility=View.VISIBLE
            yourRecipeAdapter = YourRecipeAdapter(data.Dinner,requireActivity(), this,"Dinner")
            binding.rcyDinner.adapter = yourRecipeAdapter
        }else{
            binding.rcyDinner.visibility=View.GONE
            binding.tvDinner.visibility=View.GONE
        }

        if (data!!.Snack!=null && data.Snack.size>0){
            binding.rcySnacks.visibility=View.VISIBLE
            binding.tvSnacks.visibility=View.VISIBLE
            yourRecipeAdapter = YourRecipeAdapter(data.Snack,requireActivity(), this,"Snacks")
            binding.rcySnacks.adapter = yourRecipeAdapter
        }else{
            binding.rcySnacks.visibility=View.GONE
            binding.tvSnacks.visibility=View.GONE
        }

        if (data!!.Teatime!=null && data.Teatime.size>0){
            binding.rcyTeaTimes.visibility=View.VISIBLE
            binding.tvTeaTime.visibility=View.VISIBLE
            yourRecipeAdapter = YourRecipeAdapter(data.Teatime,requireActivity(), this,"Teatime")
            binding.rcyTeaTimes.adapter = yourRecipeAdapter
        }else{
            binding.rcyTeaTimes.visibility=View.GONE
            binding.tvTeaTime.visibility=View.GONE
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

    }

    private fun removeRecipeBasketDialog(recipeId: String?, position: Int?, type: String) {
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
            if (BaseApplication.isOnline(requireActivity())) {
                removeBasketRecipeApi(recipeId.toString(), dialogAddItem,position,type)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
//            dialogAddItem.dismiss()
        }
    }

    private fun removeBasketRecipeApi(
        recipeId: String,
        dialogRemoveDay: Dialog,
        position: Int?,
        type: String
    ) {
        BaseApplication.showMe(requireActivity())
        lifecycleScope.launch {
            basketYourRecipeViewModel.removeBasketUrlApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        val gson = Gson()
                        val cookedModel = gson.fromJson(it.data, CookedTabModel::class.java)
                        if (cookedModel.code == 200 && cookedModel.success) {

                            when (type) {
                                "Breakfast" -> yourRecipeAdapter?.removeItem(position!!)
                                "Lunch" -> yourRecipeAdapter?.removeItem(position!!)
                                "Dinner" -> yourRecipeAdapter?.removeItem(position!!)
                                "Snacks" -> yourRecipeAdapter?.removeItem(position!!)
                                "Teatime" -> yourRecipeAdapter?.removeItem(position!!)
                            }
                            dialogRemoveDay.dismiss()
                        } else {
                            if (cookedModel.code == ErrorMessage.code) {
                                showAlert(cookedModel.message, true)
                            } else {
                                showAlert(cookedModel.message, false)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlert(it.message, false)
                    }

                    else -> {
                        showAlert(it.message, false)
                    }
                }
            }, recipeId)
        }
    }


    override fun itemSelect(position: Int?, recipeId: String?, type: String?) {
            removeRecipeBasketDialog(recipeId,position, type.toString())
    }

}
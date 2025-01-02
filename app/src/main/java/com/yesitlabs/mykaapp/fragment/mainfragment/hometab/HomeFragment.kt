package com.yesitlabs.mykaapp.fragment.mainfragment.hometab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.OnItemLongClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterSuperMarket
import com.yesitlabs.mykaapp.adapter.IngredientsDinnerAdapter
import com.yesitlabs.mykaapp.databinding.FragmentHomeBinding
import com.yesitlabs.mykaapp.databinding.FragmentLoginBinding
import com.yesitlabs.mykaapp.model.DataModel

class HomeFragment : Fragment(), View.OnClickListener, OnItemClickListener, OnItemLongClickListener {

    private var binding: FragmentHomeBinding? = null
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var ingredientDinnerAdapter: IngredientsDinnerAdapter? = null
    private var adapterSuperMarket: AdapterSuperMarket? = null
    private var statuses:String?=""
    private var checkStatus:Boolean?=false
    private var recySuperMarket:RecyclerView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        (activity as MainActivity?)?.changeBottom("home")

        homeSchDinnerModel()
        addSuperMarketDialog()

        initialize()

        return binding!!.root
    }

    private fun addSuperMarketDialog() {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_super_market)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        recySuperMarket = dialogAddItem.findViewById<RecyclerView>(R.id.recySuperMarket)
        val rlDoneBtn = dialogAddItem.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        dialogAddItem.show()
        dialogAddItem.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        superMarketModel()

        rlDoneBtn.setOnClickListener {
            dialogAddItem.dismiss()
        }
    }

    private fun superMarketModel() {
        val dataList1: MutableList<DataModel> = mutableListOf()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()

        data1.title = "Tesco"
        data1.isOpen = false
        data1.type = "SuperMarket"
        data1.price = "25"
        data1.image = R.drawable.super_market_tesco_image

        data2.title = "Coop"
        data2.isOpen = false
        data2.price = "28"
        data2.image = R.drawable.super_market_coop_image

        data3.title = "Iceland"
        data3.isOpen = false
        data3.price = "30"
        data3.image = R.drawable.super_market_iceland_image

        data4.title = "Albertsons"
        data4.isOpen = false
        data4.price = "32"
        data4.image = R.drawable.super_market_albertsons

        data5.title = "Aldi"
        data5.isOpen = false
        data5.price = "35"
        data5.image = R.drawable.super_market_aldi_image

        data6.title = "Costco"
        data6.isOpen = false
        data6.price = "35"
        data6.image = R.drawable.super_market_costco_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)
        dataList1.add(data4)
        dataList1.add(data5)
        dataList1.add(data6)

        adapterSuperMarket = AdapterSuperMarket(dataList1, requireActivity(), this)
        recySuperMarket!!.adapter = adapterSuperMarket
    }

    private fun homeSchDinnerModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Lasagne"
        data1.isOpen = false
        data1.type = "FullCookSchDinner"
        data1.image = R.drawable.dinner_lasagne_images

        data2.title = "stawberry"
        data2.isOpen = false
        data2.type = "FullCookSchDinner"
        data2.image = R.drawable.dinner_grilled_chicken_legs_images

        data3.title = "Juices"
        data3.isOpen = false
        data3.type = "FullCookSchDinner"
        data3.image = R.drawable.chicken_skewers_images

        data4.title = "Lasagne"
        data4.isOpen = false
        data4.type = "FullCookSchDinner"
        data4.image = R.drawable.dinner_lasagne_images

        data5.title = "stawberry"
        data5.isOpen = false
        data5.type = "FullCookSchDinner"
        data5.image = R.drawable.dinner_grilled_chicken_legs_images

        dataList3.add(data1)
        dataList3.add(data2)
        dataList3.add(data3)
        dataList3.add(data4)
        dataList3.add(data5)

        ingredientDinnerAdapter = IngredientsDinnerAdapter(dataList3, requireActivity(), this, this)
        binding!!.rcyRecipesCooked.adapter = ingredientDinnerAdapter
    }

    private fun initialize() {
        binding!!.rlSeeAllBtn.setOnClickListener(this)
        binding!!.textSeeAll.setOnClickListener(this)

        binding!!.imageCookedMeals.setOnClickListener(this)
        binding!!.imgFreeTrial.setOnClickListener(this)
        binding!!.imgBasketIcon.setOnClickListener(this)

        binding!!.imageProfile.setOnClickListener(this)

        binding!!.rlPlanAMealBtn.setOnClickListener(this)
        binding!!.imgHearRedIcons.setOnClickListener(this)
        binding!!.imageRecipeSeeAll.setOnClickListener(this)
        binding!!.relMonthlySavings.setOnClickListener(this)
        binding!!.imageCheckSav.setOnClickListener(this)

    }

    override fun onClick(item: View?) {
        when (item!!.id) {

            R.id.textSeeAll->{
                findNavController().navigate(R.id.fullCookedScheduleFragment)
            }

            R.id.rlSeeAllBtn -> {
                findNavController().navigate(R.id.cookedFragment)
//                findNavController().navigate(R.id.cookedFragment)
            }

            R.id.relMonthlySavings->{
                binding!!.imagePlanMeal.visibility=View.GONE
                binding!!.imageCheckSav.visibility=View.VISIBLE

            }

            R.id.imageCheckSav->{
                findNavController().navigate(R.id.statisticsGraphFragment)
            }

            R.id.imageCookedMeals->{
                binding!!.rlSeeAllBtn.visibility=View.VISIBLE
            }

            R.id.imgBasketIcon->{
                findNavController().navigate(R.id.basketScreenFragment)
            }

            R.id.imgHearRedIcons->{
                findNavController().navigate(R.id.cookBookFragment)
            }

            R.id.imageProfile->{
                findNavController().navigate(R.id.settingProfileFragment)
            }

            R.id.rlPlanAMealBtn->{
                findNavController().navigate(R.id.planFragment)
            }

            R.id.imgFreeTrial->{
                findNavController().navigate(R.id.homeSubscriptionFragment)
            }

            R.id.imageRecipeSeeAll->{
                binding!!.tvHomeDesc.text=getString(R.string.sept_meal_planner)
                binding!!.relPlanMeal.visibility=View.GONE
                binding!!.llRecipesCooked.visibility=View.VISIBLE
                binding!!.tvHomeDesc.visibility=View.GONE
                binding!!.tvHomeSeptDate.visibility=View.VISIBLE
            }
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        if (type == "heart") {
            addRecipeDialog(position, type)
        } else if (type == "minus") {
            if (status == "1") {
                removeDayDialog(position, type)
            }
        } else if (type=="missingIng") {
            findNavController().navigate(R.id.missingIngredientsFragment)
        }else{
            findNavController().navigate(R.id.recipeDetailsFragment)
        }
    }

    private fun addRecipeDialog(position: Int?, type: String) {
        val dialogAddRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogAddRecipe.setContentView(R.layout.alert_dialog_add_recipe)
        dialogAddRecipe.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogAddRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogAddRecipe.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val relCreateNewCookBook = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relCreateNewCookBook)
        val relFavourites = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relFavourites)
        val imgCheckBoxOrange = dialogAddRecipe.findViewById<ImageView>(R.id.imgCheckBoxOrange)

        dialogAddRecipe.show()
        dialogAddRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        relCreateNewCookBook.setOnClickListener{
            statuses="newCook"
            relCreateNewCookBook.setBackgroundResource(R.drawable.light_green_rectangular_bg)
            imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
            relFavourites.setBackgroundResource(0)
            checkStatus=false
        }

        imgCheckBoxOrange.setOnClickListener{
            if (checkStatus==true){
                statuses=""
                imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
                relFavourites.setBackgroundResource(0)
                relCreateNewCookBook.setBackgroundResource(0)
                relCreateNewCookBook.background=null
                checkStatus=false
            }else{
                relFavourites.setBackgroundResource(R.drawable.light_green_rectangular_bg)
                relCreateNewCookBook.setBackgroundResource(0)
                statuses="favourites"
                imgCheckBoxOrange.setImageResource(R.drawable.orange_checkbox_images)
                checkStatus=true
            }
        }


        rlDoneBtn.setOnClickListener{
            if (statuses==""){
                Toast.makeText(requireActivity(),"Please select atleast one of them", Toast.LENGTH_LONG).show()
            }else if (statuses=="favourites"){
                dialogAddRecipe.dismiss()
            }else{
                dialogAddRecipe.dismiss()
                val bundle=Bundle()
                bundle.putString("value","New")
                findNavController().navigate(R.id.createCookBookFragment,bundle)
            }
        }

    }

    private fun removeDayDialog(position: Int?, type: String?) {
        val dialogRemoveDay: Dialog = context?.let { Dialog(it) }!!
        dialogRemoveDay.setContentView(R.layout.alert_dialog_remove_day)
        dialogRemoveDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogRemoveDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogNoBtn = dialogRemoveDay.findViewById<TextView>(R.id.tvDialogNoBtn)
        val tvDialogYesBtn = dialogRemoveDay.findViewById<TextView>(R.id.tvDialogYesBtn)
        dialogRemoveDay.show()
        dialogRemoveDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogNoBtn.setOnClickListener {
            dialogRemoveDay.dismiss()
        }

        tvDialogYesBtn.setOnClickListener {
            if (type == "FullCookSchBreakFast") {
                dataList3.removeAt(position!!)
            } else if (type == "FullCookSchLunch") {
                dataList3.removeAt(position!!)
            } else {
                dataList3.removeAt(position!!)
            }
            /*
                        chooseDayDialog()
            */
            dialogRemoveDay.dismiss()
        }
    }

    override fun itemLongClick(position: Int?, status: String?, type: String?) {

    }
}
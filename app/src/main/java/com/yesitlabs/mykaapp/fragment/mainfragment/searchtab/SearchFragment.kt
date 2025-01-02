package com.yesitlabs.mykaapp.fragment.mainfragment.searchtab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.SearchMealAdapter
import com.yesitlabs.mykaapp.adapter.SearchRecipeAdapter
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.databinding.FragmentSearchBinding
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel

class SearchFragment : Fragment(),View.OnClickListener {

    private var binding: FragmentSearchBinding? = null
    private var searchRecipeAdapter: SearchRecipeAdapter? = null
    private var searchMealAdapter: SearchMealAdapter? = null
    private var status:String?="RecipeSearch"
    private lateinit var commonWorkUtils: CommonWorkUtils


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        (activity as MainActivity?)?.changeBottom("search")
        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        commonWorkUtils = CommonWorkUtils(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        searchRecipeDialog()

        searchRecipeModel()
        searchMealModel()
        searchPopCatModel()

        initialize()

        return binding!!.root
    }

    private fun initialize() {
        binding!!.relViewAll.setOnClickListener(this)
        binding!!.imgHeartRed.setOnClickListener(this)
        binding!!.imageProfile.setOnClickListener(this)
        binding!!.imgBasketIcon.setOnClickListener(this)
        binding!!.imgFilterIcon.setOnClickListener(this)
    }

    private fun addRecipeFromWeb() {
        val dialogWeb: Dialog = requireActivity().let { Dialog(it) }
        dialogWeb.setContentView(R.layout.alert_dialog_add_recipe_form_web)
        dialogWeb.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogWeb.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val etPasteURl = dialogWeb.findViewById<EditText>(R.id.etPasteURl)
        val rlSearchRecipe = dialogWeb.findViewById<RelativeLayout>(R.id.rlSearchRecipe)
        dialogWeb.show()
        dialogWeb.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlSearchRecipe.setOnClickListener {
            if (etPasteURl.text.toString().isEmpty()){
                commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.pasteUrl, false)
            }else{
                dialogWeb.dismiss()
            }
        }
    }

    private fun searchRecipeDialog() {
        val dialogSearchDialog: Dialog = context?.let { Dialog(it) }!!
        dialogSearchDialog.setContentView(R.layout.alert_dialog_search_recipe)
        dialogSearchDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogSearchDialog.setCancelable(false)
        dialogSearchDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val relRecipeSearch = dialogSearchDialog.findViewById<RelativeLayout>(R.id.relRecipeSearch)
        val relFavouritesRecipes = dialogSearchDialog.findViewById<RelativeLayout>(R.id.relFavouritesRecipes)
        val relFromWeb = dialogSearchDialog.findViewById<RelativeLayout>(R.id.relFromWeb)
        val relAddYourOwnRecipe = dialogSearchDialog.findViewById<RelativeLayout>(R.id.relAddYourOwnRecipe)
        val relTakingAPicture = dialogSearchDialog.findViewById<RelativeLayout>(R.id.relTakingAPicture)
        val rlSearch = dialogSearchDialog.findViewById<RelativeLayout>(R.id.rlSearch)

        val imageRecipeSearch = dialogSearchDialog.findViewById<ImageView>(R.id.imageRecipeSearch)
        val imageFavouritesRecipes = dialogSearchDialog.findViewById<ImageView>(R.id.imageFavouritesRecipes)
        val imageFromWeb = dialogSearchDialog.findViewById<ImageView>(R.id.imageFromWeb)
        val imageAddYourOwnRecipe = dialogSearchDialog.findViewById<ImageView>(R.id.imageAddYourOwnRecipe)
        val imageTakingAPicture = dialogSearchDialog.findViewById<ImageView>(R.id.imageTakingAPicture)
        val imgCrossSearch = dialogSearchDialog.findViewById<ImageView>(R.id.imgCrossSearch)
        dialogSearchDialog.show()
        dialogSearchDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlSearch.setOnClickListener {
            if (status=="RecipeSearch"){

            }else if (status=="FavouritesRecipes"){
                findNavController().navigate(R.id.cookBookFragment)
            }else if (status=="Web"){
                addRecipeFromWeb()
            }else if (status=="AddRecipe"){
                findNavController().navigate(R.id.createRecipeFragment)
            }else{

            }
            dialogSearchDialog.dismiss()
        }

        imgCrossSearch.setOnClickListener{
            dialogSearchDialog.dismiss()
        }

        relRecipeSearch.setOnClickListener{
            status="RecipeSearch"
            relRecipeSearch.setBackgroundResource(R.drawable.orange_box_bg)
            relFavouritesRecipes.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFromWeb.setBackgroundResource(R.drawable.gray_box_border_bg)
            relAddYourOwnRecipe.setBackgroundResource(R.drawable.gray_box_border_bg)
            relTakingAPicture.setBackgroundResource(R.drawable.gray_box_border_bg)

            imageRecipeSearch.visibility=View.VISIBLE
            imageFavouritesRecipes.visibility=View.GONE
            imageFromWeb.visibility=View.GONE
            imageAddYourOwnRecipe.visibility=View.GONE
            imageTakingAPicture.visibility=View.GONE
        }
        relFavouritesRecipes.setOnClickListener{
            status="FavouritesRecipes"
            relRecipeSearch.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFavouritesRecipes.setBackgroundResource(R.drawable.orange_box_bg)
            relFromWeb.setBackgroundResource(R.drawable.gray_box_border_bg)
            relAddYourOwnRecipe.setBackgroundResource(R.drawable.gray_box_border_bg)
            relTakingAPicture.setBackgroundResource(R.drawable.gray_box_border_bg)

            imageRecipeSearch.visibility=View.GONE
            imageFavouritesRecipes.visibility=View.VISIBLE
            imageFromWeb.visibility=View.GONE
            imageAddYourOwnRecipe.visibility=View.GONE
            imageTakingAPicture.visibility=View.GONE
        }
        relFromWeb.setOnClickListener{
            status="Web"
            relRecipeSearch.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFavouritesRecipes.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFromWeb.setBackgroundResource(R.drawable.orange_box_bg)
            relAddYourOwnRecipe.setBackgroundResource(R.drawable.gray_box_border_bg)
            relTakingAPicture.setBackgroundResource(R.drawable.gray_box_border_bg)

            imageRecipeSearch.visibility=View.GONE
            imageFavouritesRecipes.visibility=View.GONE
            imageFromWeb.visibility=View.VISIBLE
            imageAddYourOwnRecipe.visibility=View.GONE
            imageTakingAPicture.visibility=View.GONE
        }
        relAddYourOwnRecipe.setOnClickListener{
            status="AddRecipe"
            relRecipeSearch.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFavouritesRecipes.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFromWeb.setBackgroundResource(R.drawable.gray_box_border_bg)
            relAddYourOwnRecipe.setBackgroundResource(R.drawable.orange_box_bg)
            relTakingAPicture.setBackgroundResource(R.drawable.gray_box_border_bg)

            imageRecipeSearch.visibility=View.GONE
            imageFavouritesRecipes.visibility=View.GONE
            imageFromWeb.visibility=View.GONE
            imageAddYourOwnRecipe.visibility=View.VISIBLE
            imageTakingAPicture.visibility=View.GONE
        }
        relTakingAPicture.setOnClickListener{
            status="TakingPicture"
            relRecipeSearch.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFavouritesRecipes.setBackgroundResource(R.drawable.gray_box_border_bg)
            relFromWeb.setBackgroundResource(R.drawable.gray_box_border_bg)
            relAddYourOwnRecipe.setBackgroundResource(R.drawable.gray_box_border_bg)
            relTakingAPicture.setBackgroundResource(R.drawable.orange_box_bg)

            imageRecipeSearch.visibility=View.GONE
            imageFavouritesRecipes.visibility=View.GONE
            imageFromWeb.visibility=View.GONE
            imageAddYourOwnRecipe.visibility=View.GONE
            imageTakingAPicture.visibility=View.VISIBLE
        }


    }

    private fun searchRecipeModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Apple"
        data1.isOpen = false
        data1.type = "SearchRecipe"
        data1.image = R.drawable.apple_image

        data2.title = "Banana"
        data2.isOpen = false
        data2.type = "SearchRecipe"
        data2.image = R.drawable.banana_image

        data3.title = "Egg"
        data3.isOpen = false
        data3.type = "SearchRecipe"
        data3.image = R.drawable.egg_image

        data4.title = "PineApple"
        data4.isOpen = false
        data4.type = "SearchRecipe"
        data4.image = R.drawable.pineapple_image

        data5.title = "Coriander"
        data5.isOpen = false
        data5.type = "SearchRecipe"
        data5.image = R.drawable.coriander_image

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)

        searchRecipeAdapter = SearchRecipeAdapter(dataList, requireActivity())
        binding!!.rcySearchRecipe.adapter = searchRecipeAdapter
    }

    private fun searchMealModel() {
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

        data1.title = "BreakFast"
        data1.isOpen = false
        data1.type = "SearchMeal"
        data1.image = R.drawable.breakfast_image

        data2.title = "Lunch"
        data2.isOpen = false
        data2.type = "SearchMeal"
        data2.image = R.drawable.lunch_image

        data3.title = "Dinner"
        data3.isOpen = false
        data3.type = "SearchMeal"
        data3.image = R.drawable.lunch_image

        data4.title = "Snack"
        data4.isOpen = false
        data4.type = "SearchMeal"
        data4.image = R.drawable.snack_image

        data5.title = "Dessert"
        data5.isOpen = false
        data5.type = "SearchMeal"
        data5.image = R.drawable.dessert_image

        data6.title = "Brunch"
        data6.isOpen = false
        data6.type = "SearchMeal"
        data6.image = R.drawable.lunch_image

        data7.title = "BreakFast"
        data7.isOpen = false
        data7.type = "SearchMeal"
        data7.image = R.drawable.breakfast_image

        data8.title = "Lunch"
        data8.isOpen = false
        data8.type = "SearchMeal"
        data8.image = R.drawable.lunch_image

        data9.title = "Dinner"
        data9.isOpen = false
        data9.type = "SearchMeal"
        data9.image = R.drawable.lunch_image

        data10.title = "Snack"
        data10.isOpen = false
        data10.type = "SearchMeal"
        data10.image = R.drawable.snack_image

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

        searchMealAdapter = SearchMealAdapter(dataList, requireActivity())
        binding!!.rcySearchMeal.adapter = searchMealAdapter
    }

    private fun searchPopCatModel() {
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

        data1.title = "BBQ"
        data1.isOpen = false
        data1.type = "SearchPopCat"
        data1.image = R.drawable.bbq_image

        data2.title = "Summer"
        data2.isOpen = false
        data2.type = "SearchPopCat"
        data2.image = R.drawable.summer_image

        data3.title = "Smoothie"
        data3.isOpen = false
        data3.type = "SearchPopCat"
        data3.image = R.drawable.bbq_image

        data4.title = "Mexician"
        data4.isOpen = false
        data4.type = "SearchPopCat"
        data4.image = R.drawable.mexician_image

        data5.title = "Salad"
        data5.isOpen = false
        data5.type = "SearchPopCat"
        data5.image = R.drawable.salaad_image

        data6.title = "Asian"
        data6.isOpen = false
        data6.type = "SearchPopCat"
        data6.image = R.drawable.mexician_image

        data7.title = "BBQ"
        data7.isOpen = false
        data7.type = "SearchPopCat"
        data7.image = R.drawable.bbq_image

        data8.title = "Summer"
        data8.isOpen = false
        data8.type = "SearchPopCat"
        data8.image = R.drawable.summer_image

        data9.title = "Smoothie"
        data9.isOpen = false
        data9.type = "SearchPopCat"
        data9.image = R.drawable.bbq_image

        data10.title = "Mexician"
        data10.isOpen = false
        data10.type = "SearchPopCat"
        data10.image = R.drawable.mexician_image

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

        searchMealAdapter = SearchMealAdapter(dataList, requireActivity())
        binding!!.rcyPopularCat.adapter = searchMealAdapter
    }

    override fun onClick(item: View?) {
        when(item!!.id){
            R.id.relViewAll->{
                findNavController().navigate(R.id.allIngredientsFragment)
            }

            R.id.imageProfile->{
                findNavController().navigate(R.id.settingProfileFragment)
            }

            R.id.imgHeartRed->{
                findNavController().navigate(R.id.cookBookFragment)
            }

            R.id.imgBasketIcon->{
                findNavController().navigate(R.id.basketScreenFragment)
            }

            R.id.imgFilterIcon->{
                findNavController().navigate(R.id.filterSearchFragment)
            }
        }

    }

}
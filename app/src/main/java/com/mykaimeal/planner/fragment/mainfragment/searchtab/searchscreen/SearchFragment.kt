package com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterUrlIngredientItem
import com.mykaimeal.planner.adapter.SearchMealAdapter
import com.mykaimeal.planner.adapter.SearchMealCatAdapter
import com.mykaimeal.planner.adapter.SearchRecipeAdapter
import com.mykaimeal.planner.apiInterface.BaseUrl
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.FragmentSearchBinding
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.UpdatePreferenceSuccessfully
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.apiresponse.Category
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.apiresponse.Data
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.apiresponse.Ingredient
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.apiresponse.MealType
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.apiresponse.SearchApiResponse
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.model.SearchMealUrlModel
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.model.SearchMealUrlModelData
import com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.viewmodel.SearchRecipeViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.CookBookListResponse
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class SearchFragment : Fragment(),View.OnClickListener, OnItemClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var searchRecipeAdapter: SearchRecipeAdapter? = null
    private var searchMealAdapter: SearchMealAdapter? = null
    private var searchMealCatAdapter: SearchMealCatAdapter? = null
    private var status:String?="RecipeSearch"
    private lateinit var commonWorkUtils: CommonWorkUtils
    private lateinit var sessionManagement: SessionManagement
    private lateinit var searchRecipeViewModel:SearchRecipeViewModel
    private var clickedUrl: String = ""
    private var ingredient: MutableList<Ingredient>?= mutableListOf()
    private var mealType: MutableList<MealType>?= mutableListOf()
    private var category: MutableList<Category>?= mutableListOf()
    private var cookbookList: MutableList<com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        (activity as MainActivity?)?.changeBottom("search")

        (activity as? MainActivity)?.binding?.let {
            it.llIndicator.visibility = View.VISIBLE
            it.llBottomNavigation.visibility = View.VISIBLE
        }

        binding.cardViewAddRecipe.visibility=View.GONE

        clickedUrl = arguments?.getString("ClickedUrl", "").toString()

        searchRecipeViewModel = ViewModelProvider(this)[SearchRecipeViewModel::class.java]

        cookbookList.clear()

        val data= com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data("","",0,"","Favourites",0,"",0)
        cookbookList.add(0,data)

        commonWorkUtils = CommonWorkUtils(requireActivity())
        sessionManagement = SessionManagement(requireContext())


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        searchRecipeDialog()

        initialize()

        // This Api call when the screen in loaded
        lunchApi()

        return binding.root
    }

    private fun lunchApi() {
        if (BaseApplication.isOnline(requireActivity())) {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                searchRecipeViewModel.recipeforSearchApi {
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

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SearchApiResponse::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                showData(apiModel.data)
            } else {
                handleError(apiModel.code,apiModel.message)
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    private fun handleError(code: Int, message: String) {
        if (code == ErrorMessage.code) {
            showAlert(message, true)
        } else {
            showAlert(message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessPreferences(data: String) {
        try {
            val gson = Gson()
            val updateModel = gson.fromJson(data, UpdatePreferenceSuccessfully::class.java)
            if (updateModel.code == 200 && updateModel.success) {
                lunchApi()
            } else {
                handleError(updateModel.code,updateModel.message)
            }
        }catch (e:Exception){
            Log.d("bodyGoal@@@","message"+e.message)
        }
    }

    private fun showData(data: Data?) {
        try {
            if (data?.ingredient!=null && data.ingredient.size>0){
                ingredient=data.ingredient
                searchRecipeAdapter = SearchRecipeAdapter(data.ingredient, requireActivity())
                binding.rcySearchRecipe.adapter = searchRecipeAdapter
                binding.llSearchRecipientIng.visibility=View.VISIBLE
            }else{
                binding.llSearchRecipientIng.visibility=View.GONE
            }

            if (data?.mealType!=null && data.mealType.size>0){
                mealType=data.mealType
                searchMealAdapter = SearchMealAdapter(data.mealType, requireActivity(),this)
                binding.rcySearchMeal.adapter = searchMealAdapter
                binding.llSearchByMeal.visibility=View.VISIBLE
            }else{
                binding.llSearchByMeal.visibility=View.GONE
            }

            if (data?.category!=null && data.category.size>0){
                category=data.category
                searchMealCatAdapter = SearchMealCatAdapter(data.category, requireActivity(),this)
                binding.rcyPopularCat.adapter = searchMealCatAdapter
                binding.llPopularCat.visibility=View.VISIBLE
            }else{
                binding.llPopularCat.visibility=View.GONE
            }

            if (data?.preference_status!=null){
                if (data.preference_status == 0){
                    Glide.with(requireActivity())
                        .load(R.drawable.toggle_off_icon)
                        .into(binding.imgPreferences)
                }else{
                    Glide.with(requireActivity())
                        .load(R.drawable.toggle_on_icon)
                        .into(binding.imgPreferences)
                }
            }else{
                Glide.with(requireActivity())
                    .load(R.drawable.toggle_off_icon)
                    .into(binding.imgPreferences)
            }

        }catch (e:Exception){
            showAlert(e.message, false)
        }
    }

    private fun initialize() {

        if (sessionManagement.getUserName() != null) {
            val name = BaseApplication.getColoredSpanned(
                "Hello",
                "#06C169"
            ) + BaseApplication.getColoredSpanned(", " + sessionManagement.getUserName(), "#000000")
            binding.tvUserName.text = Html.fromHtml(name)
        }

        if (sessionManagement.getImage()!=null){
            Glide.with(requireContext())
                .load(BaseUrl.imageBaseUrl+sessionManagement.getImage())
                .placeholder(R.drawable.mask_group_icon)
                .error(R.drawable.mask_group_icon)
                .into(binding.imageProfile)
        }

        binding.relViewAll.setOnClickListener(this)
        binding.imgHeartRed.setOnClickListener(this)
        binding.imageProfile.setOnClickListener(this)
        binding.imgBasketIcon.setOnClickListener(this)
        binding.imgFilterIcon.setOnClickListener(this)
        binding.imgPreferences.setOnClickListener(this)

        binding.etIngRecipeSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val query = editable.toString().trim()
                if (query.isNotEmpty()) {
                    if (binding.rcySearchRecipe.visibility == View.VISIBLE) {
                        filterIngredients(query)
                    }
                    if (binding.rcySearchMeal.visibility == View.VISIBLE) {
                        filterMealType(query)
                    }
                    if (binding.rcyPopularCat.visibility == View.VISIBLE) {
                        filterPopular(query)
                    }
                } else {
                    // If the query is empty, reset the lists to show all original data
                    resetLists()
                }
            }
        })


    }

    private fun resetLists() {
        if (binding.rcySearchRecipe.visibility == View.VISIBLE) {
            ingredient?.let { searchRecipeAdapter!!.submitList(it) } // Reset recipe list
        }

        if (binding.rcySearchMeal.visibility == View.VISIBLE) {
            searchMealAdapter?.submitList(mealType) // Reset meal type list
        }

        if (binding.rcyPopularCat.visibility == View.VISIBLE) {
            searchMealCatAdapter?.submitList(category) // Reset popular category list
        }
    }

    private fun filterPopular(editText: String) {
        val filteredList: MutableList<Category> =
            java.util.ArrayList<Category>()
        for (item in category!!) {
            if (item.name!!.lowercase().contains(editText.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.size > 0) {
            searchMealCatAdapter!!.filterList(filteredList)
            binding.llPopularCat.visibility = View.VISIBLE
        } else {
            binding.llPopularCat.visibility = View.GONE
        }
    }

    private fun filterMealType(editText: String) {
        val filteredList: MutableList<MealType> =
            java.util.ArrayList<MealType>()
        for (item in mealType!!) {
            if (item.name!!.lowercase().contains(editText.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.size > 0) {
            searchMealAdapter!!.filterList(filteredList)
            binding.llSearchByMeal.visibility = View.VISIBLE
        } else {
            binding.llSearchByMeal.visibility = View.GONE
        }
    }

    private fun filterIngredients(editText: String) {
        val filteredList: MutableList<Ingredient> =
            java.util.ArrayList<Ingredient>()
        for (item in ingredient!!) {
            if (item.name!!.lowercase().contains(editText.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.size > 0) {
            searchRecipeAdapter!!.filterList(filteredList)
            binding.llSearchRecipientIng.visibility = View.VISIBLE
        } else {
            binding.llSearchRecipientIng.visibility = View.GONE
        }
    }

    private fun addRecipeFromWeb() {
        val dialogWeb = Dialog(requireActivity())
        dialogWeb.setContentView(R.layout.alert_dialog_add_recipe_form_web)
        dialogWeb.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogWeb.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val etPasteURl = dialogWeb.findViewById<EditText>(R.id.etPasteURl)
        val rlSearchRecipe = dialogWeb.findViewById<RelativeLayout>(R.id.rlSearchRecipe)
        val imageCrossWeb = dialogWeb.findViewById<ImageView>(R.id.imageCrossWeb)
        dialogWeb.show()
        dialogWeb.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlSearchRecipe.setOnClickListener {
            if (etPasteURl.text.toString().isEmpty()){
                commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.pasteUrl, false)
            } /*else if (isValidUrl(etPasteURl.text.toString().trim())){
                commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.validUrl, false)
            } */else{
                val bundle = Bundle().apply {
                    putString("url",etPasteURl.text.toString().trim())
                }
                findNavController().navigate(R.id.webViewByUrlFragment,bundle)
                dialogWeb.dismiss()
            }
        }

        imageCrossWeb.setOnClickListener{
            dialogWeb.dismiss()
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

        val tvRecipeSearch = dialogSearchDialog.findViewById<TextView>(R.id.tvRecipeSearch)
        val tvFavouritesRecipes = dialogSearchDialog.findViewById<TextView>(R.id.tvFavouritesRecipes)
        val tvFromWeb = dialogSearchDialog.findViewById<TextView>(R.id.tvFromWeb)
        val tvAddYourOwnRecipe = dialogSearchDialog.findViewById<TextView>(R.id.tvAddYourOwnRecipe)
        val tvTakingAPicture = dialogSearchDialog.findViewById<TextView>(R.id.tvTakingAPicture)

        val rlSearch = dialogSearchDialog.findViewById<RelativeLayout>(R.id.rlSearch)
        val imgCrossSearch = dialogSearchDialog.findViewById<ImageView>(R.id.imgCrossSearch)

        dialogSearchDialog.show()
        dialogSearchDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlSearch.setOnClickListener {
            when (status) {
                "RecipeSearch" -> {

                }
                "FavouritesRecipes" -> {
                    findNavController().navigate(R.id.cookBookFragment)
                }
                "Web" -> {
                    addRecipeFromWeb()
                }
                "AddRecipe" -> {
                    val bundle = Bundle().apply {
                        putString("name","")
                    }
                    findNavController().navigate(R.id.createRecipeFragment,bundle)
                }
                else -> {
                    findNavController().navigate(R.id.createRecipeImageFragment)
                }
            }
            dialogSearchDialog.dismiss()
        }

        imgCrossSearch.setOnClickListener{
            dialogSearchDialog.dismiss()
        }

        fun updateSelection(selectedView: View, tvTakingAPicture: TextView) {
            val allViews = listOf(relRecipeSearch, relFavouritesRecipes, relFromWeb, relAddYourOwnRecipe, relTakingAPicture)
            val textViews = listOf(tvRecipeSearch, tvFavouritesRecipes, tvFromWeb, tvAddYourOwnRecipe, tvTakingAPicture)
            val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.orange_tick_icon) // Replace with your drawable
            allViews.forEach { it.setBackgroundResource(R.drawable.gray_box_border_bg) }
            textViews.forEach { it.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null) }
            selectedView.setBackgroundResource(R.drawable.orange_box_bg)
            tvTakingAPicture.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null)
        }

        relRecipeSearch.setOnClickListener {
            status = "RecipeSearch"
            updateSelection(relRecipeSearch,tvRecipeSearch)
        }

        relFavouritesRecipes.setOnClickListener {
            status = "FavouritesRecipes"
            updateSelection(relFavouritesRecipes,tvFavouritesRecipes)
        }

        relFromWeb.setOnClickListener {
            status = "Web"
            updateSelection(relFromWeb,tvFromWeb)
        }

        relAddYourOwnRecipe.setOnClickListener {
            status = "AddRecipe"
            updateSelection(relAddYourOwnRecipe,tvAddYourOwnRecipe)
        }

        relTakingAPicture.setOnClickListener {
            status = "TakingPicture"
            updateSelection(relTakingAPicture,tvTakingAPicture)
        }

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
            R.id.imgPreferences->{
                upDatePreferences()
            }
        }
    }

    private fun upDatePreferences() {
        if (BaseApplication.isOnline(requireActivity())) {
//            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                searchRecipeViewModel.recipePreferencesApi {
                    BaseApplication.dismissMe()
                    when (it) {
                        is NetworkResult.Success -> handleSuccessPreferences(it.data.toString())
                        is NetworkResult.Error -> showAlert(it.message, false)
                        else -> showAlert(it.message, false)
                    }
                }
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
            val bundle = Bundle().apply {
                putString("recipeName",status)
            }
            findNavController().navigate(R.id.searchedRecipeBreakfastFragment,bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
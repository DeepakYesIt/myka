package com.mykaimeal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mykaimeal.planner.OnItemClickListener
import com.mykaimeal.planner.OnItemLongClickListener
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterSuperMarket
import com.mykaimeal.planner.adapter.RecipeCookedAdapter
import com.mykaimeal.planner.apiInterface.BaseUrl
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.databinding.FragmentHomeBinding
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.HomeViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse.HomeApiResponse
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.CookBookListResponse
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.DataModel
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener, OnItemClickListener,
    OnItemLongClickListener {

    private var binding: FragmentHomeBinding? = null
    private var dataList3: MutableList<DataModel> = mutableListOf()
    private var recipeCookedAdapter: RecipeCookedAdapter? = null
    private var adapterSuperMarket: AdapterSuperMarket? = null
    private var statuses: String? = ""
    private var checkStatus: Boolean? = false
    private var recySuperMarket: RecyclerView? = null
    private lateinit var sessionManagement: SessionManagement
    private lateinit var viewModel: HomeViewModel
    private lateinit var userDataLocal: com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse.DataModel
    private lateinit var spinnerActivityLevel: PowerSpinnerView
    private var cookbookList: MutableList<com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data> =
        mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sessionManagement = SessionManagement(requireContext())

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE

        (activity as MainActivity?)?.changeBottom("home")

        cookbookList.clear()
        val data =
            com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data(
                "",
                "",
                0,
                "",
                "Favourites",
                0,
                "",
                0
            )
        cookbookList.add(0, data)

        homeSchDinnerModel()
//        addSuperMarketDialog()

        initialize()

        // When screen load then api call
        fetchDataOnLoad()

        return binding!!.root
    }

    private fun fetchDataOnLoad() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchHomeDetailsData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchHomeDetailsData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.homeDetailsRequest {
                BaseApplication.dismissMe()
                handleApiResponse(it)
            }
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, HomeApiResponse::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                showData(apiModel.data)
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


    @SuppressLint("SetTextI18n")
    private fun showData(data: com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse.DataModel?) {
        try {
            userDataLocal = data!!

            if (userDataLocal.userData != null && userDataLocal.userData!!.size > 0) {
                binding!!.relPlanMeal.visibility = View.GONE
                binding!!.llRecipesCooked.visibility = View.VISIBLE
                recipeCookedAdapter =
                    RecipeCookedAdapter(userDataLocal.userData, requireActivity(), this)
                binding!!.rcyRecipesCooked.adapter = recipeCookedAdapter
            } else {
                binding!!.relPlanMeal.visibility = View.VISIBLE
                binding!!.llRecipesCooked.visibility = View.GONE
            }

            if (userDataLocal.graph_value == 0) {
                binding!!.imagePlanMeal.visibility = View.VISIBLE
                binding!!.imageCheckSav.visibility = View.GONE
            } else {
                binding!!.imagePlanMeal.visibility = View.GONE
                binding!!.imageCheckSav.visibility = View.VISIBLE
            }

            if (userDataLocal.date != null && !userDataLocal.date.equals("", true)) {
                val name = BaseApplication.getColoredSpanned(
                    "Next meal to be cooked on ",
                    "#3C4541"
                ) + BaseApplication.getColoredSpanned(data.date + ".", "#06C169")
                binding!!.tvHomeDesc.text = Html.fromHtml(name)
            } else {
                binding!!.tvHomeDesc.text =
                    "Your cooking schedule is empty! Tap the button below to add a meal and get started."
            }


            fun updateCount(breakfast: Int?) {
                var cookstatus = false
                if (breakfast!! != 0){
                    cookstatus=true
                }
                /*if (cookstatus) {
                    binding!!.rlSeeAllBtn.visibility = View.VISIBLE
                    binding!!.imageCookedMeals.visibility = View.GONE
                } else {
                    binding!!.imageCookedMeals.visibility = View.VISIBLE
                    binding!!.rlSeeAllBtn.visibility = View.GONE
                }*/
            }



            if (userDataLocal.frezzer != null) {

                if (userDataLocal.frezzer.Breakfast != null) {
                    binding!!.tvfreezerbreakfast.text = "" + userDataLocal.frezzer.Breakfast
                    updateCount(userDataLocal.frezzer.Breakfast)

                }
                if (userDataLocal.frezzer.Lunch != null) {
                    binding!!.tvfreezerlunch.text = "" + userDataLocal.frezzer.Lunch
                    updateCount(userDataLocal.frezzer.Lunch)
                }
                if (userDataLocal.frezzer.Dinner != null) {
                    binding!!.tvfreezerdinner.text = "" + userDataLocal.frezzer.Dinner
                    updateCount(userDataLocal.frezzer.Dinner)
                }

                if (userDataLocal.frezzer.Snacks != null) {
                    binding!!.laySnack.visibility = View.VISIBLE
                    binding!!.tvfreezersnack.text = "" + userDataLocal.frezzer.Snacks
                    updateCount(userDataLocal.frezzer.Snacks)
                } else {
                    binding!!.tvfreezersnack.visibility = View.GONE
                }

                if (userDataLocal.frezzer.Teatime != null) {
                    binding!!.layTeatime.visibility = View.VISIBLE
                    binding!!.tvfreezerteatime.text = "" + userDataLocal.frezzer.Teatime
                    updateCount(userDataLocal.frezzer.Breakfast)
                } else {
                    binding!!.layTeatime.visibility = View.GONE
                }
            }

            if (userDataLocal.fridge != null) {

                if (userDataLocal.fridge.Breakfast != null) {
                    binding!!.tvfridgebreakfast.text = "" + userDataLocal.fridge.Breakfast
                    updateCount(userDataLocal.fridge.Breakfast)
                }
                if (userDataLocal.fridge.Lunch != null) {
                    binding!!.tvfridgelunch.text = "" + userDataLocal.fridge.Lunch
                    updateCount(userDataLocal.fridge.Lunch)
                }
                if (userDataLocal.fridge.Dinner != null) {
                    binding!!.tvfridgedinner.text = "" + userDataLocal.fridge.Dinner
                    updateCount(userDataLocal.fridge.Dinner)
                }
                if (userDataLocal.fridge.Snacks != null) {
                    binding!!.laySnack.visibility = View.VISIBLE
                    binding!!.tvfridgesnack.text = "" + userDataLocal.fridge.Snacks
                    updateCount(userDataLocal.fridge.Snacks)
                } else {
                    binding!!.laySnack.visibility = View.GONE
                }

                if (userDataLocal.fridge.Teatime != null) {
                    binding!!.layTeatime.visibility = View.VISIBLE
                    binding!!.tvfridgeteatime.text = "" + userDataLocal.fridge.Teatime
                    updateCount(userDataLocal.fridge.Teatime)
                } else {
                    binding!!.layTeatime.visibility = View.GONE
                }

            }





        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }


    private fun checkForZeroValues(): Boolean {
        var allZero = true // Initialize to true

        if (userDataLocal.frezzer != null) {
            if (userDataLocal.frezzer.Breakfast != null) {
                binding!!.tvfreezerbreakfast.text = "" + userDataLocal.frezzer.Breakfast
                if (userDataLocal.frezzer.Breakfast == 0) return false
                allZero = false
            }
            if (userDataLocal.frezzer.Lunch != null) {
                binding!!.tvfreezerlunch.text = "" + userDataLocal.frezzer.Lunch
                if (userDataLocal.frezzer.Lunch == 0) return false
                allZero = false
            }
            if (userDataLocal.frezzer.Dinner != null) {
                binding!!.tvfreezerdinner.text = "" + userDataLocal.frezzer.Dinner
                if (userDataLocal.frezzer.Dinner == 0) return false
                allZero = false
            }
            if (userDataLocal.frezzer.Snacks != null) {
                binding!!.laySnack.visibility = View.VISIBLE
                binding!!.tvfreezersnack.text = "" + userDataLocal.frezzer.Snacks
                if (userDataLocal.frezzer.Snacks == 0) return false
                allZero = false
            } else {
                binding!!.tvfreezersnack.visibility = View.GONE
            }
            if (userDataLocal.frezzer.Teatime != null) {
                binding!!.layTeatime.visibility = View.VISIBLE
                binding!!.tvfreezerteatime.text = "" + userDataLocal.frezzer.Teatime
                if (userDataLocal.frezzer.Teatime == 0) return false
                allZero = false
            } else {
                binding!!.layTeatime.visibility = View.GONE
            }
        }

        if (userDataLocal.fridge != null) {
            if (userDataLocal.fridge.Breakfast != null) {
                binding!!.tvfridgebreakfast.text = "" + userDataLocal.fridge.Breakfast
                if (userDataLocal.fridge.Breakfast == 0) return false
                allZero = false
            }
            if (userDataLocal.fridge.Lunch != null) {
                binding!!.tvfridgelunch.text = "" + userDataLocal.fridge.Lunch
                if (userDataLocal.fridge.Lunch == 0) return false
                allZero = false
            }
            if (userDataLocal.fridge.Dinner != null) {
                binding!!.tvfridgedinner.text = "" + userDataLocal.fridge.Dinner
                if (userDataLocal.fridge.Dinner == 0) return false
                allZero = false
            }
            if (userDataLocal.fridge.Snacks != null) {
                binding!!.laySnack.visibility = View.VISIBLE
                binding!!.tvfridgesnack.text = "" + userDataLocal.fridge.Snacks
                if (userDataLocal.fridge.Snacks == 0) return false
                allZero = false
            } else {
                binding!!.laySnack.visibility = View.GONE
            }
            if (userDataLocal.fridge.Teatime != null) {
                binding!!.layTeatime.visibility = View.VISIBLE
                binding!!.tvfridgeteatime.text = "" + userDataLocal.fridge.Teatime
                if (userDataLocal.fridge.Teatime == 0) return false
                allZero = false
            } else {
                binding!!.layTeatime.visibility = View.GONE
            }
        }

        if (allZero) {
            binding!!.rlSeeAllBtn.visibility = View.VISIBLE
            binding!!.imageCookedMeals.visibility = View.GONE
        } else {
            binding!!.imageCookedMeals.visibility = View.VISIBLE
            binding!!.rlSeeAllBtn.visibility = View.GONE
        }

        return true // Return true only if no value is zero
    }


    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }


    private fun addSuperMarketDialog() {
        val dialogAddItem: Dialog = context?.let { Dialog(it) }!!
        dialogAddItem.setContentView(R.layout.alert_dialog_super_market)
        dialogAddItem.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddItem.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
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

        /* ingredientDinnerAdapter = IngredientsDinnerAdapter(dataList3, requireActivity(), this, null,this)
         binding!!.rcyRecipesCooked.adapter = ingredientDinnerAdapter*/
    }

    private fun initialize() {

        if (sessionManagement.getImage() != null) {
            Glide.with(requireContext())
                .load(BaseUrl.imageBaseUrl + sessionManagement.getImage())
                .placeholder(R.drawable.mask_group_icon)
                .error(R.drawable.mask_group_icon)
                .into(binding!!.imageProfile)
        }


        if (sessionManagement.getUserName() != null) {
            val name = BaseApplication.getColoredSpanned(
                "Hello",
                "#06C169"
            ) + BaseApplication.getColoredSpanned(", " + sessionManagement.getUserName(), "#000000")
            binding?.tvName?.text = Html.fromHtml(name)
        }


        binding!!.rlSeeAllBtn.setOnClickListener(this)
        binding!!.textSeeAll.setOnClickListener(this)

        binding!!.imageCookedMeals.setOnClickListener(this)
        binding!!.imgFreeTrial.setOnClickListener(this)
        binding!!.imgBasketIcon.setOnClickListener(this)
        binding!!.tvName.setOnClickListener(this)

        binding!!.imageProfile.setOnClickListener(this)

        binding!!.rlPlanAMealBtn.setOnClickListener(this)
        binding!!.imgHearRedIcons.setOnClickListener(this)
        binding!!.imagePlanMeal.setOnClickListener(this)
//        binding!!.imageRecipeSeeAll.setOnClickListener(this)
//        binding!!.relMonthlySavings.setOnClickListener(this)
        binding!!.imageCheckSav.setOnClickListener(this)


    }

    override fun onClick(item: View?) {
        when (item!!.id) {
            R.id.textSeeAll -> {
                findNavController().navigate(R.id.fullCookedScheduleFragment)
            }

            R.id.rlSeeAllBtn -> {
                findNavController().navigate(R.id.cookedFragment)
            }

            R.id.imageCheckSav -> {
                findNavController().navigate(R.id.statisticsGraphFragment)
            }

            R.id.imagePlanMeal -> {
                findNavController().navigate(R.id.planFragment)
            }

            R.id.imageCookedMeals -> {
                findNavController().navigate(R.id.cookedFragment)
            }

            R.id.imgBasketIcon -> {
                findNavController().navigate(R.id.basketScreenFragment)
            }

            R.id.imgHearRedIcons -> {
                findNavController().navigate(R.id.cookBookFragment)
            }

            R.id.imageProfile -> {
                findNavController().navigate(R.id.settingProfileFragment)
            }

            R.id.rlPlanAMealBtn -> {
                findNavController().navigate(R.id.planFragment)
            }

            R.id.imgFreeTrial -> {
                findNavController().navigate(R.id.homeSubscriptionFragment)
            }
            ///for checking purpose only
            R.id.tv_name -> {
                findNavController().navigate(R.id.statisticsGraphFragment)
            }

        }
    }

    override fun itemClick(position: Int?, status: String?, type: String?) {
        /*if (type == "heart") {
            addRecipeDialog(position, type)
        } else if (type == "minus") {
            if (status == "1") {
                removeDayDialog(position, type)
            }
        } else if (type=="missingIng") {
            findNavController().navigate(R.id.missingIngredientsFragment)
        }else{
            findNavController().navigate(R.id.recipeDetailsFragment)
        }*/

        when (status) {
            "1" -> {

            }

            "2" -> {

            }

            "4" -> {
                if (BaseApplication.isOnline(requireActivity())) {
                    // Safely get the item and position
                    val newLikeStatus =
                        if (userDataLocal.userData?.get(position!!)?.is_like == 0) "1" else "0"
                    if (newLikeStatus.equals("0", true)) {
                        recipeLikeAndUnlikeData(position, newLikeStatus, "", null)
                    } else {
                        addFavTypeDialog(position, newLikeStatus)
                    }
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

            "5" -> {
                val bundle = Bundle().apply {
                    putString("uri", type)
                    putString("mealType", userDataLocal.userData?.get(position!!)?.recipe?.type)
                }
                findNavController().navigate(R.id.recipeDetailsFragment, bundle)
            }

        }

    }

    private fun addFavTypeDialog(position: Int?, likeType: String) {
        val dialogAddRecipe: Dialog = context?.let { Dialog(it) }!!
        dialogAddRecipe.setContentView(R.layout.alert_dialog_add_recipe)
        dialogAddRecipe.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogAddRecipe.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogAddRecipe.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        spinnerActivityLevel = dialogAddRecipe.findViewById(R.id.spinnerActivityLevel)
        val relCreateNewCookBook =
            dialogAddRecipe.findViewById<RelativeLayout>(R.id.relCreateNewCookBook)
        val relFavourites = dialogAddRecipe.findViewById<RelativeLayout>(R.id.relFavourites)
        val imgCheckBoxOrange = dialogAddRecipe.findViewById<ImageView>(R.id.imgCheckBoxOrange)

        spinnerActivityLevel.setItems(cookbookList.map { it.name })

        dialogAddRecipe.show()
        dialogAddRecipe.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        getCookBookList()

        relCreateNewCookBook.setOnClickListener {
            relCreateNewCookBook.setBackgroundResource(R.drawable.light_green_rectangular_bg)
            imgCheckBoxOrange.setImageResource(R.drawable.orange_uncheck_box_images)
            dialogAddRecipe.dismiss()
            val bundle = Bundle()
            bundle.putString("value", "New")
            bundle.putString("uri", userDataLocal.userData?.get(position!!)?.recipe?.url)
            findNavController().navigate(R.id.createCookBookFragment, bundle)
        }


        rlDoneBtn.setOnClickListener {
            if (spinnerActivityLevel.text.toString()
                    .equals(ErrorMessage.cookBookSelectError, true)
            ) {
                BaseApplication.alertError(
                    requireContext(),
                    ErrorMessage.selectCookBookError,
                    false
                )
            } else {
                val cookbooktype = cookbookList[spinnerActivityLevel.selectedIndex].id
                recipeLikeAndUnlikeData(
                    position,
                    likeType,
                    cookbooktype.toString(),
                    dialogAddRecipe
                )
            }
        }
    }

    private fun getCookBookList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.getCookBookRequest {
                BaseApplication.dismissMe()
                handleApiCookBookResponse(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessCookBookResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, CookBookListResponse::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null && apiModel.data.size > 0) {
                    cookbookList.retainAll { it == cookbookList[0] }
                    cookbookList.addAll(apiModel.data)
                    // OR directly modify the original list
                    spinnerActivityLevel.setItems(cookbookList.map { it.name })
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

    private fun handleApiCookBookResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessCookBookResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    private fun recipeLikeAndUnlikeData(
        position: Int?,
        likeType: String,
        cookbooktype: String,
        dialogAddRecipe: Dialog?
    ) {

        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.likeUnlikeRequest({
                BaseApplication.dismissMe()

                handleLikeAndUnlikeApiResponse(it, position, dialogAddRecipe)
            }, userDataLocal.userData?.get(position!!)?.recipe?.uri!!, likeType, cookbooktype)
        }
    }


    private fun handleLikeAndUnlikeApiResponse(
        result: NetworkResult<String>,
        position: Int?,
        dialogAddRecipe: Dialog?
    ) {
        when (result) {
            is NetworkResult.Success -> handleLikeAndUnlikeSuccessResponse(
                result.data.toString(),
                position,
                dialogAddRecipe
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleLikeAndUnlikeSuccessResponse(
        data: String,
        position: Int?,
        dialogAddRecipe: Dialog?
    ) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                // Toggle the is_like value
                val item = userDataLocal.userData?.getOrNull(position!!) ?: return
                // Toggle the is_like value
                item.is_like = if (item.is_like == 0) 1 else 0
                // Update the list at the specific position
                userDataLocal.userData!![position!!] = item
                recipeCookedAdapter?.updateList(userDataLocal.userData)
                dialogAddRecipe?.dismiss()
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
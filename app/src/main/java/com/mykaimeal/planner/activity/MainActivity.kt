package com.mykaimeal.planner.activity

import PlanApiResponse
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.adapter.ChooseDayAdapter
import com.mykaimeal.planner.adapter.ImageViewPagerAdapter
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.BaseApplication.formatDate
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.databinding.ActivityMainBinding
import com.mykaimeal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykaimeal.planner.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData
import com.mykaimeal.planner.fragment.commonfragmentscreen.mealRoutine.viewmodel.MealRoutineViewModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.Data
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.RecipesModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import com.mykaimeal.planner.model.DataModel
import com.mykaimeal.planner.model.DateModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnClickListener {

    var binding: ActivityMainBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private lateinit var mealRoutineViewModel: MealRoutineViewModel
    private var recipesModel: RecipesModel? = null
    private lateinit var layOnBoardingIndicator: LinearLayout
    val dataList = ArrayList<DataModel>()
    private var adapter: ImageViewPagerAdapter? =null
    private var tvWeekRange: TextView? = null
    private var currentDate = Date() // Current date

    // Define global variables
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var sharedPreferences: SharedPreferences
    private val prefsName = "MyPrefs"
    private val lastShownTimeKey = "last_dialog_time"
    private val oneDayMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)
        mealRoutineViewModel = ViewModelProvider(this@MainActivity)[MealRoutineViewModel::class.java]
        commonWorkUtils = CommonWorkUtils(this)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        handleDeepLink(intent)

        binding!!.llHomeIndicator.visibility = View.VISIBLE
        binding!!.llSearchIndicator.visibility = View.INVISIBLE
        binding!!.llAddRecipeIndicator.visibility = View.INVISIBLE
        binding!!.llPlanIndicator.visibility = View.INVISIBLE
        binding!!.llCookedIndicator.visibility = View.INVISIBLE
        getFcmToken()
        binding!!.imgHome.setColorFilter(ContextCompat.getColor(this, R.color.light_green))
        binding!!.imgSearch.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
        binding!!.imgAddRecipe.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
        binding!!.imgPlan.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
        binding!!.imgCooked.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
        binding!!.tvHome.setTextColor(ContextCompat.getColor(this, R.color.light_green))
        binding!!.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding!!.tvAddRecipe.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding!!.tvPlan.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding!!.tvCooked.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding!!.llHome.setOnClickListener(this)
        binding!!.llSearch.setOnClickListener(this)
        binding!!.llAddRecipe.setOnClickListener(this)
        binding!!.llPlan.setOnClickListener(this)
        binding!!.llCooked.setOnClickListener(this)
        binding!!.relAddRecipeWeb.setOnClickListener(this)
        binding!!.relCreateNewRecipe.setOnClickListener(this)
        binding!!.relRecipeImage.setOnClickListener(this)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        /// using function for find destination graph
        startDestination()

        // Check if 24 hours have passed since the last dialog was shown
        if (shouldShowDialog()) {
            dialogDailyInspiration()
        }

    }

    private fun shouldShowDialog(): Boolean {
        val lastShownTime = sharedPreferences.getLong(lastShownTimeKey, 0)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastShownTime) >= oneDayMillis
    }

        private fun handleDeepLink(intent: Intent?) {
            intent?.data?.let { uri ->
                val deepLinkValue = uri.getQueryParameter("deep_link_value")
                val deepLinkSub1 = uri.getQueryParameter("deep_link_sub1")
                Log.d("DeepLink", "Deep link value: $deepLinkValue, Sub1: $deepLinkSub1")

                // Navigate to the appropriate screen based on the deep link
                when (deepLinkValue) {
                    "profile_screen" -> {
                        // Navigate to Profile screen
                        startActivity(Intent(this, AuthActivity::class.java))
                    }
                    else -> {
                        // Handle other cases or show a default screen
                    }
                }
            }
        }


    private fun startDestination() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frameContainerMain) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)

        navGraph.setStartDestination(R.id.homeFragment)
        navController.graph = navGraph
    }

    /// this function using set custom bottom navigation
    fun changeBottom(status: String) {
        if (status.equals("home", true)) {

            binding!!.cardViewAddRecipe.visibility = View.GONE
            binding!!.imgHome.setColorFilter(ContextCompat.getColor(this, R.color.light_green))
            binding!!.imgSearch.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgAddRecipe.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgPlan.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgCooked.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.tvHome.setTextColor(ContextCompat.getColor(this, R.color.light_green))
            binding!!.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvAddRecipe.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvPlan.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvCooked.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.llHomeIndicator.visibility = View.VISIBLE
            binding!!.llSearchIndicator.visibility = View.INVISIBLE
            binding!!.llAddRecipeIndicator.visibility = View.INVISIBLE
            binding!!.llPlanIndicator.visibility = View.INVISIBLE
            binding!!.llCookedIndicator.visibility = View.INVISIBLE
        }
        if (status.equals("search", true)) {
            binding!!.cardViewAddRecipe.visibility = View.GONE
            binding!!.imgHome.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgSearch.setColorFilter(ContextCompat.getColor(this, R.color.light_green))
            binding!!.imgAddRecipe.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgPlan.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgCooked.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.light_green))
            binding!!.tvAddRecipe.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvPlan.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvCooked.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.llHomeIndicator.visibility = View.INVISIBLE
            binding!!.llSearchIndicator.visibility = View.VISIBLE
            binding!!.llAddRecipeIndicator.visibility = View.INVISIBLE
            binding!!.llPlanIndicator.visibility = View.INVISIBLE
            binding!!.llCookedIndicator.visibility = View.INVISIBLE
        }
        if (status.equals("addRecipe", true)) {
            binding!!.imgHome.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgSearch.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgAddRecipe.setColorFilter(ContextCompat.getColor(this, R.color.light_green))
            binding!!.imgPlan.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgCooked.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvAddRecipe.setTextColor(ContextCompat.getColor(this, R.color.light_green))
            binding!!.tvPlan.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvCooked.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.llHomeIndicator.visibility = View.INVISIBLE
            binding!!.llSearchIndicator.visibility = View.INVISIBLE
            binding!!.llAddRecipeIndicator.visibility = View.VISIBLE
            binding!!.llPlanIndicator.visibility = View.INVISIBLE
            binding!!.llCookedIndicator.visibility = View.INVISIBLE
        }
        if (status.equals("plan", true)) {
            binding!!.cardViewAddRecipe.visibility = View.GONE
            binding!!.imgHome.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgSearch.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgAddRecipe.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgPlan.setColorFilter(ContextCompat.getColor(this, R.color.light_green))
            binding!!.imgCooked.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvAddRecipe.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvPlan.setTextColor(ContextCompat.getColor(this, R.color.light_green))
            binding!!.tvCooked.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.llHomeIndicator.visibility = View.INVISIBLE
            binding!!.llSearchIndicator.visibility = View.INVISIBLE
            binding!!.llAddRecipeIndicator.visibility = View.INVISIBLE
            binding!!.llPlanIndicator.visibility = View.VISIBLE
            binding!!.llCookedIndicator.visibility = View.INVISIBLE
        }
        if (status.equals("cooked", true)) {
            binding!!.cardViewAddRecipe.visibility = View.GONE
            binding!!.imgHome.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgSearch.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgAddRecipe.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgPlan.setColorFilter(ContextCompat.getColor(this, R.color.light_grays))
            binding!!.imgCooked.setColorFilter(ContextCompat.getColor(this, R.color.light_green))
            binding!!.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvAddRecipe.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvPlan.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvCooked.setTextColor(ContextCompat.getColor(this, R.color.light_green))

            binding!!.llHomeIndicator.visibility = View.INVISIBLE
            binding!!.llSearchIndicator.visibility = View.INVISIBLE
            binding!!.llAddRecipeIndicator.visibility = View.INVISIBLE
            binding!!.llPlanIndicator.visibility = View.INVISIBLE
            binding!!.llCookedIndicator.visibility = View.VISIBLE
        }
    }

    /// add recipe screen
    private fun addRecipeFromWeb() {
        val dialogWeb: Dialog = this.let { Dialog(it) }
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

        imageCrossWeb.setOnClickListener{
            dialogWeb.dismiss()
        }

        rlSearchRecipe.setOnClickListener {
            if (etPasteURl.text.toString().isEmpty()) {
                commonWorkUtils.alertDialog(this, ErrorMessage.pasteUrl, false)
            }/* else if (isValidUrl(etPasteURl.text.toString().trim())){
                commonWorkUtils.alertDialog(this, ErrorMessage.validUrl, false)
            }*/ else {
                binding!!.cardViewAddRecipe.visibility = View.GONE
                val bundle = Bundle().apply {
                    putString("url",etPasteURl.text.toString().trim())
                }
                findNavController(R.id.frameContainerMain).navigate(R.id.webViewByUrlFragment,bundle)
                dialogWeb.dismiss()
            }
        }
    }

    private fun dialogDailyInspiration() {
        val dialog = Dialog(this@MainActivity, R.style.BottomSheetDialog)
        dialog.apply {
            setCancelable(true)
            setContentView(R.layout.alert_dialog_daily_inspiration)
            window?.attributes = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }

            layOnBoardingIndicator = findViewById<LinearLayout>(R.id.layonboarding_indicator)
            val viewPager = findViewById<ViewPager2>(R.id.viewPager)
            val llBreakfast = findViewById<LinearLayout>(R.id.llBreakfast)
            val llLunch = findViewById<LinearLayout>(R.id.llLunch)
            val llDinner = findViewById<LinearLayout>(R.id.llDinner)
            val rlAddPlanButton = findViewById<RelativeLayout>(R.id.rlAddPlanButton)
            val rlAddCartButton = findViewById<RelativeLayout>(R.id.rlAddCartButton)
            val textBreakfast = findViewById<TextView>(R.id.textBreakfast)
            val textDinner = findViewById<TextView>(R.id.textDinner)
            val textLunch = findViewById<TextView>(R.id.textLunch)
            val viewBreakfast = findViewById<View>(R.id.viewBreakfast)
            val viewLunch = findViewById<View>(R.id.viewLunch)
            val viewDinner = findViewById<View>(R.id.viewDinner)
            
            // Save the current time as the last shown time
            sharedPreferences.edit().putLong(lastShownTimeKey, System.currentTimeMillis()).apply()
            
            // When screen load then api call
            fetchDataOnLoad()

            llBreakfast.setOnClickListener {
                viewBreakfast.visibility = View.VISIBLE
                viewLunch.visibility = View.GONE
                viewDinner.visibility = View.GONE

                textBreakfast.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.orange))
                textDinner.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
            }

            llLunch.setOnClickListener {
                viewBreakfast.visibility = View.GONE
                viewLunch.visibility = View.VISIBLE
                viewDinner.visibility = View.GONE
                textBreakfast.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.orange))
                textDinner.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
            }

            llDinner.setOnClickListener {
                viewBreakfast.visibility = View.GONE
                viewLunch.visibility = View.GONE
                viewDinner.visibility = View.VISIBLE
                textBreakfast.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
                textLunch.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
                textDinner.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.orange))
            }

            rlAddPlanButton.setOnClickListener {
                chooseDayDialog(0, "")
                dismiss()
            }

            rlAddCartButton.setOnClickListener {
                findNavController(R.id.frameContainerMain).navigate(R.id.basketScreenFragment)
                dismiss()
            }

            // Set up ViewPager with images
            setUpOnBoardingIndicator()
            currentOnBoardingIndicator(0)
            viewPager.adapter = adapter


        /*    /// set view pager position and value
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                @SuppressLint("SetTextI18n")

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                *//*    if (position == 0) {
                        binding!!.rlNextBtn.visibility= View.VISIBLE
                        binding!!.relLetsGetStarted.visibility= View.GONE
                        binding!!.tvHeading1.text="Plan a Meal"
                        binding!!.tvTextDescriptions.text=getString(R.string.tvDescriptions1)

                    }else if (position==1){
                        binding!!.rlNextBtn.visibility= View.VISIBLE
                        binding!!.relLetsGetStarted.visibility= View.GONE
                        binding!!.tvHeading1.text="Track Food Expenses"
                        binding!!.tvTextDescriptions.text=getString(R.string.tvDescriptions2)
                    }else{
                        binding!!.rlNextBtn.visibility= View.GONE
                        binding!!.relLetsGetStarted.visibility= View.VISIBLE
                        binding!!.tvHeading1.text="Smart Meal Shopping"
                        binding!!.tvTextDescriptions.text=getString(R.string.tvDescription3)
                    }*//*

                    currentOnBoardingIndicator(position)
                }
            })
*/
            /*viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentOnBoardingIndicator(position)
                }
            })
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))*/

            show()
        }
    }

    private fun fetchDataOnLoad() {
        if (BaseApplication.isOnline(this@MainActivity)) {
            fetchRecipeDetailsData()
        } else {
            BaseApplication.alertError(this@MainActivity, ErrorMessage.networkError, false)
        }
    }

    private fun fetchRecipeDetailsData() {
        BaseApplication.showMe(this@MainActivity)
        lifecycleScope.launch {
            mealRoutineViewModel.planRequest({
                BaseApplication.dismissMe()
                handleApiResponse(it)
            }, "q")
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlertFunction(result.message, false)
            else -> showAlertFunction(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, PlanApiResponse::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
                    showData(apiModel.data)
                }
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlertFunction(apiModel.message, true)
                } else {
                    showAlertFunction(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlertFunction(e.message, false)
        }
    }

    private fun showData(data: Data) {
        recipesModel = data.recipes

        Log.d("ffdfdfd", "fffdfdf$recipesModel")

        if (recipesModel!=null){
            adapter = ImageViewPagerAdapter(this@MainActivity, recipesModel!!.Breakfast)
        }
   /*     if (recipesModel != null) {
            fun setupMealAdapter(
                mealRecipes: MutableList<BreakfastModel>?, recyclerView: RecyclerView, type: String): AdapterPlanBreakFast? {
                return if (mealRecipes != null && mealRecipes.isNotEmpty()) {
                    val adapter = AdapterPlanBreakFast(mealRecipes, this@MainActivity, this, type)
                    recyclerView.adapter = adapter
                    adapter
                } else {
                    null
                }
            }


            // Breakfast
            if (recipesModel?.Breakfast != null && recipesModel?.Breakfast?.size!! > 0) {
                breakfastAdapter = setupMealAdapter(recipesModel?.Breakfast, binding!!.rcyBreakFast, "BreakFast")
                binding!!.linearBreakfast.visibility = View.VISIBLE
            } else {
                binding!!.linearBreakfast.visibility = View.GONE
            }

        }*/
    }

    private fun setUpOnBoardingIndicator() {
        val indicator = arrayOfNulls<ImageView>(adapter!!.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(10, 0, 10, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView(this@MainActivity)
            indicator[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.default_dot
                )
            )
            indicator[i]!!.layoutParams = layoutParams
            layOnBoardingIndicator.addView(indicator[i])
        }
    }

    private fun currentOnBoardingIndicator(index: Int) {
        val childCount: Int = layOnBoardingIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = layOnBoardingIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.selected_dot
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.default_dot
                    )
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun chooseDayDialog(position: Int?, typeAdapter: String?) {
        val dialogChooseDay: Dialog = Dialog(this@MainActivity)
        dialogChooseDay.setContentView(R.layout.alert_dialog_choose_day)
        dialogChooseDay.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogChooseDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rcyChooseDaySch = dialogChooseDay.findViewById<RecyclerView>(R.id.rcyChooseDaySch)
        tvWeekRange = dialogChooseDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseDay.show()
        dialogChooseDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        showWeekDates()
        dataList.clear()
        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        for (day in daysOfWeek) {
            val data = DataModel().apply {
                title = day
                isOpen = false
                type = "CookingSchedule"
                date = ""
            }
            dataList.add(data)
        }

        rcyChooseDaySch!!.adapter = ChooseDayAdapter(dataList, this@MainActivity)

        rlDoneBtn.setOnClickListener {

            var status = false
            for (it in dataList) {
                if (it.isOpen) {
                    status = true
                    break // Exit the loop early
                }
            }
            if (status){
                chooseDayMealTypeDialog(position,typeAdapter)
                dialogChooseDay.dismiss()
            }else{
                BaseApplication.alertError(this@MainActivity, ErrorMessage.weekNameError, false)
            }
        }

        btnPrevious.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, -1) // Move to next week
            currentDate = calendar.time
            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }

        btnNext.setOnClickListener {
            // Simulate clicking the "Next" button to move to the next week
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Move to next week
            currentDate = calendar.time
            // Display next week dates
            println("\nAfter clicking 'Next':")
            showWeekDates()
        }
    }

    private fun chooseDayMealTypeDialog(position: Int?, typeAdapter: String?) {
        val dialogChooseMealDay: Dialog = Dialog(this@MainActivity)
        dialogChooseMealDay.setContentView(R.layout.alert_dialog_choose_day_meal_type)
        dialogChooseMealDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseMealDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlDoneBtn = dialogChooseMealDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        // button event listener
        val tvBreakfast = dialogChooseMealDay.findViewById<TextView>(R.id.tvBreakfast)
        val tvLunch = dialogChooseMealDay.findViewById<TextView>(R.id.tvLunch)
        val tvDinner = dialogChooseMealDay.findViewById<TextView>(R.id.tvDinner)
        val tvSnacks = dialogChooseMealDay.findViewById<TextView>(R.id.tvSnacks)
        val tvTeatime = dialogChooseMealDay.findViewById<TextView>(R.id.tvTeatime)
        dialogChooseMealDay.show()
        dialogChooseMealDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        var type = ""

        fun updateSelection(selectedType: String, selectedView: TextView, allViews: List<TextView>) {
            type = selectedType
            allViews.forEach { view ->
                val drawable = if (view == selectedView) R.drawable.radio_select_icon else R.drawable.radio_unselect_icon
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
            }
        }

        val allViews = listOf(tvBreakfast, tvLunch, tvDinner, tvSnacks, tvTeatime)

        tvBreakfast.setOnClickListener {
            updateSelection("Breakfast", tvBreakfast, allViews)
        }

        tvLunch.setOnClickListener {
            updateSelection("Lunch", tvLunch, allViews)
        }

        tvDinner.setOnClickListener {
            updateSelection("Dinner", tvDinner, allViews)
        }

        tvSnacks.setOnClickListener {
            updateSelection("Snack", tvSnacks, allViews)
        }

        tvTeatime.setOnClickListener {
            updateSelection("Teatime", tvTeatime, allViews)
        }


        rlDoneBtn.setOnClickListener {
            if (BaseApplication.isOnline(this@MainActivity)) {
                if (type.equals("",true)){
                    BaseApplication.alertError(this@MainActivity, ErrorMessage.mealTypeError, false)
                }else{
                    addToPlan(dialogChooseMealDay,type,position,typeAdapter)
                }
            } else {
                BaseApplication.alertError(this@MainActivity, ErrorMessage.networkError, false)
            }
        }
    }

    private fun addToPlan(dialogChooseMealDay: Dialog, selectType: String, position: Int?, typeAdapter: String?) {
        // Map the type to the corresponding list and adapter
      /*  val (mealList, adapter) = when (typeAdapter) {
            "BreakFast" -> recipesModel?.Breakfast to breakfastAdapter
            "Lunch" -> recipesModel?.Lunch to lunchAdapter
            "Dinner" -> recipesModel?.Dinner to dinnerAdapter
            "Snack" -> recipesModel?.Dinner to snackesAdapter
            "TeaTime" -> recipesModel?.Dinner to teaTimeAdapter
            else -> null to null
        }

        // Create a JsonObject for the main JSON structure
        val jsonObject = JsonObject()

        // Safely get the item and position
        val item = mealList?.get(position!!)
        if (item != null) {
            if (item.recipe?.uri!=null){
                jsonObject.addProperty("type", selectType)
                jsonObject.addProperty("uri", item.recipe.uri)
                // Create a JsonArray for ingredients
                val jsonArray = JsonArray()
                val latestList=getDaysBetween(startDate, endDate)
                for (i in dataList.indices) {
                    val data=DataModel()
                    data.isOpen=dataList[i].isOpen
                    data.title=dataList[i].title
                    data.date=latestList[i].date
                    dataList[i] = data
                }
                // Iterate through the ingredients and add them to the array if status is true
                dataList.forEach { data ->
                    if (data.isOpen) {
                        // Create a JsonObject for each ingredient
                        val ingredientObject = JsonObject()
                        ingredientObject.addProperty("date", data.date)

                        ingredientObject.addProperty("day", data.title)
                        // Add the ingredient object to the array
                        jsonArray.add(ingredientObject)
                    }
                }

                // Add the ingredients array to the main JSON object
                jsonObject.add("slot", jsonArray)
            }
        }

        BaseApplication.showMe(this@MainActivity)
        lifecycleScope.launch {
            mealRoutineViewModel.recipeAddToPlanRequest({
                BaseApplication.dismissMe()
                handleApiAddToPlanResponse(it,dialogChooseMealDay)
            }, jsonObject)
        }*/
    }

    private fun handleApiAddToPlanResponse(
        result: NetworkResult<String>,
        dialogChooseMealDay: Dialog
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessAddToPlanResponse(result.data.toString(),dialogChooseMealDay)
            is NetworkResult.Error -> showAlertFunction(result.message, false)
            else -> showAlertFunction(result.message, false)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessAddToPlanResponse(data: String, dialogChooseMealDay: Dialog) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                dataList.clear()
                dialogChooseMealDay.dismiss()
                Toast.makeText(this@MainActivity,apiModel.message, Toast.LENGTH_LONG).show()
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlertFunction(apiModel.message, true)
                } else {
                    showAlertFunction(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlertFunction(e.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showWeekDates() {
        Log.d("currentDate :- ", "******$currentDate")
        val (startDate, endDate) = getWeekDates(currentDate)
        this.startDate = startDate
        this.endDate = endDate

        println("Week Start Date: ${formatDate(startDate.toString())}")
        println("Week End Date: ${formatDate(endDate.toString())}")

        // Get all dates between startDate and endDate
        val daysBetween = getDaysBetween(startDate, endDate)

        // Print the dates
        println("Days between ${startDate} and ${endDate}:")
        daysBetween.forEach { println(it) }
        tvWeekRange?.text = "" + formatDate(startDate.toString()) + "-" + formatDate(endDate.toString())

    }


    private fun getDaysBetween(startDate: Date, endDate: Date): MutableList<DateModel> {
        val dateList = mutableListOf<DateModel>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format for the date
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Format for the day name (e.g., Monday)

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            val date = dateFormat.format(calendar.time)  // Get the formatted date (yyyy-MM-dd)
            val dayName = dayFormat.format(calendar.time)  // Get the day name (Monday, Tuesday, etc.)

            val localDate= DateModel()
            localDate.day=dayName
            localDate.date=date
            // Combine both the day name and the date
//            dateList.add("$dayName, $date")
            dateList.add(localDate)


            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dateList
    }

    private fun getWeekDates(currentDate: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        // Set the calendar to the start of the week (Monday)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time

        // Set the calendar to the end of the week (Saturday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time
        return Pair(startOfWeek, endOfWeek)
    }

    /// use switch case to redirection or handle click event
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.llHome -> {
                binding!!.cardViewAddRecipe.visibility = View.GONE
                findNavController(R.id.frameContainerMain).navigate(R.id.homeFragment)
            }

            R.id.llSearch -> {
                binding!!.cardViewAddRecipe.visibility = View.GONE
                val bundle = Bundle().apply {
                    putString("ClickedUrl","")
                }
                findNavController(R.id.frameContainerMain).navigate(R.id.searchFragment,bundle)
            }

            R.id.llAddRecipe -> {
                changeBottom("addRecipe")
                binding!!.cardViewAddRecipe.visibility = View.VISIBLE
//                findNavController(R.id.frameContainerMain).navigate(R.id.addRecipeFragment)
            }

            R.id.llPlan -> {
                binding!!.cardViewAddRecipe.visibility = View.GONE
                findNavController(R.id.frameContainerMain).navigate(R.id.planFragment)
            }

            R.id.llCooked -> {
                binding!!.cardViewAddRecipe.visibility = View.GONE
                findNavController(R.id.frameContainerMain).navigate(R.id.cookedFragment)
            }

            R.id.relAddRecipeWeb -> {
                addRecipeFromWeb()
            }

            R.id.relCreateNewRecipe -> {
                binding!!.cardViewAddRecipe.visibility = View.GONE
                val bundle = Bundle().apply {
                    putString("name","")
                }
                findNavController(R.id.frameContainerMain).navigate(R.id.createRecipeFragment,bundle)
            }

            R.id.relRecipeImage->{
                findNavController(R.id.frameContainerMain).navigate(R.id.createRecipeImageFragment)
                binding!!.cardViewAddRecipe.visibility = View.GONE
            }
        }
    }

    fun mealRoutineSelectApi(onResult: (MutableList<MealRoutineModelData>) -> Unit) {
        // Show the loading indicator
        BaseApplication.showMe(this)

        // Launch the coroutine to perform the API call
        lifecycleScope.launch {
            mealRoutineViewModel.userPreferencesApi { networkResult ->
                // Dismiss the loading indicator
                BaseApplication.dismissMe()

                val mealRoutineList = mutableListOf<MealRoutineModelData>()

                when (networkResult) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val bodyModel = gson.fromJson(networkResult.data, GetUserPreference::class.java)
                            if (bodyModel.code == 200 && bodyModel.success) {
                                mealRoutineList.addAll(bodyModel.data.mealroutine)
                            } else {
                                // Handle specific error cases
                                if (bodyModel.code == ErrorMessage.code) {
                                    showAlertFunction(bodyModel.message, true)
                                } else {
                                    showAlertFunction(bodyModel.message, false)
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("MealRoutine@@", "message:---" + e.message)
                        }
                    }

                    is NetworkResult.Error -> {
                        showAlertFunction(networkResult.message, false)
                    }

                    else -> {
                        showAlertFunction(networkResult.message, false)
                    }
                }

                // Return the result through the callback
                onResult(mealRoutineList)
            }
        }
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(this, message, status)
    }

    private fun getFcmToken() {
        lifecycleScope.launch {
            Log.d("Token ","******"+BaseApplication.fetchFcmToken())
        }
    }
}

package com.mykameal.planner.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.google.gson.Gson
import com.mykameal.planner.R
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.commonworkutils.CommonWorkUtils
import com.mykameal.planner.databinding.ActivityMainBinding
import com.mykameal.planner.fragment.commonfragmentscreen.commonModel.GetUserPreference
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.viewmodel.MealRoutineViewModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnClickListener {

    var binding: ActivityMainBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils

    private lateinit var mealRoutineViewModel: MealRoutineViewModel


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)
        mealRoutineViewModel = ViewModelProvider(this@MainActivity)[MealRoutineViewModel::class.java]


        commonWorkUtils = CommonWorkUtils(this)

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
        dialogWeb.show()
        dialogWeb.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlSearchRecipe.setOnClickListener {
            if (etPasteURl.text.toString().isEmpty()) {
                commonWorkUtils.alertDialog(this, ErrorMessage.pasteUrl, false)
            } else {
                dialogWeb.dismiss()
            }
        }
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
                findNavController(R.id.frameContainerMain).navigate(R.id.searchFragment)
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
                findNavController(R.id.frameContainerMain).navigate(R.id.createRecipeFragment)
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

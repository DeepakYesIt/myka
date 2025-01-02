package com.yesitlabs.mykaapp.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.commonworkutils.CommonWorkUtils
import com.yesitlabs.mykaapp.databinding.ActivityMainBinding
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnClickListener {

    var binding: ActivityMainBinding? = null
    private lateinit var commonWorkUtils: CommonWorkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        commonWorkUtils = CommonWorkUtils(this)

        binding!!.llHomeIndicator.visibility = View.VISIBLE
        binding!!.llSearchIndicator.visibility = View.INVISIBLE
        binding!!.llAddRecipeIndicator.visibility = View.INVISIBLE
        binding!!.llPlanIndicator.visibility = View.INVISIBLE
        binding!!.llCookedIndicator.visibility = View.INVISIBLE

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

        /// using function for find destination graph
        startDestination()
    }

    private fun startDestination() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.frameContainerMain) as NavHostFragment
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
        }
    }
}
package com.mykameal.planner.fragment.mainfragment.profilesetting.preferencesScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.yesitlabs.mykaapp.OnItemClickListener
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.PreferencesAdapter
import com.yesitlabs.mykaapp.databinding.FragmentPreferencesBinding
import com.yesitlabs.mykaapp.model.DataPreferencesModel

import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.PreferencesAdapter
import com.mykameal.planner.databinding.FragmentPreferencesBinding
import com.mykameal.planner.model.DataModel

class PreferencesFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentPreferencesBinding? = null
    private lateinit var sessionManagement: SessionManagement
    private var preferenceAdapter: PreferencesAdapter? = null
    private var screenType: String? = null
    private val dataList = ArrayList<DataPreferencesModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false)

        (activity as MainActivity?)?.apply {
            binding?.llIndicator?.visibility = View.GONE
            binding?.llBottomNavigation?.visibility = View.GONE
        }

        sessionManagement = SessionManagement(requireActivity())
        screenType = sessionManagement.getCookingFor()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {
        populateDataList(screenType)

        binding!!.imgBackPreferences.setOnClickListener {
            findNavController().navigateUp()
        }

        preferenceAdapter = PreferencesAdapter(dataList, requireActivity(), this)
        binding!!.recyPreferences.adapter = preferenceAdapter

    }

    private fun populateDataList(type: String?) {

        val dataMap = when (type) {
            "Myself" -> listOf(
                "Body Goals",
                "Dietary Restrictions",
                "Favorite Cuisines",
                "Disliked Ingredient",
                "Allergies",
                "Meal Routine",
                "Cooking Frequency",
                "Spending on Groceries",
                "Eating Out",
                "Reason Take Away"
            )
            "MyPartner" -> listOf(
                "Partner Info",
                "Body Goals",
                "Dietary Restrictions",
                "Disliked Ingredient",
                "Allergies",
                "Favorite Cuisines",
                "Meal Prep Days",
                "Cooking Frequency",
                "Spending on Groceries",
                "Eating Out",
                "Reason Take Away"
            )
            else -> listOf(
                "Family Members",
                "Body Goals",
                "Dietary Restrictions",
                "Disliked Ingredient",
                "Allergies",
                "Favorite Cuisines",
                "Family Meal Preferences",
                "Cooking Frequency",
                "Spending on Groceries",
                "Eating Out",
                "Reason Take Away"
            )
        }

        dataList.clear()
        dataMap.forEach {
            dataList.add(DataPreferencesModel(it, false, type))
        }
    }

    override fun itemClick(position: Int?, cookingType: String?, tittleName: String?) {
        sessionManagement.setCookingScreen("Profile")
        sessionManagement.setCookingFor(cookingType ?: "")
        val navigationMap = mapOf(
            "Body Goals" to R.id.bodyGoalsFragment,
            "Dietary Restrictions" to R.id.dietaryRestrictionsFragment,
            "Favorite Cuisines" to R.id.favouriteCuisinesFragment,
            "Disliked Ingredient" to R.id.ingredientDislikesFragment,
            "Allergies" to R.id.allergensIngredientsFragment,
            "Meal Routine" to R.id.mealRoutineFragment,
            "Cooking Frequency" to R.id.cookingFrequencyFragment,
            "Spending on Groceries" to R.id.spendingOnGroceriesFragment,
            "Eating Out" to R.id.eatingOutFragment,
            "Reason Take Away" to R.id.reasonsForTakeAwayFragment,
            "Partner Info" to R.id.partnerInfoDetailsFragment,
            "Meal Prep Days" to R.id.mealRoutineFragment,
            "Family Members" to R.id.familyMembersFragment,
            "Family Meal Preferences" to R.id.mealRoutineFragment
        )
        navigationMap[tittleName]?.let {
            findNavController().navigate(it)
        }
    }
}
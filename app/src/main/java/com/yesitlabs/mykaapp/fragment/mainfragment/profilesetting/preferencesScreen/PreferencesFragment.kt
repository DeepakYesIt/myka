package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting.preferencesScreen

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
import com.yesitlabs.mykaapp.model.DataModel

class PreferencesFragment : Fragment(),OnItemClickListener {

    private var binding:FragmentPreferencesBinding?=null
    private lateinit var sessionManagement: SessionManagement
    private var preferenceAdapter: PreferencesAdapter? = null
    private var screenType:String?=null
    val dataList = ArrayList<DataModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentPreferencesBinding.inflate(layoutInflater,container,false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        sessionManagement = SessionManagement(requireActivity())

        screenType=sessionManagement.getCookingFor()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().navigateUp()

            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        if (screenType=="Myself"){
            mySelfModel()
        }else if (screenType=="MyPartner"){
            myPartnerModel()
        }else{
            myFamilyModel()
        }

        binding!!.imgBackPreferences.setOnClickListener{
            findNavController().navigateUp()
        }

    }

    private fun mySelfModel() {
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

        data1.title = "Body Goals"
        data1.isOpen = false
        data1.type = "Myself"

        data2.title = "Dietary Restrictions"
        data2.isOpen = false
        data2.type = "Myself"

        data3.title = "Favorite Cuisines"
        data3.isOpen = false
        data3.type = "Myself"

        data4.title = "Disliked Ingredient"
        data4.isOpen = false
        data4.type = "Myself"

        data5.title = "Allergies"
        data5.isOpen = false
        data5.type = "Myself"

        data6.title = "Meal Routine"
        data6.isOpen = false
        data6.type = "Myself"

        data7.title = "Cooking Frequency"
        data7.isOpen = false
        data7.type = "Myself"

        data8.title = "Spending on Groceries"
        data8.isOpen = false
        data8.type = "Myself"

        data9.title = "Eating Out"
        data9.isOpen = false
        data9.type = "Myself"

        data10.title = "Reason Take Away"
        data10.isOpen = false
        data10.type = "Myself"

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

        preferenceAdapter = PreferencesAdapter(dataList, requireActivity(),this)
        binding!!.recyPreferences.adapter = preferenceAdapter

    }

    private fun myPartnerModel() {
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
        val data11 = DataModel()

        data1.title = "Partner Info"
        data1.isOpen = false
        data1.type = "MyPartner"

        data2.title = "Body Goals"
        data2.isOpen = false
        data2.type = "MyPartner"

        data3.title = "Dietary Restrictions"
        data3.isOpen = false
        data3.type = "MyPartner"

        data4.title = "Disliked Ingredient"
        data4.isOpen = false
        data4.type = "MyPartner"

        data5.title = "Allergies"
        data5.isOpen = false
        data5.type = "MyPartner"

        data6.title = "Favorite Cuisines"
        data6.isOpen = false
        data6.type = "MyPartner"

        data7.title = " Meal Prep Days"
        data7.isOpen = false
        data7.type = "MyPartner"

        data8.title = "Cooking Frequency"
        data8.isOpen = false
        data8.type = "MyPartner"

        data9.title = " Spending on Groceries"
        data9.isOpen = false
        data9.type = "MyPartner"

        data10.title = "Eating Out"
        data10.isOpen = false
        data10.type = "MyPartner"

        data11.title = "Reason Take Away"
        data11.isOpen = false
        data11.type = "MyPartner"

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
        dataList.add(data11)

        preferenceAdapter = PreferencesAdapter(dataList, requireActivity(),this)
        binding!!.recyPreferences.adapter = preferenceAdapter

    }

    private fun myFamilyModel() {
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
        val data11 = DataModel()

        data1.title = "Family Members"
        data1.isOpen = false
        data1.type = "MyFamily"

        data2.title = "Body Goals"
        data2.isOpen = false
        data2.type = "MyFamily"

        data3.title = "Dietary Restrictions"
        data3.isOpen = false
        data3.type = "MyFamily"

        data4.title = "Disliked Ingredient"
        data4.isOpen = false
        data4.type = "MyFamily"

        data5.title = "Allergies"
        data5.isOpen = false
        data5.type = "MyFamily"

        data6.title = "Favorite Cuisines"
        data6.isOpen = false
        data6.type = "MyFamily"

        data7.title = "Family Meal Preferences"
        data7.isOpen = false
        data7.type = "MyFamily"

        data8.title = "Cooking Frequency"
        data8.isOpen = false
        data8.type = "MyFamily"

        data9.title = " Spending on Groceries"
        data9.isOpen = false
        data9.type = "MyFamily"

        data10.title = "Eating Out"
        data10.isOpen = false
        data10.type = "MyFamily"

        data11.title = "Reason Take Away"
        data11.isOpen = false
        data11.type = "MyFamily"

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

        preferenceAdapter = PreferencesAdapter(dataList, requireActivity(),this)
        binding!!.recyPreferences.adapter = preferenceAdapter
    }

    override fun itemClick(position: Int?, cookingType: String?, tittleName: String?) {
        sessionManagement.setCookingScreen("Profile")
        sessionManagement.setCookingFor(cookingType.toString())
        if (cookingType=="Myself"){
            if (tittleName=="Body Goals"){
                findNavController().navigate(R.id.bodyGoalsFragment)
            }

            if (tittleName=="Dietary Restrictions"){
                findNavController().navigate(R.id.dietaryRestrictionsFragment)
            }

            if (tittleName=="Favorite Cuisines"){
                findNavController().navigate(R.id.favouriteCuisinesFragment)
            }

            if (tittleName=="Disliked Ingredient"){
                findNavController().navigate(R.id.ingredientDislikesFragment)
            }

            if (tittleName=="Allergies"){
                findNavController().navigate(R.id.allergensIngredientsFragment)
            }

            if (tittleName=="Meal Routine"){
                findNavController().navigate(R.id.mealRoutineFragment)
            }

            if (tittleName=="Cooking Frequency"){
                findNavController().navigate(R.id.cookingFrequencyFragment)
            }

            if (tittleName=="Spending on Groceries"){
                findNavController().navigate(R.id.spendingOnGroceriesFragment)
            }

            if (tittleName=="Eating Out"){
                findNavController().navigate(R.id.eatingOutFragment)
            }

            if (tittleName=="Reason Take Away"){
                findNavController().navigate(R.id.reasonsForTakeAwayFragment)
            }

        }else if (cookingType=="MyPartner"){

            if (tittleName=="Partner Info"){
                findNavController().navigate(R.id.partnerInfoDetailsFragment)
            }

            if (tittleName=="Body Goals"){
                findNavController().navigate(R.id.bodyGoalsFragment)
            }

            if (tittleName=="Dietary Restrictions"){
                findNavController().navigate(R.id.dietaryRestrictionsFragment)
            }

            if (tittleName=="Disliked Ingredient"){
                findNavController().navigate(R.id.ingredientDislikesFragment)
            }

            if (tittleName=="Allergies"){
                findNavController().navigate(R.id.allergensIngredientsFragment)
            }

            if (tittleName=="Favorite Cuisines"){
                findNavController().navigate(R.id.favouriteCuisinesFragment)
            }

            if (tittleName=="Meal Prep Days"){
                findNavController().navigate(R.id.mealRoutineFragment)
            }

            if (tittleName=="Cooking Frequency"){
                findNavController().navigate(R.id.cookingFrequencyFragment)
            }

            if (tittleName=="Spending on Groceries"){
                findNavController().navigate(R.id.spendingOnGroceriesFragment)
            }

            if (tittleName=="Eating Out"){
                findNavController().navigate(R.id.eatingOutFragment)
            }

            if (tittleName=="Reason Take Away"){
                findNavController().navigate(R.id.reasonsForTakeAwayFragment)
            }

        }else{
            if (tittleName=="Family Members"){
                findNavController().navigate(R.id.familyMembersFragment)
            }

            if (tittleName=="Body Goals"){
                findNavController().navigate(R.id.bodyGoalsFragment)
            }

            if (tittleName=="Dietary Restrictions"){
                findNavController().navigate(R.id.dietaryRestrictionsFragment)
            }

            if (tittleName=="Disliked Ingredient"){
                findNavController().navigate(R.id.ingredientDislikesFragment)
            }

            if (tittleName=="Allergies"){
                findNavController().navigate(R.id.allergensIngredientsFragment)
            }

            if (tittleName=="Favorite Cuisines"){
                findNavController().navigate(R.id.favouriteCuisinesFragment)
            }

            if (tittleName=="Family Meal Preferences"){
                findNavController().navigate(R.id.mealRoutineFragment)
            }

            if (tittleName=="Cooking Frequency"){
                findNavController().navigate(R.id.cookingFrequencyFragment)
            }

            if (tittleName=="Spending on Groceries"){
                findNavController().navigate(R.id.spendingOnGroceriesFragment)
            }

            if (tittleName=="Eating Out"){
                findNavController().navigate(R.id.eatingOutFragment)
            }

            if (tittleName=="Reason Take Away"){
                findNavController().navigate(R.id.reasonsForTakeAwayFragment)
            }
        }


    }

}
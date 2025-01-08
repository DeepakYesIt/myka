package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.commonModel

import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model.BodyGoalModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData

data class GetUserPreference(
    val code: Int,
    val `data`: GetUserPreferenceData,
    val message: String,
    val success: Boolean
)

data class GetUserPreferenceData(
    val allergesingredient: List<DietaryRestrictionsModelData>,
    val bodygoal: List<BodyGoalModelData>,
    val cookingfrequency: List<BodyGoalModelData>,
    val cookingschedule: List<Cookingschedule>,
    val dietaryrestriction: List<DietaryRestrictionsModelData>,
    val eatingout: List<BodyGoalModelData>,
    val familyDetail: FamilyDetail,
    val favouritcuisine: List<DietaryRestrictionsModelData>,
    val grocereisExpenses: GrocereisExpenses,
    val ingredientdislike: List<DietaryRestrictionsModelData>,
    val mealroutine: List<MealRoutineModelData>,
    val partnerDetail: PartnerDetail,
    val takeawayreason: List<BodyGoalModelData>
)


/*
data class GetUserPreferenceData(
    val allergesingredient: List<Allergesingredient>,
    val bodygoal: List<BodyGoalModelData>,
    val cookingfrequency: List<Cookingfrequency>,
    val cookingschedule: List<Cookingschedule>,
    val dietaryrestriction: List<DietaryRestrictionsModelData>,
    val eatingout: List<Eatingout>,
    val familyDetail: Any,
    val favouritcuisine: List<Favouritcuisine>,
    val grocereisExpenses: GrocereisExpenses,
    val ingredientdislike: List<Ingredientdislike>,
    val mealroutine: List<Mealroutine>,
    val partnerDetail: PartnerDetail,
    val takeawayreason: List<Takeawayreason>
)*/

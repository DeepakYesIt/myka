package com.mykameal.planner.basedata

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManagement(var context: Context) {
    var dialog: Dialog? = null
    private var editor: SharedPreferences.Editor? = null
    private var pref: SharedPreferences? = null
    private val gson = Gson()


    init {
        pref = context.getSharedPreferences(AppConstant.LOGIN_SESSION, Context.MODE_PRIVATE)
        editor = pref?.edit()
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboardingCompleted"
        private const val isLocationAllowed = "isLocationAllowed"
        private const val isNotificationAllowed = "isNotificationAllowed"
        private const val isLastScreen = "isLastScreen"
        private const val isPersonalInformation = "isPersonalInformation"
        private const val isProfessionalInformation = "isProfessionalInformation"
        private const val phoneNumber = "phoneNumber"
        private const val isIdVerification = "isIdVerification"
    }


    fun getLoginSession(): Boolean {
        return pref!!.getBoolean(AppConstant.loginSession, false)
    }



    fun setLoginSession(session: Boolean?) {
        editor!!.putBoolean(AppConstant.loginSession, session!!)
        editor!!.commit()
    }

    fun setUserName(name: String) {
        editor!!.putString(AppConstant.NAME, name)
        editor!!.commit()
    }

    fun getUserName(): String? {
        return pref?.getString(AppConstant.NAME, "")
    }

    fun setGender(gender: String) {
        editor!!.putString(AppConstant.Gender, gender)
        editor!!.commit()
    }

    fun getGender(): String? {
        return pref?.getString(AppConstant.Gender, "")
    }

    fun setBodyGoal(bogyGoal: String) {
        editor!!.putString(AppConstant.BodyGoal, bogyGoal)
        editor!!.commit()
    }

    fun getBodyGoal(): String? {
        return pref?.getString(AppConstant.BodyGoal, "")
    }

    fun setPartnerName(partnerName: String) {
        editor!!.putString(AppConstant.PartnerName, partnerName)
        editor!!.commit()
    }

    fun getPartnerName(): String? {
        return pref?.getString(AppConstant.PartnerName, "")
    }

    fun setPartnerAge(partnerAge: String) {
        editor!!.putString(AppConstant.PartnerAge, partnerAge)
        editor!!.commit()
    }

    fun getPartnerAge(): String? {
        return pref?.getString(AppConstant.PartnerAge, "")
    }

    fun setPartnerGender(partnerGender: String) {
        editor!!.putString(AppConstant.PartnerGender, partnerGender)
        editor!!.commit()
    }

    fun getPartnerGender(): String? {
        return pref?.getString(AppConstant.PartnerGender, "")
    }

    fun setFamilyMemName(familyMemName: String) {
        editor!!.putString(AppConstant.FamilyMemName, familyMemName)
        editor!!.commit()
    }

    fun getFamilyMemName(): String? {
        return pref?.getString(AppConstant.FamilyMemName, "")
    }

    fun setFamilyMemAge(familyMemAge: String) {
        editor!!.putString(AppConstant.FamilyMemAge, familyMemAge)
        editor!!.commit()
    }

    fun getFamilyMemAge(): String? {
        return pref?.getString(AppConstant.FamilyMemAge, "")
    }

    fun setFamilyStatus(familyStatus: String) {
        editor!!.putString(AppConstant.FamilyStatus, familyStatus)
        editor!!.commit()
    }

    fun getFamilyStatus(): String? {
        return pref?.getString(AppConstant.FamilyStatus, "")
    }

    fun setDietaryRestrictionList(dietaryList: MutableList<String>?) {
        val json = gson.toJson(dietaryList)
        editor!!.putString(AppConstant.DietaryRestriction, json)
        editor!!.commit()
    }

    fun getDietaryRestrictionList(): MutableList<String>? {
        val json = pref?.getString(AppConstant.DietaryRestriction, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun setFavouriteCuisineList(favouriteList: MutableList<String>?) {
        val json = gson.toJson(favouriteList)
        editor!!.putString(AppConstant.FavouriteCuisine, json)
        editor!!.commit()
    }

    fun getFavouriteCuisineList(): MutableList<String>? {
        val json = pref?.getString(AppConstant.FavouriteCuisine, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun setDislikeIngredientList(dislikeIngList: MutableList<String>?) {
        val json = gson.toJson(dislikeIngList)
        editor!!.putString(AppConstant.DislikeIngredient, json)
        editor!!.commit()
    }

    fun getDislikeIngredientList(): MutableList<String>? {
        val json = pref?.getString(AppConstant.DislikeIngredient, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun setAllergenIngredientList(allergenIngList: MutableList<String>?) {
        val json = gson.toJson(allergenIngList)
        editor!!.putString(AppConstant.AllergenIngredient, json)
        editor!!.commit()
    }

    fun getAllergenIngredientList(): MutableList<String>? {
        val json = pref?.getString(AppConstant.AllergenIngredient, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun setMealRoutineList(mealRoutineList: MutableList<String>?) {
        val json = gson.toJson(mealRoutineList)
        editor!!.putString(AppConstant.MealRoutine, json)
        editor!!.commit()
    }

    fun getMealRoutineList(): MutableList<String>? {
        val json = pref?.getString(AppConstant.MealRoutine, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }


    fun setCookingFrequency(cookingFrequency: String) {
        editor!!.putString(AppConstant.CookingFrequency, cookingFrequency)
        editor!!.commit()
    }

    fun getCookingFrequency(): String? {
        return pref?.getString(AppConstant.CookingFrequency, "")
    }

    fun setSpendingAmount(spendingAmount: String) {
        editor!!.putString(AppConstant.SpendingAmount, spendingAmount)
        editor!!.commit()
    }

    fun getSpendingAmount(): String? {
        return pref?.getString(AppConstant.SpendingAmount, "")
    }

    fun setSpendingDuration(spendingDuration: String) {
        editor!!.putString(AppConstant.SpendingDuration, spendingDuration)
        editor!!.commit()
    }

    fun getSpendingDuration(): String? {
        return pref?.getString(AppConstant.SpendingDuration, "")
    }

    fun setEatingOut(eatingOut: String) {
        editor!!.putString(AppConstant.EatingOut, eatingOut)
        editor!!.commit()
    }

    fun getEatingOut(): String? {
        return pref?.getString(AppConstant.ReasonForTakeAway, "")
    }

    fun setReasonTakeAway(reasonAway: String) {
        editor!!.putString(AppConstant.ReasonForTakeAway, reasonAway)
        editor!!.commit()
    }

    fun getReasonTakeAway(): String? {
        return pref?.getString(AppConstant.ReasonForTakeAway, "")
    }



    fun setEmail(email: String) {
        editor!!.putString(AppConstant.EMAIL, email)
        editor!!.commit()
    }

    fun getEmail(): String? {
        return pref?.getString(AppConstant.EMAIL, "")
    }

    fun setPhone(phone: String) {
        editor!!.putString(AppConstant.PHONE, phone)
        editor!!.commit()
    }

    fun getPhone(): String? {
        return pref?.getString(AppConstant.PHONE, "")
    }

    fun setImage(name: String) {
        editor!!.putString(AppConstant.Image, name)
        editor!!.commit()
    }

    fun getImage(): String? {
        return pref?.getString(AppConstant.Image, "")
    }

    fun setId(id: String) {
        editor!!.putString(AppConstant.Id, id)
        editor!!.commit()
    }

    fun getId(): String? {
        return pref?.getString(AppConstant.Id, "")
    }

    fun setAuthToken(name: String) {
        editor!!.putString(AppConstant.authToken, name)
        editor!!.commit()
    }

    fun getAuthToken(): String? {
        return pref?.getString(AppConstant.authToken, "")
    }

    fun setCookingFor(cookingFor: String) {
        editor!!.putString(AppConstant.cookingFor, cookingFor)
        editor!!.commit()
    }

    fun getCookingFor(): String? {
        return pref?.getString(AppConstant.cookingFor, "")
    }

    fun setCookingScreen(cookingFor: String) {
        editor!!.putString(AppConstant.cookingScreen, cookingFor)
        editor!!.commit()
    }

    fun getCookingScreen(): String? {
        return pref?.getString(AppConstant.cookingScreen, "")
    }

    fun sessionClear(){
        editor?.apply()
        editor?.clear()
    }


}
package com.yesitlabs.mykaapp.basedata

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import com.yesitlabs.mykaapp.basedata.AppConstant

class SessionManagement(var context: Context) {
    var dialog: Dialog? = null
    private var editor: SharedPreferences.Editor? = null
    private var pref: SharedPreferences? = null

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

    fun setEmail(name: String) {
        editor!!.putString(AppConstant.EMAIL, name)
        editor!!.commit()
    }

    fun getEmail(): String? {
        return pref?.getString(AppConstant.EMAIL, "")
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


    fun sessionClear(){
        editor?.apply()
        editor?.clear()
    }


}
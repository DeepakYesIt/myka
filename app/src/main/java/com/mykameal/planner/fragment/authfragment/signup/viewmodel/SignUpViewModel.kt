package com.mykameal.planner.fragment.authfragment.signup.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun signUpModel(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String, password: String){
        repository.signUpModel({ successCallback(it) }, emailOrPhone,password)
    }

    suspend fun socialLogin(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String?,
        socialId: String?,
        userName: String?,
        userGender: String?,
        bodyGoal: String?,
        cookingFrequency: String?,
        eatingOut: String?,
        takeAway: String?,
        cookingForType: String?,
        partnerName: String?,
        partnerAge: String?,
        partnerGender: String?,
        familyMemberName: String?,
        familyMemberAge: String?,
        childFriendlyMeals: String?,
        mealRoutineId: List<String>?,
        spendingAmount: String?,
        duration: String?,
        dietaryId: List<String>?,favourite:List<String>?, allergies:List<String>?,
        dislikeIngredients: List<String>?,
        deviceType: String?,
        fcmToken: String?
    ) {
        repository.socialLogin({ successCallback(it) }, emailOrPhone, socialId, userName, userGender, bodyGoal, cookingFrequency,eatingOut, takeAway,
            cookingForType, partnerName,partnerAge, partnerGender, familyMemberName, familyMemberAge, childFriendlyMeals, mealRoutineId, spendingAmount,
            duration, dietaryId,favourite, allergies, dislikeIngredients, deviceType, fcmToken
        )
    }

}
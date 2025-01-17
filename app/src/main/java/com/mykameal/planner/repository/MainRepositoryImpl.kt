package com.mykameal.planner.repository

import com.google.gson.JsonObject
import com.mykameal.planner.apiInterface.ApiInterface
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.messageclass.ErrorMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class MainRepositoryImpl  @Inject constructor(private val api: ApiInterface) : MainRepository{

    override suspend fun bogyGoal(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getBogyGoal().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getDietaryRestrictions(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getDietaryRestrictions().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getFavouriteCuisines(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getFavouriteCuisines().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getDislikeIngredients(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getDislikeIngredients().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getAllergensIngredients(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getAllergensIngredients().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getMealRoutine(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getMealRoutine().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getCookingFrequency(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getCookingFrequency().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }



    override suspend fun getEatingOut(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getEatingOut().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun getTakeAwayReason(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getTakeAwayReason().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun signUpModel(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String,
        password: String
    ) {
        try {
            api.userSignUp(emailOrPhone, password).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun otpVerify(
        successCallback: (response: NetworkResult<String>) -> Unit,
        userid: String?, otp: String?,userName:String?,userGender:String?,bodyGoal:String?,cookingFrequency:String?,eatingOut:String?,
        takeAway:String?,cookingForType:String?,partnerName:String?,partnerAge:String?,partnerGender:String?,familyMemberName:String?,
        familyMemberAge:String?,childFriendlyMeals:String?,mealRoutineId:List<String>?,spendingAmount:String?,duration:String?,
        dietaryid:List<String>?,favourite:List<String>?, allergies:List<String>?,dislikeIngredients:List<String>?,deviceType:String?,fcmToken:String?) {
        try {
            api.otpVerify(userid, otp,userName,userGender,bodyGoal,cookingFrequency,eatingOut,takeAway,cookingForType,partnerName,partnerAge,partnerGender,
                familyMemberName,familyMemberAge,childFriendlyMeals,mealRoutineId,spendingAmount,duration,dietaryid,favourite,allergies,dislikeIngredients,
                deviceType,fcmToken).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun forgotPassword(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String
    ) {
        try {
            api.forgotPassword(emailOrPhone).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun resendSignUpModel(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String
    ) {
        try {
            api.resendOtp(emailOrPhone).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun forgotOtpVerify(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String,
        otp:String
    ) {
        try {
            api.forgotOtpVerify(emailOrPhone,otp).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun resendOtp(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String
    ) {
        try {
            api.resendOtp(emailOrPhone).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun resetPassword(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone:String,
        password: String,
        confirmPassword:String
    ) {
        try {
            api.resetPassword(emailOrPhone,password,confirmPassword).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun userLogin(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String,
        password: String,
        deviceType: String,
        fcmToken: String
    ) {
        try {
            api.userLogin(emailOrPhone,password,deviceType,fcmToken).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun socialLogin(
        successCallback: (response: NetworkResult<String>) -> Unit,
        emailOrPhone: String?, socialID: String?,userName:String?,userGender:String?,bodyGoal:String?,cookingFrequency:String?,eatingOut:String?,
        takeAway:String?,cookingForType:String?,partnerName:String?,partnerAge:String?,partnerGender:String?,familyMemberName:String?,
        familyMemberAge:String?,childFriendlyMeals:String?,mealRoutineId:List<String>?,spendingAmount:String?,duration:String?,
        dietaryid:List<String>?,favourite:List<String>?, allergies:List<String>?,dislikeIngredients:List<String>?,deviceType:String?,fcmToken:String?) {
        try {
            api.socialLogin(emailOrPhone, socialID,userName,userGender,bodyGoal,cookingFrequency,eatingOut,takeAway,cookingForType,partnerName,partnerAge,partnerGender,
                familyMemberName,familyMemberAge,childFriendlyMeals,mealRoutineId,spendingAmount,duration,dietaryid,favourite,allergies,dislikeIngredients,
                deviceType,fcmToken).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun updateLocation(
        successCallback: (response: NetworkResult<String>) -> Unit,
        locationStatus: String
    ) {
        try {
            api.updateLocation(locationStatus).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun updateNotification(
        successCallback: (response: NetworkResult<String>) -> Unit,
        notificationStatus: String
    ) {
        try {
            api.updateNotification(notificationStatus).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun privacyPolicy(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getPrivacyPolicy().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun termCondition(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getTermsCondition().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun saveFeedback(
        successCallback: (response: NetworkResult<String>) -> Unit,
        email: String,
        message: String
    ) {
        try {
            api.saveFeedback(email,message).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun userProfileDataApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.userProfileDataApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun userLogOutDataApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.userLogOutDataApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun userDeleteDataApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.userDeleteDataApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun upDateProfileRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        name:String,
        bio:String,
        genderType: String,
        dob: String,
        height: String,
        heightType: String,
        activityLevel: String,
        heightProtein: String,
        calories: String,
        fat: String,
        carbs: String,
        protien: String
    ) {
        try {
            api.userProfileUpdateApi(name,bio,genderType,dob,height,heightType,activityLevel,
                heightProtein,calories,fat,carbs,protien).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun upDateImageNameRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        image: MultipartBody.Part?,
        name: RequestBody
    ) {
        try {
            api.upDateImageNameRequestApi(image,name).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun addCardRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        token: String
    ) {
        try {
            api.addCardRequestApi(token).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun notificationRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        pushNotification: String,
        recipeRecommendations: String,
        productUpdates: String,
        promotionalUpdates: String
    ) {
        try {
            api.notificationRequestApi(pushNotification,recipeRecommendations,productUpdates,promotionalUpdates).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun recipeDetailsRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        url: String
    ) {
        try {
            api.recipeDetailsRequestApi(url).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun recipeReviewRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        url: String,
        msg: String,
        ratingBarcount: String
    ) {
        try {
            api.recipeReviewRequestApi(url,msg,ratingBarcount).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun homeDetailsRequestApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.homeDetailsRequestApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun recipeAddBasketRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        jsonObject: JsonObject
    ) {
        try {
            api.recipeAddBasketRequestApi(jsonObject).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun recipeAddToPlanRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        jsonObject: JsonObject
    ) {
        try {
            api.recipeAddToPlanRequestApi(jsonObject).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun planRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, q: String) {
        try {
            api.planRequestApi(q).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun planDateRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        date: String
    ) {
        try {
            api.planDateRequestApi(date).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun likeUnlikeRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        uri: String,
        likeType: String
    ) {
        try {
            api.likeUnlikeRequestApi(uri,likeType).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun addBasketRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        uri: String,
        quantity: String
    ) {
        try {
            api.addBasketRequestApi(uri,quantity).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getCardAndBankRequestApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getCardAndBankRequestApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun getWalletRequestApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        try {
            api.getWalletRequestApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun deleteCardRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        cardId: String,
        customerId: String
    ) {
        try {
            api.deleteCardRequestApi(cardId,customerId).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun deleteBankRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        stripeAccountId: String
    ) {
        try {
            api.deleteBankRequestApi(stripeAccountId).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun countryRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        url: String
    ) {
        try {
            api.countryRequestApi(url).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun transferAmountRequest(
        successCallback: (response: NetworkResult<String>) -> Unit,
        amount: String,
        destination: String
    ) {
        try {
            api.transferAmountRequest(amount,destination).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun addBankRequestApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        filePartFront: MultipartBody.Part?,
        filePartBack: MultipartBody.Part?,
        filePart: MultipartBody.Part?,
        firstNameBody: RequestBody,
        lastNameBody: RequestBody,
        emailBody: RequestBody,
        phoneBody: RequestBody,
        dobBody: RequestBody,
        personalIdentificationNobody: RequestBody,
        idTypeBody: RequestBody,
        ssnBody: RequestBody,
        addressBody: RequestBody,
        countryBody: RequestBody,
        shortStateNameBody: RequestBody,
        cityBody: RequestBody,
        postalCodeBody: RequestBody,
        bankDocumentTypeBody: RequestBody,
        deviceTypeBody: RequestBody,
        tokenTypeBody: RequestBody,
        stripeTokenBody: RequestBody,
        saveCardBody: RequestBody,
        amountBody: RequestBody,
        paymentTypeBody: RequestBody,
        bankIdBody: RequestBody
    ) {
        try {
            api.addBankRequestApi(filePartFront
                ,filePartBack,filePart,firstNameBody,lastNameBody,emailBody,phoneBody,dobBody
                ,personalIdentificationNobody,idTypeBody,ssnBody,addressBody,countryBody,shortStateNameBody
                ,cityBody,postalCodeBody,bankDocumentTypeBody,deviceTypeBody,tokenTypeBody,stripeTokenBody
                ,saveCardBody,amountBody,paymentTypeBody,bankIdBody).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error(ErrorMessage.apiError))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun userPreferencesApi(
        successCallback: (response: NetworkResult<String>) -> Unit
    ) {
        try {
            api.userPreferencesApi().apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateBodyGoalApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        bodygoal:String?
    ) {
        try {
            api.updateBodyGoalApi(bodygoal).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun updateCookingFrequencyApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        cookingFrequency:String?
    ) {
        try {
            api.updateCookingFrequencyApi(cookingFrequency).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }



    override suspend fun updateAllergiesApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        allergies:List<String>?
    ) {
        try {
            api.updateAllergiesApi(allergies).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun updateReasonTakeAwayApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        takeway:String?
    ) {
        try {
            api.updateReasonTakeAwayApi(takeway).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateEatingOutApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        eatingOut:String?
    ) {
        try {
            api.updateEatingOutApi(eatingOut).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun updatePartnerInfoApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        partnerName:String?,partnerAge:String?,partnerGender:String?
    ) {
        try {
            api.updatePartnerInfoApi(partnerName,partnerAge,partnerGender).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateFamilyInfoApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        familyName:String?,familyAge:String?,status:String?
    ) {
        try {
            api.updateFamilyInfoApi(familyName,familyAge,status).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateSpendingGroceriesApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        spendingAmount:String?,duration:String?) {
        try {
            api.updateSpendingGroceriesApi(spendingAmount,duration).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateMealRoutineApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        mealRoutineId:List<String>?
    ) {
        try {
            api.updateMealRoutineApi(mealRoutineId).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun updateDietaryApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        dietaryId:List<String>?
    ) {
        try {
            api.updateDietaryApi(dietaryId).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateFavouriteApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        favouriteId:List<String>?
    ) {
        try {
            api.updateFavouriteApi(favouriteId).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


    override suspend fun updateDislikedIngredientsApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        dislikedId:List<String>?
    ) {
        try {
            api.updateDislikedIngredientsApi(dislikedId).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }

    override suspend fun recipeSearchApi(
        successCallback: (response: NetworkResult<String>) -> Unit,
        itemSearch: String?
    ) {
        try {
            api.recipeSearchApi(itemSearch).apply {
                if (isSuccessful) {
                    body()?.let {
                        successCallback(NetworkResult.Success(it.toString()))
                    } ?: successCallback(NetworkResult.Error("Something went wrong"))
                }else{
                    successCallback(NetworkResult.Error(errorBody().toString()))
                }
            }
        }
        catch (e: HttpException) {
            successCallback(NetworkResult.Error(e.message()))
        }
    }


}
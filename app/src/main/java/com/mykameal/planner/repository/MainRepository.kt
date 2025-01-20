package com.mykameal.planner.repository

import com.google.gson.JsonObject
import com.mykameal.planner.basedata.NetworkResult
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface MainRepository {

    suspend fun bogyGoal(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getDietaryRestrictions(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getFavouriteCuisines(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getDislikeIngredients(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getAllergensIngredients(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getMealRoutine(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getCookingFrequency(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getEatingOut(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun getTakeAwayReason(successCallback: (response: NetworkResult<String>) -> Unit)

    suspend fun signUpModel(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone: String, password: String)

    suspend fun otpVerify(successCallback: (response: NetworkResult<String>) -> Unit,userid: String?, otp: String?,userName:String?,userGender:String?,
                          bodyGoal:String?,cookingFrequency:String?,eatingOut: String?, takeAway:String?,cookingForType:String?,
                          partnerName:String?,partnerAge:String?,partnerGender:String?,familyMemberName:String?, familyMemberAge:String?,
                          childFriendlyMeals:String?,mealRoutineId:List<String>?,spendingAmount:String?,duration:String?, dietaryid:List<String>?,
                          favourite:List<String>?, allergies:List<String>?,dislikeIngredients:List<String>?,deviceType:String?,fcmToken:String?)

    suspend fun forgotPassword(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone: String)
    suspend fun resendSignUpModel(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone: String)

    suspend fun forgotOtpVerify(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone: String,otp:String)

    suspend fun resendOtp(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone: String)

    suspend fun resetPassword(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone:String,password: String,confirmPassword:String)

    suspend fun userLogin(successCallback: (response: NetworkResult<String>) -> Unit,emailOrPhone: String,password: String,deviceType:String,fcmToken:String)

    suspend fun socialLogin(successCallback: (response: NetworkResult<String>) -> Unit,  emailOrPhone: String?, socialID: String?,userName:String?,
                            userGender:String?,bodyGoal:String?,cookingFrequency:String?,eatingOut:String?,takeAway:String?,cookingForType:String?,
                            partnerName:String?,partnerAge:String?,partnerGender:String?,familyMemberName:String?, familyMemberAge:String?,
                            childFriendlyMeals:String?,mealRoutineId:List<String>?,spendingAmount:String?,duration:String?, dietaryid:List<String>?,
                            favourite:List<String>?, allergies:List<String>?,dislikeIngredients:List<String>?,deviceType:String?,fcmToken:String?)
    suspend fun updateLocation(successCallback: (response: NetworkResult<String>) -> Unit,locationStatus: String)

    suspend fun updateNotification(successCallback: (response: NetworkResult<String>) -> Unit,notificationStatus: String)

    suspend fun privacyPolicy(successCallback: (response: NetworkResult<String>) -> Unit)

    suspend fun termCondition(successCallback: (response: NetworkResult<String>) -> Unit)

    suspend fun saveFeedback(successCallback: (response: NetworkResult<String>) -> Unit,email: String,message: String)


    suspend fun userProfileDataApi(successCallback: (response: NetworkResult<String>) -> Unit)

    suspend fun userLogOutDataApi(successCallback: (response: NetworkResult<String>) -> Unit)
    suspend fun userDeleteDataApi(successCallback: (response: NetworkResult<String>) -> Unit)

    suspend fun upDateProfileRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,name:String,bio:String, genderType: String,dob:String,height:String
                                        ,heightType:String,activityLevel:String,heightProtein:String,calories:String,fat:String,carbs:String,protien:String)

 suspend fun upDateImageNameRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, Image: MultipartBody.Part?, name: RequestBody)

 suspend fun addCardRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, token: String)


 suspend fun notificationRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, pushNotification: String,recipeRecommendations: String,productUpdates: String,promotionalUpdates: String)

 suspend fun recipeDetailsRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, url: String)

 suspend fun recipeReviewRequestApi(
     successCallback: (response: NetworkResult<String>) -> Unit,
     url: String,
     msg: String,
     ratingBarcount: String
 )

 suspend fun homeDetailsRequestApi(successCallback: (response: NetworkResult<String>) -> Unit)

 suspend fun recipeAddBasketRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,jsonObject: JsonObject)

 suspend fun recipeAddToPlanRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,jsonObject: JsonObject)

 suspend fun planRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, q: String)

 suspend fun planDateRequestApi(successCallback: (response: NetworkResult<String>) -> Unit, date: String,planType:String)
 suspend fun createCookBookApi(successCallback: (response: NetworkResult<String>) -> Unit,   name: RequestBody?, image: MultipartBody.Part?, status:RequestBody?)

 suspend fun likeUnlikeRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,  uri: String,likeType: String)

 suspend fun addBasketRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,  uri: String,quantity: String)

 suspend fun getCardAndBankRequestApi(successCallback: (response: NetworkResult<String>) -> Unit)

 suspend fun getWalletRequestApi(successCallback: (response: NetworkResult<String>) -> Unit)

 suspend fun deleteCardRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,cardId: String,customerId: String)

 suspend fun deleteBankRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,stripeAccountId: String)

 suspend fun countryRequestApi(successCallback: (response: NetworkResult<String>) -> Unit,url: String)

 suspend fun transferAmountRequest(successCallback: (response: NetworkResult<String>) -> Unit,amount: String,destination: String)


 suspend fun addBankRequestApi(
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
     bankIdBody: RequestBody,)


    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit)

    suspend fun updateAllergiesApi(successCallback: (response: NetworkResult<String>) -> Unit, allergies:List<String>?)

    suspend fun updateBodyGoalApi(successCallback: (response: NetworkResult<String>) -> Unit,bodyGoal: String?)
    suspend fun updateCookingFrequencyApi(successCallback: (response: NetworkResult<String>) -> Unit,cookingFrequency: String?)
    suspend fun updateReasonTakeAwayApi(successCallback: (response: NetworkResult<String>) -> Unit,takeAway: String?)
    suspend fun updateEatingOutApi(successCallback: (response: NetworkResult<String>) -> Unit,eatingOut: String?)
    suspend fun updatePartnerInfoApi(successCallback: (response: NetworkResult<String>) -> Unit,partnerName: String?,
                                     partnerAge: String?,partnerGender: String?)
    suspend fun updateFamilyInfoApi(successCallback: (response: NetworkResult<String>) -> Unit,familyName: String?,
                                    familyAge: String?,status: String?)

    suspend fun updateSpendingGroceriesApi(successCallback: (response: NetworkResult<String>) -> Unit,spendingAmount: String?,
                                           duration: String?)

    suspend fun updateMealRoutineApi(successCallback: (response: NetworkResult<String>) -> Unit, mealRoutineId:List<String>?)
    suspend fun updateDietaryApi(successCallback: (response: NetworkResult<String>) -> Unit, dietaryId:List<String>?)
    suspend fun updateFavouriteApi(successCallback: (response: NetworkResult<String>) -> Unit, favouriteId:List<String>?)
    suspend fun updateDislikedIngredientsApi(successCallback: (response: NetworkResult<String>) -> Unit, dislikedId:List<String>?)

    suspend fun recipeSearchApi(successCallback: (response: NetworkResult<String>) -> Unit, itemSearch:String?)


}
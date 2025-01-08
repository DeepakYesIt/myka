package com.yesitlabs.mykaapp.apiInterface

import com.google.gson.JsonObject
import com.yesitlabs.mykaapp.messageclass.ApiEndPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET(ApiEndPoint.bodyGoals)
    suspend fun getBogyGoal(): Response<JsonObject>

    @GET(ApiEndPoint.dietaryRestrictions)
    suspend fun getDietaryRestrictions(): Response<JsonObject>

    @GET(ApiEndPoint.favouriteCuisines)
    suspend fun getFavouriteCuisines(): Response<JsonObject>

    @GET(ApiEndPoint.dislikeIngredients)
    suspend fun getDislikeIngredients(): Response<JsonObject>

    @GET(ApiEndPoint.allergensIngredients)
    suspend fun getAllergensIngredients(): Response<JsonObject>

    @GET(ApiEndPoint.mealRoutine)
    suspend fun getMealRoutine(): Response<JsonObject>

    @GET(ApiEndPoint.cookingFrequency)
    suspend fun getCookingFrequency(): Response<JsonObject>

    @GET(ApiEndPoint.eatingOut)
    suspend fun getEatingOut(): Response<JsonObject>

    @GET(ApiEndPoint.takeAwayReason)
    suspend fun getTakeAwayReason(): Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.userSignup)
    suspend fun userSignUp(
        @Field("email_or_phone") email_or_phone:String,
        @Field("password") password:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.socialLogin)
    suspend fun socialLogin(
        @Field("email_or_phone") email_or_phone:String?,
        @Field("social_id") socialId:String?,
        @Field("username") username:String?,
        @Field("usergender") userGender:String?,
        @Field("bodygoal") bodygoal:String?,
        @Field("cooking_frequency") cookingFrequency:String?,
        @Field("eating_out") eatingOut:String?,
        @Field("take_way") takeWay:String?,
        @Field("cooking_for_type") cookingForType:String?,
        @Field("partner_name") partnerName:String?,
        @Field("partner_gender") partnerGender:String?,
        @Field("family_member_name") familyMemberName:String?,
        @Field("family_member_age") familyMemberAge:String?,
        @Field("child_friendly_meals") childFriendlyMeals:String?,
        @Field("meal_routine_id[]") mealRoutineId:List<String>?,
        @Field("spending_amount") spendingAmount:String?,
        @Field("duration") duration:String?,
        @Field("dietary_id[]") dietaryId:List<String>?,
        @Field("favourite[]") favourite:List<String>?,
        @Field("allergies[]") allergies:List<String>?,
        @Field("dislike_ingredients_id[]") dislikeIngredientsId:List<String>?,
        @Field("device_type") deviceType:String?,
        @Field("fcm_token") fcmToken:String?,
    ):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.otpVerify)
    suspend fun otpVerify(
        @Field("user_id") userId:String?,
        @Field("otp") otp:String?,
        @Field("username") username:String?,
        @Field("usergender") userGender:String?,
        @Field("bodygoal") bodygoal:String?,
        @Field("cooking_frequency") cookingFrequency:String?,
        @Field("eating_out") eatingOut:String?,
        @Field("take_way") takeWay:String?,
        @Field("cooking_for_type") cookingForType:String?,
        @Field("partner_name") partnerName:String?,
        @Field("partner_gender") partnerGender:String?,
        @Field("family_member_name") familyMemberName:String?,
        @Field("family_member_age") familyMemberAge:String?,
        @Field("child_friendly_meals") childFriendlyMeals:String?,
        @Field("meal_routine_id[]") mealRoutineId:List<String>?,
        @Field("spending_amount") spendingAmount:String?,
        @Field("duration") duration:String?,
        @Field("dietary_id[]") dietaryId:List<String>?,
        @Field("favourite[]") favourite:List<String>?,
        @Field("allergies[]") allergies: List<String>?,
        @Field("dislike_ingredients_id[]") dislikeIngredientsId: List<String>?,
        @Field("device_type") deviceType:String?,
        @Field("fcm_token") fcmToken:String?,
    ):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.forgotPassword)
    suspend fun forgotPassword(
        @Field("email_or_phone") emailOrPhone:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.forgotOtpVerify)
    suspend fun forgotOtpVerify(
        @Field("email_or_phone") emailOrPhone:String,
        @Field("otp") otp:String,
    ):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.resendOtp)
    suspend fun resendOtp(
        @Field("email_or_phone") email_or_phone:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.updatePassword)
    suspend fun resetPassword(
        @Field("email_or_phone") emailOrPhone:String,
        @Field("password") password:String,
        @Field("conformpassword") conformPassword:String,
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.userLogin)
    suspend fun userLogin(
        @Field("email_or_phone") emailOrPhone:String,
        @Field("password") password:String,
        @Field("device_type") deviceType:String,
        @Field("fcm_token") fcmToken:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.updateLocation)
    suspend fun updateLocation(
        @Field("location_on_off") location_on_off:String
    ):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.updateNotification)
    suspend fun updateNotification(
        @Field("notification_status") notification_status:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.saveFeedback)
    suspend fun saveFeedback(
        @Field("email") email:String,
        @Field("message") message:String,
    ):Response<JsonObject>


    @GET(ApiEndPoint.termsCondition)
    suspend fun getTermsCondition(): Response<JsonObject>

    @GET(ApiEndPoint.privacyPolicy)
    suspend fun getPrivacyPolicy(): Response<JsonObject>


    @POST(ApiEndPoint.userProfileUrl)
    suspend fun userProfileDataApi():Response<JsonObject>

    @POST(ApiEndPoint.logOutUrl)
    suspend fun userLogOutDataApi():Response<JsonObject>

    @POST(ApiEndPoint.deleteUrl)
    suspend fun userDeleteDataApi():Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.userProfileUpdateUrl)
    suspend fun userProfileUpdateApi(@Field("name") name:String,
                                     @Field("bio") bio:String,
                                     @Field("gender") gender:String,
                                     @Field("dob") dob:String,
                                     @Field("height") height:String,
                                     @Field("height_type") heightType:String,
                                     @Field("activity_level") activityLevel:String,
                                     @Field("height_protein") heightProtein:String,
                                     @Field("calories") calories:String,
                                     @Field("fat") fat:String,
                                     @Field("carbs") carbs:String,
                                     @Field("protien") protien:String):Response<JsonObject>

    @Multipart
    @POST(ApiEndPoint.userImageUpdateUrl)
    suspend fun upDateImageNameRequestApi(@Part image: MultipartBody.Part?,
                                          @Part("name") name: RequestBody?):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.addCardUrl)
    suspend fun addCardRequestApi(@Field("stripe_token") stripeToken:String):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.updateNotificationUrl)
    suspend fun notificationRequestApi(@Field("push_notification") pushNotification:String,
                                       @Field("recipe_recommendations") recipeRecommendations:String,
                                       @Field("product_updates") productUpdates:String,
                                       @Field("promotional_updates") promotionalUpdates:String):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.recipeDetailsUrl)
    suspend fun recipeDetailsRequestApi(@Field("uri") url:String):Response<JsonObject>


    @POST(ApiEndPoint.addBasketeDetailsUrl)
    suspend fun recipeAddBasketRequestApi(@Body jsonObject: JsonObject):Response<JsonObject>


    @POST(ApiEndPoint.getCardBankUrl)
    suspend fun getCardAndBankRequestApi():Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.allRecipeUrl)
    suspend fun planRequestApi(@Field("q") q:String):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.likeUnlikeUrl)
    suspend fun likeUnlikeRequestApi(@Field("uri") uri:String
                                     ,@Field("type") type:String):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.addBasketeUrl)
    suspend fun addBasketRequestApi(@Field("uri") uri:String
                                     ,@Field("quantity") quantity:String):Response<JsonObject>

    @POST(ApiEndPoint.walletAmountUrl)
    suspend fun getWalletRequestApi():Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.deleteCardUrl)
    suspend fun deleteCardRequestApi(@Field("card_id") cardId:String,
                                     @Field("customer_id") customerId:String):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.deleteBankUrl)
    suspend fun deleteBankRequestApi(@Field("stripe_account_id") stripeAccountId:String):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.countriesUrl)
    suspend fun countryRequestApi(@Field("url") url:String):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.transferToAccountUrl)
    suspend fun transferAmountRequest(@Field("amount") amount:String
                                      ,@Field("account_id") destination:String):Response<JsonObject>

    @Multipart
    @POST(ApiEndPoint.bankAddUrl)
    suspend fun addBankRequestApi(
        @Part image: MultipartBody.Part?,
        @Part filePartBack: MultipartBody.Part?,
        @Part filePart: MultipartBody.Part?,
        @Part("firstname") firstNameBody: RequestBody,
        @Part("lastname") lastNameBody: RequestBody,
        @Part("email") emailBody: RequestBody,
        @Part("phone") phoneBody: RequestBody,
        @Part("dob") dobBody: RequestBody,
        @Part("id_number") personalIdentificationNobody: RequestBody,
        @Part("id_type") idTypeBody: RequestBody,
        @Part("ssn") ssnBody: RequestBody,
        @Part("address") addressBody: RequestBody,
        @Part("country") countryBody: RequestBody,
        @Part("state_code") shortStateNameBody: RequestBody,
        @Part("city") cityBody: RequestBody,
        @Part("postal_code") postalCodeBody: RequestBody,
        @Part("bank_proof_type") bankDocumentTypeBody: RequestBody,
        @Part("device_type") deviceTypeBody: RequestBody,
        @Part("token_type") tokenTypeBody: RequestBody,
        @Part("stripe_token") stripeTokenBody: RequestBody,
        @Part("save_card") saveCardBody: RequestBody,
        @Part("amount") amountBody: RequestBody,
        @Part("payment_type") paymentTypeBody: RequestBody,
        @Part("bank_id") bankIdBody: RequestBody
    ):Response<JsonObject>


}
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
    @POST(ApiEndPoint.otpVerify)
    suspend fun otpVerify(
        @Field("user_id") user_id:String?,
        @Field("otp") otp:String?,
        @Field("username") username:String?,
        @Field("usergender") usergender:String?,
        @Field("bodygoal") bodygoal:String?,
        @Field("cooking_frequency") cooking_frequency:String?,
        @Field("take_way") take_way:String?,
        @Field("cooking_for_type") cooking_for_type:String?,
        @Field("partner_name") partner_name:String?,
        @Field("partner_gender") partner_gender:String?,
        @Field("family_member_name") family_member_name:String?,
        @Field("family_member_age") family_member_age:String?,
        @Field("child_friendly_meals") child_friendly_meals:String?,
        @Field("meal_routine_id") meal_routine_id:List<String>?,
        @Field("spending_amount") spending_amount:String?,
        @Field("duration") duration:String?,
        @Field("dietary_id") dietary_id:List<String>?,
        @Field("dislike_ingredients_id") dislike_ingredients_id:List<String>?,
        @Field("device_type") device_type:String?,
        @Field("fcm_token") fcm_token:String?,
    ):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.forgotPassword)
    suspend fun forgotPassword(
        @Field("email_or_phone") email_or_phone:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.forgotOtpVerify)
    suspend fun forgotOtpVerify(
        @Field("email_or_phone") email_or_phone:String,
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
        @Field("conformpassword") conformpassword:String,
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.userLogin)
    suspend fun userLogin(
        @Field("email_or_phone") email_or_phone:String,
        @Field("password") password:String,
        @Field("device_type") device_type:String,
        @Field("fcm_token") fcm_token:String
    ):Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.socialLogin)
    suspend fun socialLogin(
        @Field("email_or_phone") email_or_phone:String,
        @Field("social_id") socialId:String,
        @Field("device_type") device_type:String,
        @Field("fcm_token") fcm_token:String
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


    @POST(ApiEndPoint.userProfileUrl)
    suspend fun userProfileDataApi():Response<JsonObject>

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


    @POST(ApiEndPoint.getCardBankUrl)
    suspend fun getCardAndBankRequestApi():Response<JsonObject>


    @FormUrlEncoded
    @POST(ApiEndPoint.deleteCardUrl)
    suspend fun deleteCardRequestApi(@Field("card_id") cardId:String,
                                     @Field("customer_id") customerId:String):Response<JsonObject>

    @FormUrlEncoded
    @POST(ApiEndPoint.countriesUrl)
    suspend fun countryRequestApi(@Field("url") url:String):Response<JsonObject>

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
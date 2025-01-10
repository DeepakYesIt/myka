package com.mykameal.planner.fragment.authfragment.login.model

data class LoginModel(
    val code: Int,
    val `data`: LoginModelData,
    val message: String,
    val success: Boolean
)

data class LoginModelData(
    val device_type: Any,
    val email: String,
    val fcm_token: Any,
    val id: Int,
    val user_type: Int,
    val name: String,
    val profile_img: Any,
    val token: String,
    val updated_at: String
)
data class SocialLoginModel(
    val code: Int,
    val `data`: SocialLoginModelData,
    val message: String,
    val success: Boolean
)

data class SocialLoginModelData(
    val cooking_for_type: Int,
    val created_at: String,
    val name: String,
    val email: String,
    val id: Int,
    val isNewuser: Boolean,
    val otp_verify: Int,
    val social_id: String,
    val profile_img: Any,
    val token: String,
    val updated_at: String
)


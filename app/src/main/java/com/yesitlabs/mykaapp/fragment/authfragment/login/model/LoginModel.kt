package com.yesitlabs.mykaapp.fragment.authfragment.login.model

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
    val name: String,
    val profile_img: Any,
    val token: String,
    val updated_at: String
)
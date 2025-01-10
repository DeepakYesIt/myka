package com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse

data class Data(
    val activity_level: String?,
    var bio: String?="",
    var calories: Int?=0,
    var carbs: Int?=0,
    val email: String,
    var fat: Int?=0,
    val gender: String,
    val height: String?,
    val dob: String?,
    var height_protein: String?,
    val height_type: String?,
    var name: String?,
    val profile_img: String?,
    var protien: Int?=0
)
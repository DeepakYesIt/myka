package com.mykameal.planner.fragment.commonfragmentscreen.commonModel

data class PartnerDetail(
    var age: String,
    val created_at: String,
    val deleted_at: Any,
    var gender: String,
    val id: Int,
    var name: String,
    val updated_at: String,
    val user_id: Int
)

data class FamilyDetail(
    var age: String,
    val created_at: String,
    val deleted_at: Any,
    var status: String,
    val id: Int,
    var name: String,
    val updated_at: String,
    val user_id: Int
)
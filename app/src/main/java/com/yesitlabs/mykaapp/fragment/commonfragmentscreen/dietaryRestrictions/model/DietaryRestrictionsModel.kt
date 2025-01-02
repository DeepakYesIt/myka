package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.model

data class DietaryRestrictionsModel(
    val code: Int,
    val `data`: List<DietaryRestrictionsModelData>,
    val message: String,
    val success: Boolean
)

data class DietaryRestrictionsModelData(
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    var isOpen:Boolean=false,
    val name: String,
    val updated_at: String
)
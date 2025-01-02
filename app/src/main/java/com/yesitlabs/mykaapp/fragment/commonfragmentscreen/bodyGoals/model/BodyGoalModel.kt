package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.model

data class BodyGoalModel(
    val code: Int,
    val `data`: List<BodyGoalModelData>,
    val message: String,
    val success: Boolean
)

data class BodyGoalModelData(
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val name: String,
    val updated_at: String
)
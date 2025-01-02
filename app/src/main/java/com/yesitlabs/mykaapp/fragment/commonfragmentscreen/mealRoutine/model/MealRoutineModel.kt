package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.model

data class MealRoutineModel(
    val code: Int,
    val `data`: List<MealRoutineModelData>,
    val message: String,
    val success: Boolean
)

data class MealRoutineModelData(
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val name: String,
    val updated_at: String
)
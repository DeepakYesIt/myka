package com.mykameal.planner.fragment.commonfragmentscreen.bodyGoals.model

data class BodyGoalModel(
    val code: Int,
    val `data`: List<BodyGoalModelData>,
    val message: String,
    val success: Boolean
)

data class BodyGoalModelData(

    val id: Int,
    val name: String,
    var selected:Boolean=false
)


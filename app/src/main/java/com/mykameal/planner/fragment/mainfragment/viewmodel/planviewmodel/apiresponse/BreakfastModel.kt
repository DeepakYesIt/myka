package com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse

data class BreakfastModel(
    val recipe: RecipePlanModel?,
    var is_like: Int?,
    val review_number: Int?=0,
    val review: Double?=0.0,
)
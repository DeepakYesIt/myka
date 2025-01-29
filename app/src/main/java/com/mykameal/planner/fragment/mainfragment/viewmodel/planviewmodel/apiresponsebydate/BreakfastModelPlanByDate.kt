package com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate

import com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.RecipePlanModel

data class BreakfastModelPlanByDate(
    var id: Int?,
    var servings: Int?,
    var date: String?,
    var day: String?,
    val recipe: RecipePlanModel?,
)
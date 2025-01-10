package com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate

data class DataPlayByDate(
    val Breakfast: MutableList<BreakfastModelPlanByDate>?,
    val Dinner: MutableList<BreakfastModelPlanByDate>?,
    val Lunch: MutableList<BreakfastModelPlanByDate>?,
    val Snack: MutableList<BreakfastModelPlanByDate>?,
    val Teatime: MutableList<BreakfastModelPlanByDate>?
)
package com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate

data class DataPlayByDate(
    val Breakfast: MutableList<BreakfastModelPlanByDate>?,
    val Dinner: MutableList<BreakfastModelPlanByDate>?,
    val Lunch: MutableList<BreakfastModelPlanByDate>?,
    val Snack: MutableList<BreakfastModelPlanByDate>?,
    val Teatime: MutableList<BreakfastModelPlanByDate>?,
    val fridge:Int?,
    val freezer:Int?,
    val fat:Double?,
    val protein:Double?,
    val kcal:Double?,
    val carbs:Double?
)
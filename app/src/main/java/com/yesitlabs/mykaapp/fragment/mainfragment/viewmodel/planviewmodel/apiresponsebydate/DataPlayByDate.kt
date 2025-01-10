package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponsebydate

import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponse.BreakfastModel

data class DataPlayByDate(
    val Breakfast: MutableList<BreakfastModelPlanByDate>?,
    val Dinner: MutableList<BreakfastModelPlanByDate>?,
    val Lunch: MutableList<BreakfastModelPlanByDate>?,
    val Snack: MutableList<BreakfastModelPlanByDate>?,
    val Teatime: MutableList<BreakfastModelPlanByDate>?
)
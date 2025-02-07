package com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponse

data class RecipesModel(
    val Breakfast: MutableList<BreakfastModel>?,
    val Dinner: MutableList<BreakfastModel>?,
    val Lunch: MutableList<BreakfastModel>?,
    val Snack: MutableList<BreakfastModel>?,
    val Teatime: MutableList<BreakfastModel>?

)
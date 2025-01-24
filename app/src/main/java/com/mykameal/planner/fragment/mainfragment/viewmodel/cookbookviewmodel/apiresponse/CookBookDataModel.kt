package com.mykameal.planner.fragment.mainfragment.viewmodel.cookbookviewmodel.apiresponse

import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.RecipeModel

data class CookBookDataModel(
    val `data`: CookBookDataRecipeModel?,
    val id: Int,
    val uri: String?
)
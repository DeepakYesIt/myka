package com.mykameal.planner.fragment.mainfragment.viewmodel.cookbookviewmodel.apiresponse

import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.RecipeModel

data class CookBookDataRecipeModel(
    val recipe: RecipeModel?,
    var is_like: Int?
)
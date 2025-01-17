package com.mykameal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse

import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse.RecipeModel

data class UserDataModel(
    val recipe: RecipeModel?,
    var is_like: Int?
)
package com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.apiresponse

data class RecipeModel(
    val label: String?,
    val url: String?,
    val images: ImagesModel?,
    val totalNutrients: TotalNutrientsModel?,
    val calories: Double?,
    val totalTime: Int?,
    val statusInGredients: Boolean=false,
    val ingredients: MutableList<IngredientsModel>?,
    val instructionLines: MutableList<String>?

)
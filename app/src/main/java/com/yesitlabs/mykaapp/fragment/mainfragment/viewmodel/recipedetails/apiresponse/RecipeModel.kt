package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.apiresponse

data class RecipeModel(
    val label: String?,
    val images: ImagesModel?,
    val totalNutrients: TotalNutrientsModel?,
    val calories: Double?,
    val totalTime: Int?

)
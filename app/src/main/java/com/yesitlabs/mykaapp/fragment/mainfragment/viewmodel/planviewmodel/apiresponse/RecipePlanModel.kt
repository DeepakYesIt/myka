package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel.apiresponse

import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.apiresponse.ImagesModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.apiresponse.TotalNutrientsModel

data class RecipePlanModel(
    val label: String?,
    val uri: String?,
    val images: ImagesModel?,
    val totalNutrients: TotalNutrientsModel?,
    val calories: Double?,
    val totalTime: Int?

)
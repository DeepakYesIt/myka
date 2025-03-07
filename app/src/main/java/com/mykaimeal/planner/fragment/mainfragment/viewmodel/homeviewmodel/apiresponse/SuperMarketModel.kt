package com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse

data class SuperMarketModel(
    val code: Int?,
    val `data`: List<SuperMarketModelData>?,
    val message: String?,
    val success: Boolean?
)
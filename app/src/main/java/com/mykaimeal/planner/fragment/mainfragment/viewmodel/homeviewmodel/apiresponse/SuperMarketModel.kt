package com.mykaimeal.planner.fragment.mainfragment.viewmodel.homeviewmodel.apiresponse

import com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.model.Store

data class SuperMarketModel(
    val code: Int?,
    val `data`: MutableList<Store>?,
    val message: String?,
    val success: Boolean?
)
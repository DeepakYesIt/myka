package com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.apiresponse

data class SearchApiResponse(
    val code: Int,
    val `data`: Data?,
    val message: String,
    val success: Boolean
)
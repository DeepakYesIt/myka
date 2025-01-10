package com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun recipeDetailsRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    url: String){
        repository.recipeDetailsRequestApi({ successCallback(it) },url)
    }


    suspend fun recipeAddBasketRequest(successCallback: (response: NetworkResult<String>) -> Unit, jsonObject: JsonObject
    ){
        repository.recipeAddBasketRequestApi({ successCallback(it) },jsonObject)
    }



}
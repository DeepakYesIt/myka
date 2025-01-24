package com.mykameal.planner.fragment.mainfragment.cookedtab.cookedfragment.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CookedTabViewModel@Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun cookedDateRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                date: String,planType:String){
        repository.planDateRequestApi({ successCallback(it) },date,planType)
    }

    suspend fun removeMealApi(successCallback: (response: NetworkResult<String>) -> Unit,
                                cookedId: String){
        repository.removeMealApi({ successCallback(it) },cookedId)
    }

    suspend fun getCookBookRequest(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCookBookRequestApi { successCallback(it) }
    }

    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,likeType: String,type:String){
        repository.likeUnlikeRequestApi({ successCallback(it) },uri,likeType,type)
    }

    suspend fun recipeServingCountRequest(successCallback: (response: NetworkResult<String>) -> Unit, jsonObject: JsonObject
    ){
        repository.recipeAddToPlanRequestApi({ successCallback(it) },jsonObject)
    }

}
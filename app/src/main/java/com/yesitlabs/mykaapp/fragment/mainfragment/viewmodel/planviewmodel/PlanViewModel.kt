package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.planviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@HiltViewModel
class PlanViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun planRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    q: String){
        repository.planRequestApi({ successCallback(it) },q)
    }

    suspend fun planDateRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                            date: String){
        repository.planDateRequestApi({ successCallback(it) },date)
    }


    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    uri: String,likeType: String){
        repository.likeUnlikeRequestApi({ successCallback(it) },uri,likeType)
    }

    suspend fun addBasketRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,quantity: String){
        repository.addBasketRequestApi({ successCallback(it) },uri,quantity)
    }


    suspend fun recipeAddToPlanRequest(successCallback: (response: NetworkResult<String>) -> Unit, jsonObject: JsonObject
    ){
        repository.recipeAddToPlanRequestApi({ successCallback(it) },jsonObject)
    }


}
package com.mykameal.planner.fragment.mainfragment.viewmodel.homeviewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.Data
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {



    suspend fun homeDetailsRequest(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.homeDetailsRequestApi { successCallback(it) }
    }

    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,likeType: String,type:String){
        repository.likeUnlikeRequestApi({ successCallback(it) },uri,likeType,type)
    }


}
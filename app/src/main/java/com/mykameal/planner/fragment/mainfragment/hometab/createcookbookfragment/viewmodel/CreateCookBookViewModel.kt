package com.mykameal.planner.fragment.mainfragment.hometab.createcookbookfragment.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel

class CreateCookBookViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun createCookBookApi(successCallback: (response: NetworkResult<String>) -> Unit, name: RequestBody?, image: MultipartBody.Part?, status: RequestBody?){
        repository.createCookBookApi({ successCallback(it) },name,image, status)
    }

    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,likeType: String,type:String){
        repository.likeUnlikeRequestApi({ successCallback(it) },uri,likeType,type)
    }

}
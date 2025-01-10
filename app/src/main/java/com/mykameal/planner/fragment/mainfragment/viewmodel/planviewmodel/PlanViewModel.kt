package com.mykameal.planner.fragment.mainfragment.viewmodel.planviewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class PlanViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun planRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    q: String){
        repository.planRequestApi({ successCallback(it) },q)
    }


    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    uri: String,likeType: String){
        repository.likeUnlikeRequestApi({ successCallback(it) },uri,likeType)
    }

    suspend fun addBasketRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,quantity: String){
        repository.addBasketRequestApi({ successCallback(it) },uri,quantity)
    }



}
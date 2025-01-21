package com.mykameal.planner.fragment.mainfragment.viewmodel.cookbookviewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class CookBookViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getCookBookRequest(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCookBookRequestApi { successCallback(it) }
    }

}
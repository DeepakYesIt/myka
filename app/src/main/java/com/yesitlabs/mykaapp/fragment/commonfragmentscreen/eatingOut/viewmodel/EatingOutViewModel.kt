package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.eatingOut.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EatingOutViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getEatingOut(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getEatingOut { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateEatingOutApi(successCallback: (response: NetworkResult<String>) -> Unit,eatingOut: String?){
        repository.updateEatingOutApi ({ successCallback(it) },eatingOut)
    }

}
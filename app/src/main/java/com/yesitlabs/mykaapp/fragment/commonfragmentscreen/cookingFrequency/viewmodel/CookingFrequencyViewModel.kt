package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.cookingFrequency.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CookingFrequencyViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getCookingFrequency(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCookingFrequency { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }


    suspend fun updateCookingFrequencyApi(successCallback: (response: NetworkResult<String>) -> Unit,bodyGoal: String){
        repository.updateCookingFrequencyApi ({ successCallback(it) },bodyGoal)
    }

}
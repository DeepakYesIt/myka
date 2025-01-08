package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.bodyGoals.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BodyGoalViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getBodyGoal(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.bogyGoal { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateBodyGoalApi(successCallback: (response: NetworkResult<String>) -> Unit,bodyGoal: String){
        repository.updateBodyGoalApi ({ successCallback(it) },bodyGoal)
    }

}
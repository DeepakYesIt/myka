package com.mykameal.planner.fragment.commonfragmentscreen.reasonTakeAway.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ReasonTakeAwayViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getTakeAwayReason(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getTakeAwayReason { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateReasonTakeAwayApi(successCallback: (response: NetworkResult<String>) -> Unit,reasonTakeAway: String?){
        repository.updateReasonTakeAwayApi ({ successCallback(it) },reasonTakeAway)
    }

}
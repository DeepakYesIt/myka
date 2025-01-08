package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.spendingOnGroceries.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SpendingGroceriesViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        repository.userPreferencesApi { successCallback(it) }
    }

    suspend fun updateSpendingGroceriesApi(successCallback: (response: NetworkResult<String>) -> Unit,spendingAmount: String?,duration: String?){
        repository.updateSpendingGroceriesApi ({ successCallback(it) },spendingAmount,duration)
    }

}

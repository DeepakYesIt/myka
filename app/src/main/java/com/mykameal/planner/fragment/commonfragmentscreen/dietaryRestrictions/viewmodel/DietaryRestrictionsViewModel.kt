package com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DietaryRestrictionsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getDietaryRestrictions(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getDietaryRestrictions { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateDietaryApi(successCallback: (response: NetworkResult<String>) -> Unit,dietaryId: List<String>?){
        repository.updateDietaryApi ({ successCallback(it) },dietaryId)
    }

}
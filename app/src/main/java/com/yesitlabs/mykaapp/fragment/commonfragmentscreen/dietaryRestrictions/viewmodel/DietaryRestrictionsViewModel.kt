package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.dietaryRestrictions.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DietaryRestrictionsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getDietaryRestrictions(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getDietaryRestrictions { successCallback(it) }
    }

}
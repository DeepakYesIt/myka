package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.allergensIngredients.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AllergenIngredientViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getAllergensIngredients(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getAllergensIngredients { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateAllergiesApi(successCallback: (response: NetworkResult<String>) -> Unit,allergies: List<String>?){
        repository.updateAllergiesApi ({ successCallback(it) },allergies)
    }

}
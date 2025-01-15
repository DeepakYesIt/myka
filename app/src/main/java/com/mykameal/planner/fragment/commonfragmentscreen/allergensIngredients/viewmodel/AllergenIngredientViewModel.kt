package com.mykameal.planner.fragment.commonfragmentscreen.allergensIngredients.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllergenIngredientViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    var allergensLocalData: List<DietaryRestrictionsModelData>?=null

    suspend fun getAllergensIngredients(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getAllergensIngredients { successCallback(it) }
    }

    fun setAllergensData(data: List<DietaryRestrictionsModelData>) {
        allergensLocalData=data
    }

    // Method to clear data
    fun clearData() {
        allergensLocalData = null // Clear LiveData
        // Reset other variables
    }

    fun getAllergensData(): List<DietaryRestrictionsModelData>? {
        return allergensLocalData
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateAllergiesApi(successCallback: (response: NetworkResult<String>) -> Unit,allergies: List<String>?){
        repository.updateAllergiesApi ({ successCallback(it) },allergies)
    }

}
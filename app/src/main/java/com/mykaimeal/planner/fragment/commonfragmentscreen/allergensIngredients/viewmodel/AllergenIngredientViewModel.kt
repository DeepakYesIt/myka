package com.mykaimeal.planner.fragment.commonfragmentscreen.allergensIngredients.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.fragment.commonfragmentscreen.allergensIngredients.model.AllergensIngredientModelData
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllergenIngredientViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    private var allergensLocalData: MutableList<AllergensIngredientModelData>?=null

    suspend fun getAllergensIngredients(successCallback: (response: NetworkResult<String>) -> Unit,itemCount:String?){
        repository.getAllergensIngredients({ successCallback(it) },itemCount)
    }

    fun setAllergensData(data: MutableList<AllergensIngredientModelData>) {
        allergensLocalData=data
    }

    // Method to clear data
    fun clearData() {
        allergensLocalData = null // Clear LiveData
        // Reset other variables
    }

    fun getAllergensData(): MutableList<AllergensIngredientModelData>? {
        return allergensLocalData
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateAllergiesApi(successCallback: (response: NetworkResult<String>) -> Unit,allergies: List<String>?){
        repository.updateAllergiesApi ({ successCallback(it) },allergies)
    }

}
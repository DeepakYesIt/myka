package com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.fragment.commonfragmentscreen.dietaryRestrictions.model.DietaryRestrictionsModelData
import com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.model.MealRoutineModelData
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MealRoutineViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    private var mealRoutineLocalData: MutableList<MealRoutineModelData>?=null

    suspend fun getMealRoutine(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getMealRoutine { successCallback(it) }
    }

    fun setMealRoutineData(data: MutableList<MealRoutineModelData>) {
        mealRoutineLocalData=data
    }

    // Method to clear data
    fun clearData() {
        mealRoutineLocalData = null // Clear LiveData
        // Reset other variables
    }

    fun getMealRoutineData(): MutableList<MealRoutineModelData>? {
        return mealRoutineLocalData
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateMealRoutineApi(successCallback: (response: NetworkResult<String>) -> Unit,mealRoutineId: List<String>?){
        repository.updateMealRoutineApi ({ successCallback(it) },mealRoutineId)
    }

}
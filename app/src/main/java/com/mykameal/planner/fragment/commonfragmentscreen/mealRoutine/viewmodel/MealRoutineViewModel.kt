package com.mykameal.planner.fragment.commonfragmentscreen.mealRoutine.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MealRoutineViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {
    suspend fun getMealRoutine(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getMealRoutine { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateMealRoutineApi(successCallback: (response: NetworkResult<String>) -> Unit,mealRoutineId: List<String>?){
        repository.updateMealRoutineApi ({ successCallback(it) },mealRoutineId)
    }

}
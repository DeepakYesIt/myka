package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.mealRoutine.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MealRoutineViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {
    suspend fun getMealRoutine(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getMealRoutine { successCallback(it) }
    }

}
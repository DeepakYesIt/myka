package com.mykameal.planner.fragment.commonfragmentscreen.ingredientDislikes.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class DislikeIngredientsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getDislikeIngredients(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getDislikeIngredients { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateDislikedIngredientsApi(successCallback: (response: NetworkResult<String>) -> Unit,dislikeId: List<String>?){
        repository.updateDislikedIngredientsApi ({ successCallback(it) },dislikeId)
    }

}
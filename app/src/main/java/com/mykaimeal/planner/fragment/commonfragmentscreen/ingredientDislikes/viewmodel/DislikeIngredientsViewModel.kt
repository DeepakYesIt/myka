package com.mykaimeal.planner.fragment.commonfragmentscreen.ingredientDislikes.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.fragment.commonfragmentscreen.ingredientDislikes.model.DislikedIngredientsModelData
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class DislikeIngredientsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    private var dislikeIngLocalData: MutableList<DislikedIngredientsModelData>?=null

    suspend fun getDislikeIngredients(successCallback: (response: NetworkResult<String>) -> Unit,itemCount:String?){
        repository.getDislikeIngredients({ successCallback(it) },itemCount)
    }

    fun setDislikeIngData(data: MutableList<DislikedIngredientsModelData>) {
        dislikeIngLocalData=data
    }

    // Method to clear data
    fun clearData() {
        dislikeIngLocalData = null // Clear LiveData
        // Reset other variables
    }

    fun getDislikeIngData(): MutableList<DislikedIngredientsModelData>? {
        return dislikeIngLocalData
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateDislikedIngredientsApi(successCallback: (response: NetworkResult<String>) -> Unit,dislikeId: List<String>?){
        repository.updateDislikedIngredientsApi ({ successCallback(it) },dislikeId)
    }

}
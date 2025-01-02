package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.ingredientDislikes.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class DislikeIngredientsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getDislikeIngredients(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getDislikeIngredients { successCallback(it) }
    }

}
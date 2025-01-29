package com.mykameal.planner.fragment.mainfragment.searchtab.searchscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun recipeSearchApi(successCallback: (response: NetworkResult<String>) -> Unit, itemSearch: String?){
        repository.recipeSearchApi ({ successCallback(it) },itemSearch)
    }


}
package com.mykameal.planner.fragment.commonfragmentscreen.favouriteCuisines.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class FavouriteCuisineViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {
    suspend fun getFavouriteCuisines(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getFavouriteCuisines { successCallback(it) }
    }

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userPreferencesApi{ successCallback(it) }
    }

    suspend fun updateFavouriteApi(successCallback: (response: NetworkResult<String>) -> Unit,favouriteId: List<String>?){
        repository.updateFavouriteApi ({ successCallback(it) },favouriteId)
    }

}
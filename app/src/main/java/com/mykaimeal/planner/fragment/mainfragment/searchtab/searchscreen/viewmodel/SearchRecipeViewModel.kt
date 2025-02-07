package com.mykaimeal.planner.fragment.mainfragment.searchtab.searchscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun recipeSearchApi(successCallback: (response: NetworkResult<String>) -> Unit, itemSearch: String?){
        repository.recipeSearchApi ({ successCallback(it) },itemSearch)
    }

    suspend fun recipeforSearchApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.recipeforSearchApi { successCallback(it) }
    }

    suspend fun recipePreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.recipePreferencesApi { successCallback(it) }
    }

    suspend fun getCookBookRequest(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCookBookRequestApi { successCallback(it) }
    }

    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,likeType: String,type:String){
        repository.likeUnlikeRequestApi({ successCallback(it) },uri,likeType,type)
    }


    suspend fun getMealByUrl(successCallback: (response: NetworkResult<String>) -> Unit,url:String?){
        repository.getMealByUrl({successCallback(it)},url)
    }

}
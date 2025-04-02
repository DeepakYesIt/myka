package com.mykaimeal.planner.fragment.mainfragment.searchtab.allingredient.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllIngredientsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun getAllIngredientsUrl(successCallback: (response: NetworkResult<String>) -> Unit,category:String?,search:String?,number:String?){
        repository.getAllIngredientsUrl({ successCallback(it) },category, search, number)
    }



}
package com.mykaimeal.planner.fragment.mainfragment.searchtab.filtersearch.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterSearchViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getFilterList(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getFilterList{ successCallback(it) }
    }

}
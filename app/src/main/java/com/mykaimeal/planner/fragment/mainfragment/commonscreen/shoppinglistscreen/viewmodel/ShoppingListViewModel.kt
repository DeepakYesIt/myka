package com.mykaimeal.planner.fragment.mainfragment.commonscreen.shoppinglistscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun getShoppingListUrl(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getShoppingList{ successCallback(it) }
    }

}
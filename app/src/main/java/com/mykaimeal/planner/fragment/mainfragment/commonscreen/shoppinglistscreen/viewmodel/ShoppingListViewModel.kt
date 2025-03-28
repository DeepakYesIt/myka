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

    suspend fun removeBasketUrlApi(successCallback: (response: NetworkResult<String>) -> Unit,
                                   cookedId: String?){
        repository.removeBasketUrlApi({ successCallback(it) },cookedId)
    }

    suspend fun basketYourRecipeIncDescUrl(successCallback: (response: NetworkResult<String>) -> Unit,
                                           uri: String?,quantity:String?){
        repository.basketYourRecipeIncDescUrl({ successCallback(it) },uri, quantity)
    }


    suspend fun basketIngIncDescUrl(successCallback: (response: NetworkResult<String>) -> Unit,
                                    foodId: String?,quantity:String?){
        repository.basketIngIncDescUrl({ successCallback(it) },foodId, quantity)
    }

}
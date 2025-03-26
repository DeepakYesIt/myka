package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BasketScreenViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getBasketUrl(successCallback: (response: NetworkResult<String>) -> Unit,storeId:String?){
        repository.getBasketUrl({ successCallback(it) },storeId)
    }

    suspend fun getAddressUrl(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getAddressUrl{ successCallback(it) }
    }

    suspend fun removeBasketUrlApi(successCallback: (response: NetworkResult<String>) -> Unit,
                              cookedId: String?){
        repository.removeBasketUrlApi({ successCallback(it) },cookedId)
    }

    suspend fun basketYourRecipeIncDescUrl(successCallback: (response: NetworkResult<String>) -> Unit,
                              uri: String?,quantity:String?){
        repository.basketYourRecipeIncDescUrl({ successCallback(it) },uri, quantity)
    }

}
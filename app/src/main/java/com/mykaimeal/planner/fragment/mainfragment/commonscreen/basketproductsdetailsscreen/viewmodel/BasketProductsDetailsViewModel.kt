package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketproductsdetailsscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BasketProductsDetailsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    suspend fun getProductsUrl(successCallback: (response: NetworkResult<String>) -> Unit,query:String?){
        repository.getProductsUrl({ successCallback(it) },query)
    }


    suspend fun getProductsDetailsUrl(successCallback: (response: NetworkResult<String>) -> Unit,proId:String?,query:String?){
        repository.getProductsDetailsUrl({ successCallback(it) },proId,query)
    }

    suspend fun getSelectProductsUrl(successCallback: (response: NetworkResult<String>) -> Unit,id:String?,productId:String?){
        repository.getSelectProductsUrl({ successCallback(it) },id, productId)
    }

}
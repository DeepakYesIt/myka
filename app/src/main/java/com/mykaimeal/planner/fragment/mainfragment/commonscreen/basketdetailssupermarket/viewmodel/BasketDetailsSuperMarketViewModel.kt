package com.mykaimeal.planner.fragment.mainfragment.commonscreen.basketdetailssupermarket.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BasketDetailsSuperMarketViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    suspend fun getStoreProductUrl(successCallback: (response: NetworkResult<String>) -> Unit) {
        repository.getStoreProductUrl{ successCallback(it) }
    }

    suspend fun getSuperMarket(successCallback: (response: NetworkResult<String>) -> Unit,
                               latitude: String?,longitude: String?){
        repository.getSuperMarket({ successCallback(it) },latitude,longitude)
    }

}
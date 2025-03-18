package com.mykaimeal.planner.fragment.mainfragment.commonscreen.checkoutscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutScreenViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getCheckoutScreenUrl(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCheckoutScreenUrl{ successCallback(it) }
    }


}
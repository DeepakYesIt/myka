package com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentCreditDebitViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getCardMealMeUrl(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCardMealMeUrl{ successCallback(it) }
    }

    suspend fun deleteCardRequest(successCallback: (response: NetworkResult<String>) -> Unit,cardId: String,customerId: String){
        repository.deleteCardRequestApi ({ successCallback(it) },cardId,customerId)
    }


    suspend fun addCardMealMeUrl(successCallback: (response: NetworkResult<String>) -> Unit,cardNumber:String?,expMonth:String?,expYear:String?,cvv:String?){
        repository.addCardMealMeUrl({ successCallback(it) },cardNumber,expMonth,expYear,cvv)
    }

}
package com.mykaimeal.planner.fragment.mainfragment.commonscreen.productpaymentscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentCreditDebitViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {



}
package com.mykameal.planner.fragment.authfragment.forgotpassword.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun forgotPassword(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String){
        repository.forgotPassword({ successCallback(it) }, emailOrPhone)
    }

}
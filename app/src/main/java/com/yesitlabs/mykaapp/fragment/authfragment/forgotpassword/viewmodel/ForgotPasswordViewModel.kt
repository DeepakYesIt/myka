package com.yesitlabs.mykaapp.fragment.authfragment.forgotpassword.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun forgotPassword(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String){
        repository.forgotPassword({ successCallback(it) }, emailOrPhone)
    }

}
package com.yesitlabs.mykaapp.fragment.authfragment.resetpassword.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun resetPassword(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String, password:String, confirmPassword:String){
        repository.resetPassword({ successCallback(it) }, emailOrPhone,password,confirmPassword)
    }

}
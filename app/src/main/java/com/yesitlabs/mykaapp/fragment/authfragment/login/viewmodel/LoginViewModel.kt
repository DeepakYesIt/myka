package com.yesitlabs.mykaapp.fragment.authfragment.login.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun userLogin(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String,password:String,deviceType:String,fcmToken:String){
        repository.userLogin({ successCallback(it) }, emailOrPhone,password,deviceType, fcmToken)
    }

}
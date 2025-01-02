package com.yesitlabs.mykaapp.fragment.authfragment.signup.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun  signUpModel(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String, password: String){
        repository.signUpModel({ successCallback(it) }, emailOrPhone,password)
    }

}
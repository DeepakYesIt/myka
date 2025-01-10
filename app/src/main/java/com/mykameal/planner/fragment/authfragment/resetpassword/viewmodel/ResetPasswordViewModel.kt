package com.mykameal.planner.fragment.authfragment.resetpassword.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun resetPassword(successCallback: (response: NetworkResult<String>) -> Unit, emailOrPhone: String, password:String, confirmPassword:String){
        repository.resetPassword({ successCallback(it) }, emailOrPhone,password,confirmPassword)
    }

}
package com.yesitlabs.mykaapp.fragment.commonfragmentscreen.eatingOut.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EatingOutViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun getEatingOut(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getEatingOut { successCallback(it) }
    }

}
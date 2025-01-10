package com.mykameal.planner.fragment.commonfragmentscreen.partnerinfoscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PartnerInfoViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        repository.userPreferencesApi { successCallback(it) }
    }

    suspend fun updatePartnerInfoApi(successCallback: (response: NetworkResult<String>) -> Unit, partnerName: String?, partnerAge: String?,partnerGender:String?){
        repository.updatePartnerInfoApi ({ successCallback(it) },partnerName,partnerAge,partnerGender)
    }

}
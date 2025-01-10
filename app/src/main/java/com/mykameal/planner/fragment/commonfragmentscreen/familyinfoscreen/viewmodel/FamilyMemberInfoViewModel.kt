package com.mykameal.planner.fragment.commonfragmentscreen.familyinfoscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FamilyMemberInfoViewModel@Inject constructor(private val repository: MainRepository) : ViewModel() {

    suspend fun userPreferencesApi(successCallback: (response: NetworkResult<String>) -> Unit) {
        repository.userPreferencesApi { successCallback(it) }
    }

    suspend fun updatePartnerInfoApi(successCallback: (response: NetworkResult<String>) -> Unit, familyName: String?, familyAge: String?, familyStatus:String?){
        repository.updateFamilyInfoApi ({ successCallback(it) },familyName,familyAge,familyStatus)
    }

}
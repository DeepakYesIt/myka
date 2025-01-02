package com.yesitlabs.mykaapp.fragment.authfragment.locationfragment.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val repository: MainRepository) :
    ViewModel() {

    suspend fun updateLocation(
        successCallback: (response: NetworkResult<String>) -> Unit,
        locationStatus: String
    ) {
        repository.updateLocation({ successCallback(it) }, locationStatus)
    }
}
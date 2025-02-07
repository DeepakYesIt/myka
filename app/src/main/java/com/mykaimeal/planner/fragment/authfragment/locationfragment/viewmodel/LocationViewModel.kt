package com.mykaimeal.planner.fragment.authfragment.locationfragment.viewmodel

import androidx.lifecycle.ViewModel
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.repository.MainRepository
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
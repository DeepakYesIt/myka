package com.yesitlabs.mykaapp.fragment.authfragment.notificationmodel.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: MainRepository) :
    ViewModel() {

    suspend fun updateNotification(
        successCallback: (response: NetworkResult<String>) -> Unit,
        notificationStatus: String
    ) {
        repository.updateNotification({ successCallback(it) }, notificationStatus)
    }
}
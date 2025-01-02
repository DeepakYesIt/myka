package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting.privacypolicy.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel@Inject constructor(private val repository: MainRepository) : ViewModel() {

    suspend fun getPrivacyPolicy(successCallback: (response: NetworkResult<String>) -> Unit) {
        repository.privacyPolicy { successCallback(it) }
    }

}
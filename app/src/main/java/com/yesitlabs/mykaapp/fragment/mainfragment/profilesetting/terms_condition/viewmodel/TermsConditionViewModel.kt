package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting.terms_condition.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsConditionViewModel @Inject constructor(private val repository: MainRepository) :
    ViewModel() {

    suspend fun getTermCondition(successCallback: (response: NetworkResult<String>) -> Unit) {
        repository.termCondition { successCallback(it) }
    }

}
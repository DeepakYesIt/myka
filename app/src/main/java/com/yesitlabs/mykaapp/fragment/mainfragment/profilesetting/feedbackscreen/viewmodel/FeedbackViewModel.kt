package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting.feedbackscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel

class FeedbackViewModel@Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun saveFeedback(successCallback: (response: NetworkResult<String>) -> Unit, email: String, message:String){
        repository.saveFeedback({ successCallback(it) }, email,message)
    }

}
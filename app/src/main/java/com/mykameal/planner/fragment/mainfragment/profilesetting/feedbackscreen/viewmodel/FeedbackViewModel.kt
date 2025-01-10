package com.mykameal.planner.fragment.mainfragment.profilesetting.feedbackscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel

class FeedbackViewModel@Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun saveFeedback(successCallback: (response: NetworkResult<String>) -> Unit, email: String, message:String){
        repository.saveFeedback({ successCallback(it) }, email,message)
    }

}
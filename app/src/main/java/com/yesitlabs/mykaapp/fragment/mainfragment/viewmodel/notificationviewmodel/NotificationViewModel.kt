package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.notificationviewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun notificationRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    pushNotification: String,recipeRecommendations: String,productUpdates: String,promotionalUpdates: String){
        repository.notificationRequestApi({ successCallback(it) },pushNotification,recipeRecommendations,productUpdates,promotionalUpdates)
    }

}
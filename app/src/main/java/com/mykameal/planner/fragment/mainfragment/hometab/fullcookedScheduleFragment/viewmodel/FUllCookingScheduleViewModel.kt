package com.mykameal.planner.fragment.mainfragment.hometab.fullcookedScheduleFragment.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FUllCookingScheduleViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun fullCookingSchedule(successCallback: (response: NetworkResult<String>) -> Unit,
                                date: String,planType:String){
        repository.planDateRequestApi({ successCallback(it) },date,planType)
    }
    suspend fun likeUnlikeRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                  uri: String,likeType: String,type:String) {
        repository.likeUnlikeRequestApi({ successCallback(it) }, uri, likeType, type)
    }
    suspend fun getCookBookRequest(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCookBookRequestApi { successCallback(it) }
    }

    suspend fun removeMealApi(successCallback: (response: NetworkResult<String>) -> Unit,
                              cookedId: String){
        repository.removeMealApi({ successCallback(it) },cookedId)
    }

}
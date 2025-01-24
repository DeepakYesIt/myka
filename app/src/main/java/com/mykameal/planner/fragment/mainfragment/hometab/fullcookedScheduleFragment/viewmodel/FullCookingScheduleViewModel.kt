package com.mykameal.planner.fragment.mainfragment.hometab.fullcookedScheduleFragment.viewmodel

import androidx.lifecycle.ViewModel
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullCookingScheduleViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun fullCookingSchedule(successCallback: (response: NetworkResult<String>) -> Unit,
                                date: String,planType:String){
        repository.getScheduleApi({ successCallback(it) },date,planType)
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
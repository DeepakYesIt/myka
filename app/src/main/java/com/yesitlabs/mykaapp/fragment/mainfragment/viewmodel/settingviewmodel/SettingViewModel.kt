package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.settingviewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.Data
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    var localData: Data?=null


    suspend fun userProfileData(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.userProfileDataApi { successCallback(it) }
    }


    fun setProfileData(data: Data) {
        localData=data
    }

    // Method to clear data
    fun clearData() {
        localData = null // Clear LiveData
        // Reset other variables
    }

    fun getProfileData(): Data? {
        return localData
    }


    suspend fun upDateProfileRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                      name: String
                                     ,bio:String
                                     ,genderType: String
                                     ,dob:String
                                     ,height:String
                                     ,heightType:String,
                                     activityLevel:String
                                     ,heightProtein:String
                                     ,calories:String
                                     ,fat:String
                                     ,carbs:String
                                     ,protien:String){
        repository.upDateProfileRequestApi({ successCallback(it) },  name,bio,genderType,dob,height,heightType,activityLevel,heightProtein,calories,fat,carbs,protien)
    }

    suspend fun upDateImageNameRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                      Image: MultipartBody.Part?,name: RequestBody){
        repository.upDateImageNameRequestApi({ successCallback(it) },  Image,name)
    }

}
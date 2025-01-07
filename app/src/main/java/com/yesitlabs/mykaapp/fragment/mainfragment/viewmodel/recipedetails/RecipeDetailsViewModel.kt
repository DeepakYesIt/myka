package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {


    suspend fun recipeDetailsRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                    url: String){
        repository.recipeDetailsRequestApi({ successCallback(it) },url)
    }



}
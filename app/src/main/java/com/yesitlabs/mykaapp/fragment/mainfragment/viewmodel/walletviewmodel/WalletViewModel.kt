package com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel

import androidx.lifecycle.ViewModel
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@HiltViewModel
class WalletViewModel @Inject constructor(private val repository: MainRepository) : ViewModel()  {

    suspend fun addCardRequest(successCallback: (response: NetworkResult<String>) -> Unit,
                                      token: String){
        repository.addCardRequestApi({ successCallback(it) },token)
    }

    suspend fun getCardAndBankRequest(successCallback: (response: NetworkResult<String>) -> Unit){
        repository.getCardAndBankRequestApi { successCallback(it) }
    }

    suspend fun deleteCardRequest(successCallback: (response: NetworkResult<String>) -> Unit,cardId: String,customerId: String){
        repository.deleteCardRequestApi ({ successCallback(it) },cardId,customerId)
    }

    suspend fun countryStateCityRequest(successCallback: (response: NetworkResult<String>) -> Unit,url: String){
        repository.countryRequestApi ({ successCallback(it) },url)
    }






    suspend fun addBankRequest(
        successCallback: (response: NetworkResult<String>) -> Unit,
        filePartFront: MultipartBody.Part?,
        filePartBack: MultipartBody.Part?,
        filePart: MultipartBody.Part?,
        firstNameBody: RequestBody,
        lastNameBody: RequestBody,
        emailBody: RequestBody,
        phoneBody: RequestBody,
        dobBody: RequestBody,
        personalIdentificationNobody: RequestBody,
        idTypeBody: RequestBody,
        ssnBody: RequestBody,
        addressBody: RequestBody,
        countryBody: RequestBody,
        shortStateNameBody: RequestBody,
        cityBody: RequestBody,
        postalCodeBody: RequestBody,
        bankDocumentTypeBody: RequestBody,
        deviceTypeBody: RequestBody,
        tokenTypeBody: RequestBody,
        stripeTokenBody: RequestBody,
        saveCardBody: RequestBody,
        amountBody: RequestBody,
        paymentTypeBody: RequestBody,
        bankIdBody: RequestBody
    ){
        repository.addBankRequestApi ({ successCallback(it) },filePartFront
            ,filePartBack,filePart,firstNameBody,lastNameBody,emailBody,phoneBody,dobBody
            ,personalIdentificationNobody,idTypeBody,ssnBody,addressBody,countryBody,shortStateNameBody
            ,cityBody,postalCodeBody,bankDocumentTypeBody,deviceTypeBody,tokenTypeBody,stripeTokenBody
            ,saveCardBody,amountBody,paymentTypeBody,bankIdBody)
    }


}
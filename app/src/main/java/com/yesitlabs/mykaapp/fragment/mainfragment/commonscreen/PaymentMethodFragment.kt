package com.yesitlabs.mykaapp.fragment.mainfragment.commonscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.skydoves.powerspinner.PowerSpinnerView
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.BankAccountTokenParams
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.MyWalletAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentPaymentMethodBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.WalletViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponsecard.CradApiResponse
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponsecard.Data
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.walletviewmodel.apiresponsecountry.CountryResponseModel
import com.yesitlabs.mykaapp.listener.CardBankListener
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar
import java.util.Objects


class PaymentMethodFragment : Fragment(), CardBankListener {

    private lateinit var binding: FragmentPaymentMethodBinding
    private lateinit var viewModel: WalletViewModel
    private var stripe: Stripe? = null
    private var month:Int=0
    private var year:Int=0
    private lateinit var adapterCard: MyWalletAdapter
    private var dataLocal: MutableList<Data> = mutableListOf()
    private var storage_permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private var storage_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
    /*private val mediaFiles: ArrayList<MediaFile?> = ArrayList<MediaFile?>()
    private val REQUEST_Folder = 2*/
    private var imgtype:String=""
    private var filefrontid:String="No"
    private var filebackid:String="No"
    private var filebankid:String="No"
    private var filefront: File? =null
    private var fileback: File? =null
    private var bankuploadfile: File? =null
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private var country:String=""
    private var states:String=""
    private var currency:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentMethodBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[WalletViewModel::class.java]
        stripe = Stripe(requireContext(),getString(R.string.publish_key))
        (activity as MainActivity).binding?.apply {
            llIndicator.visibility = View.VISIBLE
            llBottomNavigation.visibility = View.VISIBLE
        }

        ActivityCompat.requestPermissions(requireActivity(), permissions(), REQUEST_CODE_STORAGE_PERMISSION)


        setupBackNavigation()

        setupUI()

        // Add Refresh Api
        // Set up swipe-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Perform your refresh operation
            fetchDataOnLoad()
        }


        // Add Card Api Event
        setUpAddCardEvent()

        // Add Bank Api Event
        setUpBankEvent()

        setupSpinners()

        // When screen load then api call
        fetchCountry()



//        // When screen load then api call
//        fetchDataOnLoad()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun fetchStates(value: String) {
        if (BaseApplication.isOnline(requireActivity())) {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                viewModel.countryStateCityRequest({
                    BaseApplication.dismissMe()
                    handleApiStatesResponse(it,value)
                }, "https://api.countrystatecity.in/v1/countries/$value/states/")
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchCity(value: String, iso2: String) {
        if (BaseApplication.isOnline(requireActivity())) {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                viewModel.countryStateCityRequest({
                    BaseApplication.dismissMe()
                    handleApiCitiesResponse(it, value)
                }, "https://api.countrystatecity.in/v1/countries/$value/states/$iso2/cities/")
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun handleApiStatesResponse(result: NetworkResult<String>, value: String) {
        when (result) {
            is NetworkResult.Success -> handleStatesSuccessResponse(result.data.toString(),value)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleApiCitiesResponse(result: NetworkResult<String>, value: String) {
        when (result) {
            is NetworkResult.Success -> handleCitiesSuccessResponse(result.data.toString(),value)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    private fun fetchCountry(){
        if (BaseApplication.isOnline(requireActivity())) {
            BaseApplication.showMe(requireContext())
            lifecycleScope.launch {
                viewModel.countryStateCityRequest({
                    BaseApplication.dismissMe()
                    handleApiCountryResponse(it)
                }, "https://api.countrystatecity.in/v1/countries/")
            }
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }


    private fun setupSpinners() {
        setupSpinner(binding.spinnerSelectIDType, listOf("Driver license", "Passport"))
        setupSpinner(binding.spinnerSelectOption, listOf("Bank account statement", "Voided cheque", "Bank letterhead"))
    }

    private fun setupSpinner(spinner: PowerSpinnerView, items: List<String>) {
        spinner.setItems(items)
        spinner.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) spinner.show() else spinner.dismiss()
        }
        spinner.setIsFocusable(true)
    }


    private fun setUpBankEvent(){
        binding.textAddBank.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                if (isValidation()){
                   addBankApi()
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun addBankApi(){
        BaseApplication.showMe(requireContext())
        val bankAccountTokenParams = BankAccountTokenParams(country, currency,
            binding.etBankAccountNumber.text?.toString()?.trim() ?: throw IllegalArgumentException("Bank number is required"),
            BankAccountTokenParams.Type.Individual,
            binding.etBankName.text.toString(),
            binding.etRoutingNumber.text?.toString()?.trim() ?: throw IllegalArgumentException("Routing number is required"))



            stripe?.createBankAccountToken(bankAccountTokenParams ,null,null, object : ApiResultCallback<Token> {
            override fun onSuccess(token: Token) {
                lifecycleScope.launch {
                    addCard(token)
                }
            }
            override fun onError(e: Exception) {
                BaseApplication.dismissMe()
                showAlert(e.message, false)
            }
             })



    }

    private suspend fun addCard(token:Token){

        try {
            val filePartFront: MultipartBody.Part? = if (filefront != null) {
                val requestBody = filefront?.asRequestBody(filefront!!.extension.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("document_front", filefront?.name,requestBody!!)
            }else{
                null
            }
            val filePartBack: MultipartBody.Part? = if (fileback != null) {
                val requestBody = fileback?.asRequestBody(fileback!!.extension.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("document_back", fileback?.name,requestBody!!)
            }else{
                null
            }

            val filePart: MultipartBody.Part? = if (bankuploadfile != null) {
                val requestBody = bankuploadfile?.asRequestBody(bankuploadfile!!.extension.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("bank_proof", bankuploadfile?.name,requestBody!!)
            }else{
                null
            }

            val firstNameBody= binding.etFirstName.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val lastNameBody= binding.textLastName.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val emailBody= binding.etEmail.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val phoneBody= binding.etPhoneNumber.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val dobBody= binding.etDOB.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val idTypeBody = binding.spinnerSelectIDType.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val personalIdentificationNobody= binding.etPersonalIdentificationNumber.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val ssnBody= binding.etSSN.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val addressBody= binding.etAddress.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val countryBody= country.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val shortStateNameBody= states.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val cityBody= binding.spinnerSelectCity.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val postalCodeBody= binding.etPostalCode.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val bankDocumentTypeBody = when (binding.spinnerSelectOption.getText().toString()) {
                "Bank account statement" -> "statement".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                "Voided cheque" -> "cheque".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                else -> "letterhead".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }
            val deviceTypeBody= "Android".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val tokenTypeBody= "bank_account".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val stripeTokenBody= token.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val saveCardBody= "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val amountBody= "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val paymentTypeBody= "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val bankIdBody= "".toRequestBody("multipart/form-data".toMediaTypeOrNull())

            viewModel.addBankRequest ({
                BaseApplication.dismissMe()
                handleApiBankResponse(it)
            }   ,filePartFront
                ,filePartBack
                ,filePart
                ,firstNameBody
                ,lastNameBody
                ,emailBody
                ,phoneBody
                ,dobBody
                ,personalIdentificationNobody
                ,idTypeBody
                ,ssnBody
                ,addressBody
                ,countryBody
                ,shortStateNameBody
                ,cityBody
                ,postalCodeBody
                ,bankDocumentTypeBody
                ,deviceTypeBody
                ,tokenTypeBody
                ,stripeTokenBody
                ,saveCardBody
                ,amountBody
                ,paymentTypeBody
                ,bankIdBody)

        }catch (e:Exception){
            BaseApplication.dismissMe()
            showAlert(e.message, false)
        }

    }

    private fun handleApiBankResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleBankSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun isValidation(): Boolean {
        if (binding.etFirstName.text.trim().toString().trim().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.firstNameError, false)
            return false
        } else if (binding.etLastName.text?.trim().toString().trim().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.lastNameError, false)
            return false
        } else if (binding.etEmail.text?.trim().toString().trim().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.emailError, false)
            return false
        } else if (!binding.etEmail.text?.trim().toString().contains("@")) {
            BaseApplication.alertError(requireContext(), ErrorMessage.validEmail, false)
            return false
        } else if (binding.etPhoneNumber.text?.trim().toString().trim().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.phoneError, false)
            return false
        }else if (binding.etPhoneNumber.text?.trim().toString().length!=10) {
            BaseApplication.alertError(requireContext(), ErrorMessage.validPhoneNumber, false)
            return false
        }else if (binding.etDOB.text?.toString().equals("MM/DD/YYYY")) {
            BaseApplication.alertError(requireContext(), ErrorMessage.dobError, false)
            return false
        }else if (binding.spinnerSelectIDType.text?.toString().equals("Select ID type")) {
            BaseApplication.alertError(requireContext(), ErrorMessage.selectIdTypeError, false)
            return false
        } else if (binding.etPersonalIdentificationNumber.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.pINError, false)
            return false
        }  else if (binding.etSSN.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.SNNError, false)
            return false
        } else if (binding.etSSN.text?.trim().toString().length!=4) {
            BaseApplication.alertError(requireContext(), ErrorMessage.SNNValidError, false)
            return false
        } else if (binding.etAddress.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.addressError, false)
            return false
        } else if (binding.spinnerSelectCountry.text?.toString().equals("Select Country")) {
            BaseApplication.alertError(requireContext(), ErrorMessage.countryError, false)
            return false
        }  else if (binding.spinnerSelectState.text?.toString().equals("Select State")) {
            BaseApplication.alertError(requireContext(), ErrorMessage.stateError, false)
            return false
        }  else if (binding.spinnerSelectCity.text?.toString().equals("Select City")) {
            BaseApplication.alertError(requireContext(), ErrorMessage.cityError, false)
            return false
        }   else if (binding.etPostalCode.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.postalCodeError, false)
            return false
        }  else if (binding.etBankName.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.bankNameError, false)
            return false
        }   else if (binding.etAccountHolderName.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.cardholderError, false)
            return false
        }  else if (binding.etBankAccountNumber.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.accountNumberError, false)
            return false
        }  else if (binding.etConfirmAccountNumber.text?.trim().toString().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.cAccountNumberError, false)
            return false
        } else if (binding.etRoutingNumber.text.toString().trim().isEmpty()) {
            BaseApplication.alertError(requireContext(), ErrorMessage.routingNumberError, false)
            return false
        }else if (filebankid.equals("No",true)) {
            BaseApplication.alertError(requireContext(), ErrorMessage.proofofbanError, false)
            return false
        }else if (filefrontid.equals("No",true)) {
            BaseApplication.alertError(requireContext(), ErrorMessage.frontimageError, false)
            return false
        }else if (filebackid.equals("No",true)) {
            BaseApplication.alertError(requireContext(), ErrorMessage.backimageError, false)
            return false
        }

        return true
    }

    private fun fetchDataOnLoad() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchUserBankAndCardData()
        } else {
            binding.swipeRefreshLayout.isRefreshing = false
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchUserBankAndCardData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.getCardAndBankRequest { result ->
                BaseApplication.dismissMe()
                binding.swipeRefreshLayout.isRefreshing = false
                handleApiResponse(result)
            }
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> processSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun processSuccessResponse(response: String) {
        try {
            val apiModel = Gson().fromJson(response, CradApiResponse::class.java)
            Log.d("@@@ Response cardBank ", "message :- $response")
            if (apiModel.code == 200) {
                apiModel.data?.let { updateUI(it) }?: run {
                    // Code for the else condition
                    binding.llBankAccount.visibility=View.VISIBLE
                    binding.llSavedBankAccountDetails4.visibility=View.GONE
                }
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }

    private fun updateUI(data: MutableList<Data>) {
        dataLocal.clear()
        dataLocal.addAll(data)
        if (dataLocal.size>0){
            binding.llBankAccount.visibility=View.GONE
            binding.llSavedBankAccountDetails4.visibility=View.VISIBLE
            adapterCard = MyWalletAdapter(requireContext(), dataLocal,"card",this)
            binding.rcvCardNumber.adapter = adapterCard
        }else{
            binding.llBankAccount.visibility=View.VISIBLE
            binding.llSavedBankAccountDetails4.visibility=View.GONE
            fetchDataOnLoad()
        }
    }


    private fun setupUI() {

        binding.imgWallet.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.textAddCardNumber.setOnClickListener {
            binding.llBankAccount.visibility=View.VISIBLE
            binding.llSavedBankAccountDetails4.visibility=View.GONE
        }

        binding.etMonth.setOnClickListener {
            openMonthPickerBox()
        }

        binding.etYear.setOnClickListener {
            openYearPickerBox()
        }

        binding.textBankAccountToggle.setOnClickListener {
            toggleBankAndCardView(true)
        }

        binding.textDebitCardToggle.setOnClickListener {
            toggleBankAndCardView(false)
        }

        binding.rlDOB.setOnClickListener {
            openCalendarBox()
        }

        binding.imguploaddocument.setOnClickListener {

            if (hasPermissions(requireContext(), *permissions())) {
                val dialog = Dialog(requireContext(), R.style.BottomSheetDialog)
                dialog.setContentView(R.layout.alert_box_gallery_pdf)
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window!!.attributes)
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog.window!!.attributes = layoutParams
                val laygallery: LinearLayout = dialog.findViewById(R.id.lay_gallery)
                val laycamera: LinearLayout = dialog.findViewById(R.id.lay_camera)
                val view1: View = dialog.findViewById(R.id.view1)
                val laypdf: LinearLayout = dialog.findViewById(R.id.lay_pdf)
                view1.visibility = View.VISIBLE
                laycamera.visibility = View.VISIBLE
                laycamera.setOnClickListener {
                    dialog.dismiss()
                    imgtype = "camera"
                    ImagePicker.with(this)
                        .cameraOnly()
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start()
                }

                laygallery.setOnClickListener {
                    dialog.dismiss()
                    imgtype = "Gallery"
                    ImagePicker.with(this)
                        .galleryOnly()
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start()
                }

                laypdf.setOnClickListener{
                    imgtype = "pdffile"
                    dialog.dismiss()
//                    fileIntentMulti()
                }

                dialog.show()
            } else {
                Toast.makeText(requireContext(), "Please go to setting Enable Permission", Toast.LENGTH_SHORT).show()
            }
        }


        binding.layFront.setOnClickListener {
            imgtype = "front"
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.layBack.setOnClickListener {
            imgtype="back"
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }



    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (data?.data!=null){
                if (imgtype .equals( "front",true)){
                    val uri = data.data!!
                    filefront = BaseApplication.getPath(requireContext(), uri)?.let { File(it) }
                    filefrontid="Yes"
                    binding.textChooseVerificationDocument.text=filefront.toString()

                }
                if (imgtype.equals("back",true)){
                    val uri = data.data!!
                    fileback = BaseApplication.getPath(requireContext(), uri)?.let { File(it) }
                    filebackid="Yes"
                    binding.textChooseVerificationDocumentBack.text=fileback.toString()

                }

                if (imgtype.equals("camera",true)){
                    val uri = data.data!!
                    bankuploadfile = BaseApplication.getPath(requireContext(), uri)?.let { File(it) }
                    binding.textChooseBankProof.text= bankuploadfile.toString()
                    filebankid="Yes"

                }
                if (imgtype.equals("Gallery",true)){
                    val uri = data.data!!
                    bankuploadfile = BaseApplication.getPath(requireContext(), uri)?.let { File(it) }
                    filebankid="Yes"
                    binding.textChooseBankProof.text= bankuploadfile.toString()

                }
            }
        }
        /*if (requestCode == REQUEST_Folder) {
            data?.let { onSelectFromFolderResult(it) }
        }*/
    }

    private fun permissions(): Array<String> {
        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storage_permissions_33
        } else {
            storage_permissions
        }
        return p
    }



    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    // This function is use for open the Calendar
    @SuppressLint("SetTextI18n")
    private fun openCalendarBox() {
        // Get the current calendar instance
        val calendar = Calendar.getInstance()

        // Extract the current year, month, and day
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog with the current date and minimum date set to today
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Update the TextView with the selected date
                binding.etDOB.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            },
            year,
            month,
            day
        )

        // Disable previous dates
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        // Show the date picker dialog
        datePickerDialog.show()
    }


    private fun toggleBankAndCardView(showBank: Boolean) {
        binding.textBankAccountToggle.setBackgroundResource(
            if (showBank) R.drawable.selected_green_toogle_bg else 0
        )
        binding.textDebitCardToggle.setBackgroundResource(
            if (!showBank) R.drawable.selected_green_toogle_bg else 0
        )
        binding.textBankAccountToggle.setTextColor(
            Color.parseColor(if (showBank) "#FFFFFF" else "#06C169")
        )
        binding.textDebitCardToggle.setTextColor(
            Color.parseColor(if (!showBank) "#FFFFFF" else "#06C169")
        )
        binding.cvBankAccount2.visibility = if (showBank) View.VISIBLE else View.GONE
        binding.cvDebitCard3.visibility = if (showBank) View.GONE else View.VISIBLE
    }


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    private fun openYearPickerBox() {
        // Get the current calendar instance
        val calendar = Calendar.getInstance()
        // Extract the current year and month
        val currentYear = calendar.get(Calendar.YEAR)
        // Create a dialog
        val dialog = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_month_picker, null)
        // Get references to the NumberPickers in the custom dialog layout
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.Picker)
        // Configure the year picker
        monthPicker.minValue = currentYear
        monthPicker.maxValue = currentYear + 50 // Limit to 50 years ahead
        monthPicker.value = currentYear
        // Set the custom view in the dialog
        dialog.setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Get selected month and year
                val selectedMonth = monthPicker.value
                year=monthPicker.value
                // Update the TextView with the selected month name and year
                binding.etYear.text = ""+selectedMonth
            }
            .setNegativeButton("Cancel", null)
        // Show the dialog
        dialog.create().show()
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    private fun openMonthPickerBox() {
        // Get the current calendar instance
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        // Array of month names
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        // Create a dialog
        val dialog = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_month_picker, null)
        // Get references to the NumberPickers in the custom dialog layout
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.Picker)
        // Configure the month picker
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = monthNames
        monthPicker.value = currentMonth
        // Set the custom view in the dialog
        dialog.setView(dialogView)
            .setPositiveButton("OK") { _, _ ->

                val selectedMonth = monthPicker.value
                month=monthPicker.value+1
                // Update the TextView with the selected month name and year
                binding.etMonth.text = monthNames[selectedMonth]
                Toast.makeText(requireContext(), "selectedMonth :- $selectedMonth", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)

        // Show the dialog
        dialog.create().show()
    }


    private fun setUpAddCardEvent() {

        binding.textAddCardDebitCard.setOnClickListener {
            if (BaseApplication.isOnline(requireActivity())) {
                if (isValidationCard()){
                    paymentApi()
                }
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    private fun paymentApi() {
        BaseApplication.showMe(requireContext())
        val cardNumber: String = Objects.requireNonNull(binding.etCardNumber.text.toString()).toString()
        val cvvNumber: String = Objects.requireNonNull(binding.etCVVNumber.text.toString()).toString()
        val name: String = binding.etName.text.toString()
        val card = CardParams(cardNumber, Integer.valueOf(month), Integer.valueOf(year), cvvNumber, name)
        stripe!!.createCardToken(card, null, null, object : ApiResultCallback<Token> {
            override fun onError(e: Exception) {
                BaseApplication.dismissMe()
                Log.d("PaymentActivity1", "data$e")
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: Token) {
                val id = result.id
                Log.d("@@@Token:-", "data$id")
                saveCardApi(id)
            }
        })
    }

    private fun saveCardApi(id: String) {
        lifecycleScope.launch {
            viewModel.addCardRequest({
                BaseApplication.dismissMe()
                handleApiUpdateResponse(it)
            }, id)
        }
    }

    private fun handleApiUpdateResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleUpdateSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleApiCountryResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleCountrySuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    private fun handleApiDeleteCardResponse(
        result: NetworkResult<String>,
        position: Int?,
        type: String
    ) {
        when (result) {
            is NetworkResult.Success -> handleDeleteCardSuccessResponse(result.data.toString(),position,type)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    @SuppressLint("SetTextI18n")
    private fun handleUpdateSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Add Card", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {

                month=0
                year=0
                binding.etName.text.clear()
                binding.etCardNumber.text.clear()
                binding.etCVVNumber.text.clear()
                binding.etMonth.text="Month"
                binding.etYear.text="Year"

                Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()

                // When screen load then api call
                fetchDataOnLoad()

            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun handleCountrySuccessResponse(data: String) {
        try {
            val apiModelCountry = Gson().fromJson(data, CountryResponseModel::class.java)
            Log.d("@@@ Add Card", "message :- $data")
            if (apiModelCountry.code == 200 && apiModelCountry.success) {
                if (apiModelCountry.data!=null){
                    val localList :MutableList<String> = mutableListOf()
                    localList.clear()
                    for (value in apiModelCountry.data ){
                        localList.add(value.name)
                    }
                    binding.spinnerSelectCountry.setItems(localList)
                    binding.spinnerSelectCountry.setIsFocusable(true)
                    binding.spinnerSelectCountry.setOnSpinnerItemSelectedListener<String> {
                        oldIndex, oldItem, newIndex, newItem ->
                        country = apiModelCountry.data[newIndex].iso2
                        currency=apiModelCountry.data[newIndex].currency
                        // When screen load then api call
                        fetchStates(country)
                    }

                }
            } else {
                if (apiModelCountry.code == ErrorMessage.code) {
                    showAlert(apiModelCountry.message, true)
                } else {
                    showAlert(apiModelCountry.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun handleStatesSuccessResponse(data: String, value: String) {
        try {
            val apiModelCountry = Gson().fromJson(data, CountryResponseModel::class.java)
            Log.d("@@@ Add Card", "message :- $data")
            if (apiModelCountry.code == 200 && apiModelCountry.success) {
                if (apiModelCountry.data!=null){
                    val localList :MutableList<String> = mutableListOf()
                    localList.clear()
                    for (value in apiModelCountry.data ){
                        localList.add(value.name)
                    }
                    binding.spinnerSelectState.setItems(localList)
                    binding.spinnerSelectState.setIsFocusable(true)
                    binding.spinnerSelectState.setOnSpinnerItemSelectedListener<String> {
                        oldIndex, oldItem, newIndex, newItem ->
                        states = apiModelCountry.data[newIndex].iso2
                        // When screen load then api call
                        fetchCity(value,states)
                    }

                }
            } else {
                if (apiModelCountry.code == ErrorMessage.code) {
                    showAlert(apiModelCountry.message, true)
                } else {
                    showAlert(apiModelCountry.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun handleCitiesSuccessResponse(data: String, value: String) {
        try {
            val apiModelCountry = Gson().fromJson(data, CountryResponseModel::class.java)
            Log.d("@@@ Add Card", "message :- $data")
            if (apiModelCountry.code == 200 && apiModelCountry.success) {
                if (apiModelCountry.data!=null){
                    val localList :MutableList<String> = mutableListOf()
                    localList.clear()
                    for (value in apiModelCountry.data ){
                        localList.add(value.name)
                    }
                    binding.spinnerSelectCity.setItems(localList)
                    binding.spinnerSelectCity.setIsFocusable(true)
                }
            } else {
                if (apiModelCountry.code == ErrorMessage.code) {
                    showAlert(apiModelCountry.message, true)
                } else {
                    showAlert(apiModelCountry.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }

    private fun handleBankSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Add Card", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()
                // When screen load then api call
                fetchDataOnLoad()
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }


    private fun handleDeleteCardSuccessResponse(data: String, position: Int?, type: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Add Card", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()
                dataLocal.removeAt(position!!)
                if (dataLocal.size>0){
                    adapterCard.upDateList(dataLocal,type)
                    binding.llBankAccount.visibility=View.GONE
                    binding.llSavedBankAccountDetails4.visibility=View.VISIBLE
                }else{
                    binding.llBankAccount.visibility=View.VISIBLE
                    binding.llSavedBankAccountDetails4.visibility=View.GONE
                }

            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }

    }

    private fun isValidationCard(): Boolean {
        if (binding.etName.text.toString().trim().isEmpty()){
            BaseApplication.alertError(requireContext(), ErrorMessage.cardholderError, false)
            return false
        }else if (binding.etCardNumber.text.toString().trim().isEmpty()){
            BaseApplication.alertError(requireContext(), ErrorMessage.cardNumberError, false)
            return false
        }else if (binding.etCVVNumber.text.toString().trim().isEmpty()){
            BaseApplication.alertError(requireContext(), ErrorMessage.cvvError, false)
            return false
        }else if (binding.etCVVNumber.text.toString().length ==1 || binding.etCVVNumber.text.toString().length ==2 ){
            BaseApplication.alertError(requireContext(), ErrorMessage.cvvValidError, false)
            return false
        }else if (binding.etMonth.text.toString().equals("Month",true)){
            BaseApplication.alertError(requireContext(), ErrorMessage.monthError, false)
            return false
        }else if (binding.etYear.text.toString().equals("Year",true)){
            BaseApplication.alertError(requireContext(), ErrorMessage.yearError, false)
            return false
        }

        return true
    }


    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    override fun itemSelect(position: Int?, status: String?, type: String?) {
         if (type!!.equals("card",true)){
             showWithdrawAmountDialog(position)
         }

        if (type.equals("delete",true)){
            if (BaseApplication.isOnline(requireActivity())) {
                deleteApi(position,type)
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }

    }


    private fun deleteApi(position: Int?, type: String){
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.deleteCardRequest({
                BaseApplication.dismissMe()
                handleApiDeleteCardResponse(it,position,type)
            }, dataLocal[position!!].card_id.toString(),dataLocal[position].customer_id.toString())
        }
    }

    private fun showWithdrawAmountDialog(position: Int?) {

        val dialog = Dialog(requireContext(), R.style.BottomSheetDialog).apply {
            setCancelable(false)
            setContentView(R.layout.alert_dialog_withdraw_amount)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.attributes = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }

            findViewById<ImageView>(R.id.imageCross).setOnClickListener {
                dismiss()
            }

            findViewById<RelativeLayout>(R.id.rlWithdrawAmountButton).setOnClickListener {
                dismiss()
            }
        }


        dialog.show()
    }


}
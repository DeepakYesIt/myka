package com.mykameal.planner.fragment.mainfragment.hometab.createcookbookfragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.commonworkutils.CommonWorkUtils
import com.mykameal.planner.databinding.FragmentCreateCookBookBinding
import com.mykameal.planner.fragment.mainfragment.hometab.createcookbookfragment.model.CreateCookBookModel
import com.mykameal.planner.fragment.mainfragment.hometab.createcookbookfragment.viewmodel.CreateCookBookViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykameal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


@AndroidEntryPoint
class CreateCookBookFragment : Fragment() {
    private var binding: FragmentCreateCookBookBinding? = null
    private var isOpened: Boolean? = null
    private var checkType: String? = null
    private var uri: String? = null
    private var file: File? = null
    private lateinit var commonWorkUtils: CommonWorkUtils
    private lateinit var createCookBookViewModel: CreateCookBookViewModel
    private val selectedButton = arrayOf<RadioButton?>(null)
    private var status:String=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateCookBookBinding.inflate(layoutInflater, container, false)

        createCookBookViewModel = ViewModelProvider(this)[CreateCookBookViewModel::class.java]

        commonWorkUtils = CommonWorkUtils(requireActivity())

        if (arguments != null) {
            checkType = requireArguments().getString("value", "")
            uri = requireArguments().getString("uri", "")
        }

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        return binding!!.root
    }


    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                binding!!.imageCookBook.visibility=View.VISIBLE
                binding!!.llAddImages.visibility=View.GONE
                binding!!.llAddImage.background=null
                file = commonWorkUtils.getPath(requireContext(), uri)?.let { File(it) }
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.mask_group_icon)
                    .error(R.drawable.mask_group_icon)
                    .into(binding!!.imageCookBook)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initialize() {

        if (checkType == "New") {
            binding!!.tvToolbar.text = "Create Cookbook"
        } else {
            binding!!.tvToolbar.text = "Edit Cookbook"
        }

        binding!!.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.imageInfo.setOnClickListener {
            if (isOpened == true) {
                isOpened = false
                binding!!.cvInfoMessage.visibility = View.GONE
            } else {
                isOpened = true
                binding!!.cvInfoMessage.visibility = View.GONE
            }
        }


        binding!!.textDone.setOnClickListener {
            if (validate()) {
                if (BaseApplication.isOnline(requireActivity())) {
                    createCookBookApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }

        }

         // Set listeners for each RadioButton
        binding!!.radioPrivate.setOnClickListener { v: View? ->
            if (selectedButton[0] === binding!!.radioPrivate) {
                // If already selected, deselect it
                binding!!.radioPrivate.isChecked = false
                selectedButton[0] = null
                status=""
            } else {
                // Select the RadioButton and update the selected reference
                binding!!.radioPrivate.isChecked = true
                binding!!.radioPublic.isChecked = false
                selectedButton[0] = binding!!.radioPrivate
                status="0"
            }
        }

        binding!!.radioPublic.setOnClickListener { v: View? ->
            if (selectedButton[0] === binding!!.radioPublic) {
                // If already selected, deselect it
                binding!!.radioPublic.isChecked = false
                selectedButton[0] = null
                status=""
            } else {
                // Select the RadioButton and update the selected reference
                binding!!.radioPublic.isChecked = true
                binding!!.radioPrivate.isChecked = false
                selectedButton[0] = binding!!.radioPublic
                status="1"
            }
        }


        binding!!.llAddImage.setOnClickListener {
            ImagePicker.with(this)
                .crop() // Crop image (Optional)
                .compress(1024 * 5) // Compress the image to less than 5 MB
                .maxResultSize(250, 250) // Set max resolution
                .createIntent { intent -> pickImageLauncher.launch(intent) }
        }
    }

    /// add validation based on valid email or phone
    private fun validate(): Boolean {
        if (file==null) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.cookbookUpload, false)
            return false
        } else if (binding!!.etEnterYourNewCookbook.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.cookbookName, false)
            return false
        } else if (status=="") {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.selectPrivatePublic, false)
            return false
        }
        return true
    }

    private fun createCookBookApi() {
        BaseApplication.showMe(requireActivity())
        lifecycleScope.launch {

            val filePart: MultipartBody.Part? = if (file != null) {
                val requestBody = file?.asRequestBody(file!!.extension.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file?.name, requestBody!!)
            } else {
                null
            }

            val cookBookName = binding!!.etEnterYourNewCookbook.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val cookBookStatus = status.toRequestBody("multipart/form-data".toMediaTypeOrNull())

            createCookBookViewModel.createCookBookApi({
                if (uri.equals("",true)){
                    BaseApplication.dismissMe()
                }
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val createCookBookModel = Gson().fromJson(it.data, CreateCookBookModel::class.java)
                            if (createCookBookModel.code == 200 && createCookBookModel.success) {
                                if (uri.equals("",true)){
                                    Toast.makeText(requireContext(),createCookBookModel.message,Toast.LENGTH_LONG).show()
                                    findNavController().navigateUp()
                                }else{
                                    recipeLikeAndUnlikeData(createCookBookModel.data.id.toString(),createCookBookModel.message)
                                }
                            } else {
                                if (createCookBookModel.code == ErrorMessage.code) {
                                    showAlertFunction(createCookBookModel.message, true)
                                } else {
                                    showAlertFunction(createCookBookModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            BaseApplication.dismissMe()
                            showAlertFunction(e.message, false)
                        }
                    }
                    is NetworkResult.Error -> {
                        showAlertFunction(it.message, false)
                    }else -> {
                        showAlertFunction(it.message, false)
                    }
                }
            }, cookBookName, filePart, cookBookStatus)
        }
    }

    private fun recipeLikeAndUnlikeData(cookbooktype: String, message: String) {
        lifecycleScope.launch {
            createCookBookViewModel.likeUnlikeRequest({
                BaseApplication.dismissMe()
                handleLikeAndUnlikeApiResponse(it,message)
            }, uri!!,"1",cookbooktype)
        }
    }

    private fun handleLikeAndUnlikeApiResponse(result: NetworkResult<String>, message: String) {
        when (result) {
            is NetworkResult.Success -> handleLikeAndUnlikeSuccessResponse(result.data.toString(),message)
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleLikeAndUnlikeSuccessResponse(data: String, message: String) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ Plan List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
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

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun showAlertFunction(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }
}
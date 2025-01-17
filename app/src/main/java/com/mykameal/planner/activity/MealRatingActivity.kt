package com.mykameal.planner.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mykameal.planner.R
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.ActivityMainBinding
import com.mykameal.planner.databinding.ActivityMealRatingBinding
import com.mykameal.planner.databinding.AdapterMealTypeHorizentalBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.RecipeDetailsViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.settingviewmodel.apiresponse.ProfileRootResponse
import com.mykameal.planner.messageclass.ErrorMessage
import kotlinx.coroutines.launch

class MealRatingActivity : AppCompatActivity() {

    private var mealType: String = ""
    private var uri: String = ""

    lateinit var binding: ActivityMealRatingBinding
    private lateinit var viewModel: RecipeDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealRatingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[RecipeDetailsViewModel::class.java]

        mealType = intent?.getStringExtra("mealType").toString()
        uri = intent?.getStringExtra("uri").toString()



        binding.imgBackRateMeal.setOnClickListener {
            finish()
        }

        binding.rlPublishReviews.setOnClickListener {
            if (isValidation()){
                if (BaseApplication.isOnline(this)) {
                    reviewApi()
                } else {
                    BaseApplication.alertError(this, ErrorMessage.networkError, false)
                }
            }
        }

    }

    private fun isValidation(): Boolean {
        if (binding.edMsg.text.toString().trim().isEmpty()){
            BaseApplication.alertError(this, ErrorMessage.ratingError, false)
            return false
        }
        return true
    }

    private fun reviewApi() {
        BaseApplication.showMe(this)
        lifecycleScope.launch {
            viewModel.recipeReviewRequest({
                BaseApplication.dismissMe()
                handleApiUpdateResponse(it)
            },mealType,uri, binding.edMsg.text.toString(),binding.ratingBarSmall.rating.toString())
        }
    }

    private fun handleApiUpdateResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleUpdateSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleUpdateSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, ProfileRootResponse::class.java)
            Log.d("@@@ Health profile", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                 finish()
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
        BaseApplication.alertError(this, message, status)
    }

}
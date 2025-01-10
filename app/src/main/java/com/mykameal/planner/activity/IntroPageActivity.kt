package com.mykameal.planner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.BaseApplication.fetchFcmToken
import com.mykameal.planner.databinding.ActivityIntroPageBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IntroPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use `inflate` method directly without creating an extra object
        binding = ActivityIntroPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize UI and logic
        initialize()



//        Log.d("token :- ","*****"+ BaseApplication.getToken())
    }
    private fun initialize() {
        // Set up click listener for the Next button
        binding.rlNextBtn.setOnClickListener {
            navigateToOnBoarding()
        }
    }
    private fun navigateToOnBoarding() {
        // Navigate to OnBoardingActivity
        val intent = Intent(this, OnBoardingActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity to avoid going back
    }
}

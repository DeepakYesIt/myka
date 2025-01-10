package com.mykameal.planner.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var binding:ActivitySplashBinding?=null
    private lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        sessionManagement = SessionManagement(this)

        ///main function using all triggered of this screen
        initialize()
    }

    private fun initialize() {
        //using handler for showing screen timer
        Handler().postDelayed({
            /* if (!sessionManagement.isOnboardingCompleted()) {
                 val intent = Intent(this@SplashActivity, OnBoardingActivity::class.java)
                 startActivity(intent)
                 finish()
             } else {*/
            /// check login session
            if (sessionManagement.getLoginSession()) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
//                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                val intent = Intent(this@SplashActivity, IntroPageActivity::class.java)
                startActivity(intent)
                finish()
            }
            /*}*/
        }, 3000)
    }
}
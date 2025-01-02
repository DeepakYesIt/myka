package com.yesitlabs.mykaapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var binding:ActivitySplashBinding?=null
    private lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        sessionManagement = SessionManagement(this)

        initialize()
    }

    private fun initialize() {
        Handler().postDelayed({
            /* if (!sessionManagement.isOnboardingCompleted()) {
                 val intent = Intent(this@SplashActivity, OnBoardingActivity::class.java)
                 startActivity(intent)
                 finish()
             } else {*/
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
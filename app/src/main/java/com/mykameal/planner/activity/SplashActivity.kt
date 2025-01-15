package com.mykameal.planner.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManagement: SessionManagement

    companion object {
        private const val SPLASH_DELAY = 3000L // 3 seconds delay
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManagement = SessionManagement(this)
        // Initialize screen actions

        initialize()
    }

    private fun initialize() {
        lifecycleScope.launch {
            delay(SPLASH_DELAY)
            // Check login session and navigate accordingly
            if (sessionManagement.getFirstTime()){
                val intent = Intent(this@SplashActivity, IntroPageActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val targetActivity = if (sessionManagement.getLoginSession()) {
                    MainActivity::class.java
                } else {
                    LetsStartOptionActivity::class.java
                }
                val intent = Intent(this@SplashActivity, targetActivity)
                startActivity(intent)
                finish()
            }



        }
    }
}

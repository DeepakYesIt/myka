package com.mykaimeal.planner.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManagement: SessionManagement

    companion object {
        public const val SPLASH_DELAY = 3000L // 3 seconds delay
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
        navigateNext()
        /*lifecycleScope.launch {
            delay(SPLASH_DELAY)
            // Check login session and navigate accordingly
            val targetActivity = if (sessionManagement.getLoginSession()) {
                MainActivity::class.java
            } else {
                IntroPageActivity::class.java
            }
            val intent = Intent(this@SplashActivity, targetActivity)
            startActivity(intent)
            finish()
        }*/
    }

    private fun navigateNext() {
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


    private fun handleDeepLink(deepLinkValue: String?) {
        when (deepLinkValue) {
            "profile_screen" -> {
                // Navigate to Profile Screen
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("navigate_to", "profile_screen")
                }
                startActivity(intent)
                finish()
            }
            else -> {
                // Handle other deep link values or fallback
                navigateNext()
            }
        }
    }
}

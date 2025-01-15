package com.mykameal.planner.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
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

        // Initialize AppsFlyer SDK
        AppsFlyerLib.getInstance().init("M57zyjkFgb7nSQwHWN6isW", null, this);
        AppsFlyerLib.getInstance().start(this)


        initialize()
    }

    private fun initialize() {

        // Register Conversion Listener
        registerAppsFlyerConversionListener()

        // Subscribe to Deep Links
        subscribeToAppsFlyerDeepLinks()

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


    private fun registerAppsFlyerConversionListener() {
        AppsFlyerLib.getInstance().registerConversionListener(this, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: MutableMap<String, Any>?) {
                Log.d("AppsFlyer11", "Conversion data: $conversionData")
            }

            override fun onConversionDataFail(error: String?) {
                Log.e("AppsFlyer11", "Conversion data error: $error")
            }

            override fun onAppOpenAttribution(attributionData: MutableMap<String, String>?) {
                Log.d("AppsFlyer11", "App open attribution: $attributionData")
            }

            override fun onAttributionFailure(error: String?) {
                Log.e("AppsFlyer11", "Attribution error: $error")
            }
        })
    }

    private fun subscribeToAppsFlyerDeepLinks() {
        AppsFlyerLib.getInstance().subscribeForDeepLink { deepLinkResult ->
            when (deepLinkResult.status) {
                DeepLinkResult.Status.FOUND -> {
                    val deepLinkValue = deepLinkResult.deepLink?.getStringValue("deep_link_value")
                    Log.d("AppsFlyer", "Deep link found: $deepLinkValue")
                    navigateNext()
                  /*  handleDeepLink(deepLinkValue)*/
                }
                DeepLinkResult.Status.NOT_FOUND -> {
                    Log.e("AppsFlyer", "Deep link not found")
                    navigateNext()
                }
                DeepLinkResult.Status.ERROR -> {
                    Log.e("AppsFlyer", "Error in deep link: ${deepLinkResult.error}")
                    navigateNext()
                }
            }
        }
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

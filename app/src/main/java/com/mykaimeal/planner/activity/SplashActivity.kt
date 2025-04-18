package com.mykaimeal.planner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.AppsFlyerConstants
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

        val afDevKey: String = AppsFlyerConstants.afDevKey

        // Initialize AppsFlyer
        AppsFlyerLib.getInstance().init(afDevKey, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: Map<String, Any>) {
                Log.d("AppsFlyer", "Conversion Data: $data")
            }

            override fun onConversionDataFail(error: String) {
                Log.e("AppsFlyer", "Conversion Data Error: $error")
            }

            override fun onAppOpenAttribution(data: Map<String, String>) {
                Log.d("AppsFlyer", "App Open Attribution: $data")
            }

            override fun onAttributionFailure(error: String) {
                Log.e("AppsFlyer", "Attribution Error: $error")
            }
        }, this)

        // Enable debugging during development (disable in production)
        AppsFlyerLib.getInstance().setDebugLog(true)

        // Start AppsFlyer
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setAppInviteOneLink("mPqu")

        // Listen for deep links
        AppsFlyerLib.getInstance().subscribeForDeepLink { deepLinkResult ->
            when (deepLinkResult.status) {
                DeepLinkResult.Status.FOUND -> {
                    val deepLink = deepLinkResult.deepLink
                    Log.d("AppsFlyer", "Deep link: ${deepLink?.deepLinkValue}")
                }

                DeepLinkResult.Status.NOT_FOUND -> {
                    Log.d("AppsFlyer", "Deep link not found.")
                }

                DeepLinkResult.Status.ERROR -> {
                    Log.e("AppsFlyer", "Deep link error: ${deepLinkResult.error}")
                }
            }
        }

        navigateNext()



    }

    private fun redirectToPlayStore() {
        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.mykaimeal.planner")
        }
        startActivity(playStoreIntent)
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

package com.mykameal.planner.basedata

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MykaBaseApplication : Application() {

    companion object {
        @Volatile
        var instance: MykaBaseApplication? = null
        fun getAppContext(): Context {
            return instance?.applicationContext
                ?: throw IllegalStateException("Application instance is null")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(NetworkChangeReceiver(), filter)

        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()
    }


}
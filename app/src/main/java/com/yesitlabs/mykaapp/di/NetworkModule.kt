package com.yesitlabs.mykaapp.di

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.yesitlabs.mykaapp.activity.AuthActivity
import com.yesitlabs.mykaapp.apiInterface.ApiInterface
import com.yesitlabs.mykaapp.apiInterface.BaseUrl
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.MykaBaseApplication
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.repository.MainRepository
import com.yesitlabs.mykaapp.repository.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun mykapiInterFace(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): ApiInterface {
        return  retrofit
            .client(okHttpClient)
            .build()
            .create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun mykaRepository(api:ApiInterface): MainRepository {
        return MainRepositoryImpl(api)
    }


    @Singleton
    @Provides
    fun provideAuthAuthenticator(@ApplicationContext context: Context): AuthInterceptor {
        return AuthInterceptor(context)
    }

    @Singleton
    @Provides
    fun mykaOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain -> // Corrected lambda syntax
                val response = chain.proceed(chain.request())
                Log.d("@@@@@@@@", "response: ${response.code}") // Use Kotlin string interpolation
                // If the server responds with a 401 Unauthorized, handle logout
                if (response.code == 401) {
                    // Trigger logout or redirect to login activity
                    handleLogout(MykaBaseApplication.getAppContext())
                }
                response // Return the response
            }
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun customerRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BaseUrl.baseUrl)
        .addConverterFactory(GsonConverterFactory.create())


    private fun handleLogout(context: Context) {
        // Use application context to avoid lifecycle issues
        val appContext = context.applicationContext

        // Run on the main thread for UI-related tasks
        android.os.Handler(Looper.getMainLooper()).post {
            // Clear user session
            val sessionManagement = SessionManagement(appContext)
            sessionManagement.sessionClear()

            // Show a toast message
            Toast.makeText(appContext, "Your Session Expired", Toast.LENGTH_LONG).show()

            // Redirect to LoginActivity
            val intent = Intent(appContext, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            appContext.startActivity(intent)
        }
    }



}
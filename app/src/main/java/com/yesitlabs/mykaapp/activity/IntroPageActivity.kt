package com.yesitlabs.mykaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.ActivityIntroPageBinding
import com.yesitlabs.mykaapp.databinding.ActivitySplashBinding

class IntroPageActivity : AppCompatActivity() {

    private var binding:ActivityIntroPageBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityIntroPageBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        ///main function using all triggered of this screen
        initialize()
    }

    private fun initialize() {

        ///handle for click event
        binding!!.rlNextBtn.setOnClickListener{
            val intent = Intent(this@IntroPageActivity, OnBoardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
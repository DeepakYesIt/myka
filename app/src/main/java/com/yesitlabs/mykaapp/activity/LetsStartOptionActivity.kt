package com.yesitlabs.mykaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.ActivityCookingMyselfBinding
import com.yesitlabs.mykaapp.databinding.ActivityLetsStartOptionBinding

class LetsStartOptionActivity : AppCompatActivity() {

    private var binding: ActivityLetsStartOptionBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLetsStartOptionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        initialize()
    }

    private fun initialize() {
        binding!!.tvLogin.setOnClickListener{
            val intent = Intent(this@LetsStartOptionActivity, AuthActivity::class.java)
            intent.putExtra("type","login")
            startActivity(intent)
            finish()
        }

        binding!!.rlLetsCooking.setOnClickListener{
            val intent = Intent(this@LetsStartOptionActivity, AuthActivity::class.java)
            intent.putExtra("type","signup")
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressed()
    }
}
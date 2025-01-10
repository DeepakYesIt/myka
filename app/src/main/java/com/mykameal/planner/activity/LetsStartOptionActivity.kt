package com.mykameal.planner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mykameal.planner.databinding.ActivityLetsStartOptionBinding

class LetsStartOptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLetsStartOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetsStartOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        // Set an OnClickListener for the "Login" TextView
        binding.tvLogin.setOnClickListener {
            navigateToAuthActivity("login")
        }

        // Set an OnClickListener for the "Lets Start cooking" Button
        binding.rlLetsCooking.setOnClickListener {
            navigateToAuthActivity("signup")
        }
    }

    private fun navigateToAuthActivity(type: String) {
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("type", type)
        }
        startActivity(intent)
        finish()
    }

}

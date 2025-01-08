package com.yesitlabs.mykaapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.ActivityCookingMyselfBinding
import com.yesitlabs.mykaapp.databinding.ActivityLetsStartOptionBinding

class LetsStartOptionActivity : AppCompatActivity() {
    private var binding: ActivityLetsStartOptionBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetsStartOptionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        ///main function using all triggered of this screen
        initialize()
    }

    private fun initialize() {

        // Set an OnClickListener for the "Login" TextView.
        // When clicked, navigate to the AuthActivity with the "login" type passed as an extra in the intent.
        binding!!.tvLogin.setOnClickListener {
            val intent = Intent(this@LetsStartOptionActivity, AuthActivity::class.java)
            intent.putExtra("type", "login")
            startActivity(intent)
        }

        // Set an OnClickListener for the "Lets Start cooking" Button.
        // When clicked, navigate to the AuthActivity with the "Signup" type passed as an extra in the intent.
        // Finish the current activity to prevent returning to it.
        binding!!.rlLetsCooking.setOnClickListener {
            val intent = Intent(this@LetsStartOptionActivity, AuthActivity::class.java)
            intent.putExtra("type", "signup")
            startActivity(intent)
        }
    }

    ///handle on back pressed
    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressed()
    }
}
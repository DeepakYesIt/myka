package com.yesitlabs.mykaapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.yesitlabs.mykaapp.basedata.SessionManagement
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.ActivityCookingMyselfBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CookingForMyselfActivity : AppCompatActivity() {

    private var binding: ActivityCookingMyselfBinding?=null
    private lateinit var sessionManagement: SessionManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCookingMyselfBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)
        sessionManagement = SessionManagement(this@CookingForMyselfActivity)

        initialize()
    }

    private fun initialize() {

        startDestination()
    }

    private fun startDestination(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutAuth) as? NavHostFragment
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            val navGraph = navController.navInflater.inflate(R.navigation.main_graph)

            val cookingFor = sessionManagement.getCookingFor() ?: ""
            Toast.makeText(this, "sessionManagement :- $cookingFor", Toast.LENGTH_SHORT).show()
            val startDestination = when (cookingFor) {
                "Myself" -> R.id.bodyGoalsFragment
                "MyPartner" -> R.id.partnerInfoDetailsFragment
                else -> R.id.familyMembersFragment
            }
            navGraph.setStartDestination(startDestination)
            navController.graph = navGraph
        } else {
            Toast.makeText(this, "Navigation Host Fragment not found", Toast.LENGTH_SHORT).show()
        }
    }

}
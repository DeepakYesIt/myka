package com.yesitlabs.mykaapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.NavHostFragment
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private var binding:ActivityAuthBinding?=null
    private var type:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAuthBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        if (intent!=null){
            type=intent.getStringExtra("type").toString()
        }

        //handle destination for using this function
        startDestination()

    }

    //handle destinations for Login or Signup
    private fun startDestination(){
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.frameContainerAuth) as NavHostFragment
        val navController = navHostFragment.navController

        // Inflate the navigation graph
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)

        if (type == "signup"){
            navGraph.setStartDestination(R.id.signUpFragment)
        }else{
            navGraph.setStartDestination(R.id.loginFragment)
        }

        // Set the modified graph to the NavController
        navController.graph = navGraph

    }
}
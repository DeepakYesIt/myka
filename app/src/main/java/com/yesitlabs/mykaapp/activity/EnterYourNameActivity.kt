package com.yesitlabs.mykaapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.databinding.ActivityEnterYourNameBinding


class EnterYourNameActivity : AppCompatActivity() {
    private var binding:ActivityEnterYourNameBinding?=null
    private var status:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEnterYourNameBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        initialize()
    }

    private fun initialize() {

        binding!!.rlSelectNextBtn.setOnClickListener{
            val intent = Intent(this@EnterYourNameActivity, CookingForScreenActivity::class.java)
            startActivity(intent)
        }

        binding!!.rlSelectGender.setOnClickListener{
            if (status){
                status=false
                val drawableEnd = ContextCompat.getDrawable(this, R.drawable.drop_up_icon)
                val drawableStart = ContextCompat.getDrawable(this, R.drawable.gender_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
                binding!!.tvChooseGender.setCompoundDrawables(drawableStart, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility=View.VISIBLE
            }else{
                status=true
                val drawableEnd = ContextCompat.getDrawable(this, R.drawable.drop_down_icon)
                val drawableStart = ContextCompat.getDrawable(this, R.drawable.gender_icon)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
                binding!!.tvChooseGender.setCompoundDrawables(drawableStart, null, drawableEnd, null)
                binding!!.relSelectedGender.visibility=View.GONE
            }
        }

        binding!!.rlSelectMale.setOnClickListener{
            binding!!.tvChooseGender.text="Male"
            val drawableEnd = ContextCompat.getDrawable(this, R.drawable.drop_down_icon)
            val drawableStart = ContextCompat.getDrawable(this, R.drawable.gender_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(drawableStart, null, drawableEnd, null)
            binding!!.relSelectedGender.visibility=View.GONE
            status=true
        }

        binding!!.rlSelectFemale.setOnClickListener{
            binding!!.tvChooseGender.text="Female"
            val drawableEnd = ContextCompat.getDrawable(this, R.drawable.drop_down_icon)
            val drawableStart = ContextCompat.getDrawable(this, R.drawable.gender_icon)
            drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.tvChooseGender.setCompoundDrawables(drawableStart, null, drawableEnd, null)
            binding!!.relSelectedGender.visibility=View.GONE
            status=true
        }

    }
}
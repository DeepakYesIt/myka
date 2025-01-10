package com.mykameal.planner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.mykameal.planner.R
import com.mykameal.planner.adapter.OnboardingAdapter
import com.mykameal.planner.databinding.ActivityOnBoardingBinding
import com.mykameal.planner.model.OnboardingItem


class OnBoardingActivity : AppCompatActivity() {

    private var binding: ActivityOnBoardingBinding? = null
    var datalist : ArrayList<OnboardingItem> = arrayListOf()
    private var adapters:OnboardingAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        adapters = OnboardingAdapter(datalist)
        binding!!.viewpager.adapter = adapters
        binding!!.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        ///main function using all triggered of this screen
        initialize()

        /// set indicator for onboarding
        setUpOnBoardingIndicator()
        /// set current indicator position
        currentOnBoardingIndicator(0)

        /// handle click event to set viewpager position
        binding!!.rlNextBtn.setOnClickListener{
            if (binding!!.viewpager.currentItem == 0) {
                binding!!.viewpager.currentItem =+1
            } else {
                binding!!.viewpager.currentItem =adapters!!.itemCount+1
            }
        }

        // handle click event to set next screen
        binding!!.relLetsGetStarted.setOnClickListener{
            val intent = Intent(this@OnBoardingActivity, EnterYourNameActivity::class.java)
            startActivity(intent)
            finish()
        }


        /// set view pager position and value
        binding!!.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding!!.rlNextBtn.visibility= View.VISIBLE
                    binding!!.relLetsGetStarted.visibility= View.GONE
                    binding!!.tvHeading1.text="Plan a Meal"
                    binding!!.tvTextDescriptions.text=getString(R.string.tvDescriptions1)

                }else if (position==1){
                    binding!!.rlNextBtn.visibility= View.VISIBLE
                    binding!!.relLetsGetStarted.visibility= View.GONE
                    binding!!.tvHeading1.text="Track Food Expenses"
                    binding!!.tvTextDescriptions.text=getString(R.string.tvDescriptions2)
                }else{
                    binding!!.rlNextBtn.visibility= View.GONE
                    binding!!.relLetsGetStarted.visibility= View.VISIBLE
                    binding!!.tvHeading1.text="Smart Meal Shopping"
                    binding!!.tvTextDescriptions.text=getString(R.string.tvDescription3)
                }

                currentOnBoardingIndicator(position)
            }
        })
    }

    private fun setUpOnBoardingIndicator() {
        val indicator = arrayOfNulls<ImageView>(3)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 0)
        for (i in 0 until indicator.size) {
            val img = ImageView(this)
            img.setImageDrawable(this.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.indicator_inactive
                )
            })
            img.layoutParams = layoutParams
            binding!!.layOnboardingIndicator.addView(img)
        }
    }

    private fun currentOnBoardingIndicator(index: Int) {
        val childCount: Int = binding!!.layOnboardingIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = binding!!.layOnboardingIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_active))
            } else  {
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive))
            }
        }
    }

    private fun initialize() {

        // List of onboarding items
        datalist.add(OnboardingItem(R.drawable.onboarding_images_1))

        datalist.add(OnboardingItem(R.drawable.onboarding_images_2))

        datalist.add(OnboardingItem(R.drawable.onboarding_images_3))

    }
}

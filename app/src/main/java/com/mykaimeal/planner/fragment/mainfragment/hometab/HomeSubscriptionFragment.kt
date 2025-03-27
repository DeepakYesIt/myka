package com.mykaimeal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.OnboardingAdapter
import com.mykaimeal.planner.databinding.FragmentHomeSubscriptionBinding
import com.mykaimeal.planner.model.OnboardingItem


class HomeSubscriptionFragment : Fragment() {

    private var binding: FragmentHomeSubscriptionBinding? = null
    var datalist : ArrayList<OnboardingItem> = arrayListOf()
    private var adapters:OnboardingAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeSubscriptionBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        adapters = OnboardingAdapter(datalist)
        binding!!.viewpager.adapter = adapters
        binding!!.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        initialize()

        /// set indicator for onboarding
        setUpOnBoardingIndicator()
        /// set current indicator position
        currentOnBoardingIndicator(0)

        return binding!!.root
    }

    private fun setUpOnBoardingIndicator() {
        val indicator = arrayOfNulls<ImageView>(5)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 0)
        for (i in 0 until indicator.size) {
            val img = ImageView(requireContext())
            img.setImageDrawable(this.let {
                ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
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
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.subs_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.subs_indicator_inactive
                    )
                )
            }
        }
    }

    private fun initialize() {

        // List of onboarding items
        datalist.add(OnboardingItem(R.drawable.subscription_onboarding_1))

        datalist.add(OnboardingItem(R.drawable.subscription_onboarding_2))

        datalist.add(OnboardingItem(R.drawable.subscription_onboarding_3))

        datalist.add(OnboardingItem(R.drawable.subscription_onboarding_4))

        datalist.add(OnboardingItem(R.drawable.subscription_onboarding_5))

        viewPagerFunctionImpl()

        binding!!.imageCross.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.tvStartFreeTrial.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.tvSeeAllPlan.setOnClickListener {
            findNavController().navigate(R.id.homeSubscriptionAllPlanFragment)
        }
    }


    private fun viewPagerFunctionImpl() {

        /// set view pager position and value
        binding!!.viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding!!.tvHeadingTitle.text = "Save Time, Save Money Eat Better"
                    binding!!.tvDescriptions.text = "Kai plans your meals, Compares store prices, And creates your cart so you don’t have to."
                    binding!!.tvDescriptions2.text = ""
                    binding!!.tvDescriptions2.visibility=View.INVISIBLE
                } else if (position == 1) {
                    binding!!.tvHeadingTitle.text = "Endless Meals to Explore"
                    binding!!.tvDescriptions.text = "Kai gives you access to over 80,000 recipes"
                    binding!!.tvDescriptions2.text = ""
                    binding!!.tvDescriptions2.visibility=View.INVISIBLE
                } else if (position==2) {
                    binding!!.tvHeadingTitle.text = "Eat Smart, Every Day"
                    binding!!.tvDescriptions.text = "Kai helps you plan your week with recipes tailored to your preferences"
                    binding!!.tvDescriptions2.text = "Stay on top of your nutrition with Kai’s daily nutrition tracker"
                    binding!!.tvDescriptions2.visibility=View.VISIBLE
                }else if (position==3){
                    binding!!.tvHeadingTitle.text = "Maximum Savings, Zero Hassle"
                    binding!!.tvDescriptions.text = "One tap, and all your weekly ingredients are in your cart"
                    binding!!.tvDescriptions2.text = "Compare grocery prices at nearby stores & have them delivered  right to your door"
                    binding!!.tvDescriptions2.visibility=View.VISIBLE
                }else{
                    binding!!.tvHeadingTitle.text = "Show Me the Money!"
                    binding!!.tvDescriptions.text = "Users save an average of \$64 a week that’s an amazing \$256* a month!"
                    binding!!.tvDescriptions2.text = "With Kai, smart choices aren't just smart. They’re money in the bank"
                    binding!!.tvDescriptions2.visibility=View.VISIBLE
                }

                currentOnBoardingIndicator(position)
            }
        })
    }

}
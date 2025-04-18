package com.mykaimeal.planner.fragment.mainfragment.profilesetting.subscriptionplan

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterOnBoardingSubscriptionItem
import com.mykaimeal.planner.databinding.FragmentSubscriptionPlanOverViewBinding
import com.mykaimeal.planner.model.OnSubscriptionModel

class SubscriptionPlanOverViewFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionPlanOverViewBinding
    private lateinit var slideUp: Animation
    var datalist: ArrayList<OnSubscriptionModel> = arrayListOf()
    private var adapters: AdapterOnBoardingSubscriptionItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSubscriptionPlanOverViewBinding.inflate(layoutInflater, container, false)

        (activity as? MainActivity)?.binding?.apply {
            llIndicator.visibility = View.GONE
            llBottomNavigation.visibility = View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })



        binding.crossImages.visibility = View.INVISIBLE // Initially hide the ImageView

        Handler(Looper.getMainLooper()).postDelayed({
            binding.crossImages.visibility = View.VISIBLE // Show the ImageView after 5 seconds
        }, 5000)

        initialize()

        return binding.root
    }

    private fun setUpOnBoardingIndicator() {
        val indicator = arrayOfNulls<ImageView>(5)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicator.indices) {
            val img = ImageView(requireContext())
            img.setImageDrawable(this.let {
                ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive)
            })
            img.layoutParams = layoutParams
            binding.layOnboardingIndicator.addView(img)
        }
    }

    private fun currentOnBoardingIndicator(index: Int) {
        val childCount: Int = binding.layOnboardingIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layOnboardingIndicator.getChildAt(i) as ImageView
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

        binding.rlNextBtn.setOnClickListener {
            if (binding.viewpager.currentItem < adapters!!.itemCount - 1) {
                // Move to the next item
                binding.viewpager.currentItem += 1
            } else {
                findNavController().navigate(R.id.homeSubscriptionAllPlanFragment)
            }
        }

        binding.crossImages.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        datalist.clear()

        // List of onboarding items
        datalist.add(OnSubscriptionModel(R.drawable.image_1, true))

        datalist.add(OnSubscriptionModel(R.drawable.image_2, false))

        datalist.add(OnSubscriptionModel(R.drawable.image_3, false))

        datalist.add(OnSubscriptionModel(R.drawable.image_4, false))

        datalist.add(OnSubscriptionModel(R.drawable.image_5, false))

        adapters = AdapterOnBoardingSubscriptionItem(datalist)
        binding.viewpager.adapter = adapters
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        /// set indicator for onboarding
        setUpOnBoardingIndicator()
        /// set current indicator position
        currentOnBoardingIndicator(0)

        viewPagerFunctionImpl()

        binding.textSeeAllButton.setOnClickListener {
            binding.textSeeAllButton.visibility = View.GONE

            binding.rlBottom.visibility = View.VISIBLE

            slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_anim)
            slideUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding.llSubPlans.clearAnimation()
                    binding.llSubPlans.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding.llSubPlans.startAnimation(slideUp)
        }

    }

    private fun viewPagerFunctionImpl() {

        /// set view pager position and value
        binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.tvHeadingTitle.text = "Save Time, Save Money Eat Better"
                    binding.tvDescriptions.text =
                        "Kai plans your meals, Compares store prices, And creates your cart so you don’t have to."
                    binding.tvDescriptions2.text = ""
                    binding.relDescriptions2.visibility = View.INVISIBLE
                } else if (position == 1) {
                    binding.tvHeadingTitle.text = "Endless Meals to Explore"
                    binding.tvDescriptions.text = "Kai gives you access to over 80,000 recipes"
                    binding.tvDescriptions2.text = ""
                    binding.relDescriptions2.visibility = View.INVISIBLE
                } else if (position == 2) {
                    binding.relDescriptions2.visibility = View.VISIBLE
                    binding.tvHeadingTitle.text = "Eat Smart, Every Day"
                    binding.tvDescriptions.text =
                        "Kai helps you plan your week with recipes tailored to your preferences"
                    binding.tvDescriptions2.text =
                        "Stay on top of your nutrition with Kai’s daily nutrition tracker"

                } else if (position == 3) {
                    binding.relDescriptions2.visibility = View.VISIBLE
                    binding.tvHeadingTitle.text = "Maximum Savings, Zero Hassle"
                    binding.tvDescriptions.text =
                        "One tap, and all your weekly ingredients are in your cart"
                    binding.tvDescriptions2.text =
                        "Compare grocery prices at nearby stores & have them delivered  right to your door"
                } else {
                    binding.relDescriptions2.visibility = View.VISIBLE
                    binding.tvHeadingTitle.text = "Show Me the Money!"
                    binding.tvDescriptions.text =
                        "Users save an average of \$64 a week that’s an amazing \$256* a month!"
                    binding.tvDescriptions2.text =
                        "With Kai, smart choices aren't just smart. They’re money in the bank"
                }

                currentOnBoardingIndicator(position)
            }
        })

    }

}
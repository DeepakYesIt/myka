package com.mykaimeal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.SubscriptionAdaptor
import com.mykaimeal.planner.databinding.FragmentHomeSubscriptionAllPlanBinding
import com.mykaimeal.planner.model.SubscriptionsModel
import java.util.ArrayList
import kotlin.math.abs

class HomeSubscriptionAllPlanFragment : Fragment() {

    private var binding: FragmentHomeSubscriptionAllPlanBinding?=null
    var adapter: SubscriptionAdaptor? = null
    var datalist: ArrayList<SubscriptionsModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeSubscriptionAllPlanBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        binding!!.imageCrossing.setOnClickListener{
            findNavController().navigateUp()
        }

        datalist = getSubscription()

        adapter = SubscriptionAdaptor(datalist,requireActivity())
        binding!!.viewpager.adapter = adapter
        binding!!.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Configure ViewPager2 for carousel effect
        binding!!.viewpager.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3 // Preload adjacent pages
            val marginWidth =    (requireContext().resources.displayMetrics.widthPixels / 3) + 20
            setPadding(marginWidth, 0, marginWidth, 0) // Add padding for side visibility

            // Disable over-scroll effect
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        }


        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(20)) // Adjust spacing

        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)

            // Adjust top padding dynamically for the middle item
            if (position == 0f) {
                page.translationY = -20f // Raise the middle item slightly
            } else {
                page.translationY = 0f // Keep others at the default position
            }

            // Optionally, apply a scale effect
            page.scaleY = 0.85f + r * 0.15f // Zoom effect for the middle item
        }
        binding!!.viewpager.setPageTransformer(compositePageTransformer)

        binding!!.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0 || position == 1 || position == 2) {
                    if (position == 0){
                    } else  if (position == 1){

                    }
                    else  if (position == 2){
                    }
                }
            }
        })

        return binding!!.root
    }

    private fun getSubscription(): ArrayList<SubscriptionsModel> {
        val data: ArrayList<SubscriptionsModel> = ArrayList()
        data.add(SubscriptionsModel("Myka Basic", "\$1.99 / Weekly", "\$3.99 / Weekly"))
        data.add(SubscriptionsModel("Myka Monthly", "\$5.99 / monthly", "\$11.99 / monthly"))
        data.add(SubscriptionsModel("Annual", "\$55.00 / Yearly", "\$66.00 / Yearly"))
        return data
    }

}
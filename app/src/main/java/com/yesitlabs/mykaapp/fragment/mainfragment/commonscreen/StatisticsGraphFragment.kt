package com.yesitlabs.mykaapp.fragment.mainfragment.commonscreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.databinding.FragmentStatisticsGraphBinding

class StatisticsGraphFragment : Fragment() {

    private var binding:FragmentStatisticsGraphBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentStatisticsGraphBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imgBackStats.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.relInvite.setOnClickListener{
            findNavController().navigate(R.id.invitationsScreenFragment)
        }

        val entries = listOf(
            BarEntry(0f, 300f), // 1 June
            BarEntry(1f, 600f), // 7 June
            BarEntry(2f, 200f), // 14 June
            BarEntry(3f, 400f)  // 28 June
        )

        val dataSet = BarDataSet(entries, "")
        val barData = BarData(dataSet)
        binding!!.barChart.data = barData
        binding!!.barChart.invalidate()

// Customize labels
        val xAxis = binding!!.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("01 June", "07 June", "14 June", "28 June"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        binding!!.textInviteFriends.setOnClickListener {
            val appPackageName: String = requireActivity().packageName
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            val body =
                "https://play.google.com/store/apps/details?id=$appPackageName"
            val sub = "Your Subject"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sub)
            myIntent.putExtra(Intent.EXTRA_TEXT, body)
            startActivity(Intent.createChooser(myIntent, "Share Using"))
        }

        binding!!.barChart.setOnClickListener{
            findNavController().navigate(R.id.statisticsWeekYearFragment)
        }

    }

}
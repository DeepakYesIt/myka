package com.mykameal.planner.fragment.mainfragment.commonscreen


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.basedata.SessionManagement
import com.mykameal.planner.customview.SpendingChartView
import com.mykameal.planner.databinding.FragmentStatisticsGraphBinding

class StatisticsGraphFragment : Fragment() {

    private var binding:FragmentStatisticsGraphBinding?=null
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentStatisticsGraphBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.VISIBLE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.VISIBLE

        sessionManagement = SessionManagement(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

       /* val spendingData = listOf(
            SpendingChartView.BarData(300f, "01 June", Color.parseColor("#FFA500")),  // Orange
            SpendingChartView.BarData(600f, "07 June", Color.parseColor("#32CD32")),  // Green
            SpendingChartView.BarData(200f, "14 June", Color.parseColor("#FF0000")),  // Red
            SpendingChartView.BarData(400f, "21 June", Color.parseColor("#FFA500"))   // Orange
        )
        binding!!.spendingChart.setData(spendingData)*/

        // Create chart data
        val data = listOf(
            SpendingChartView.BarData(300f, "01","Jun", Color.parseColor("#FFA500")),
            SpendingChartView.BarData(600f, "02","Jun", Color.parseColor("#32CD32")),
            SpendingChartView.BarData(200f, "03", "Jun",Color.parseColor("#FF4040")),
           /* SpendingChartView.BarData(400f, "04","Jun", Color.parseColor("#FFA500")),
            SpendingChartView.BarData(600f, "05","Jun", Color.parseColor("#32CD32")),
            SpendingChartView.BarData(400f, "06","Jun", Color.parseColor("#32CD32")),
            SpendingChartView.BarData(300f, "07","Jun", Color.parseColor("#32CD32")),*/
            )

        binding!!.spendingChart.setData(data, 1566f, 60f)

       /* val values = listOf(300.0, 600.0, 200.0, 400.0)
        val colors = listOf(
            Color.parseColor("#FFA500"),  // Orange
            Color.parseColor("#00FF00"),  // Green
            Color.parseColor("#FF0000"),  // Red
            Color.parseColor("#FFA500")   // Orange
        )

        binding!!.spendingChart.setData(values, colors)*/

        initialize()

        return binding!!.root
    }

    private fun initialize() {
      /*  Log.d("AppsFlyer22", "Deep link found, store")

        AppsFlyerLib.getInstance().subscribeForDeepLink { deepLinkResult ->
            when (deepLinkResult.status) {
                DeepLinkResult.Status.FOUND -> {
                    Log.d("AppsFlyer22", "Deep link found, store")

                    val deepLinkValue = deepLinkResult.deepLink?.getStringValue("deep_link_value")
                    if (deepLinkValue == "profile_screen") {
                        // Navigate to Profile Screen
                        Log.d("AppsFlyer22", "profile")

                    } else {
                        Log.d("AppsFlyer22", "Deep link statistics")

                        // Handle other deep link values
                    }
                }
                DeepLinkResult.Status.NOT_FOUND -> {
                    Log.d("AppsFlyer22", "Deep link not found, redirecting to Play Store")
                    redirectToPlayStore()
                }
                DeepLinkResult.Status.ERROR -> {
                    Log.d("AppsFlyer22", "Error in deep link: ${deepLinkResult.error}")
                }
            }
        }*/

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
            /*shareApp(requireActivity())*/

            copyShareInviteLink()
           /* val appPackageName: String = requireActivity().packageName
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            val body =
                "https://play.google.com/store/apps/details?id=$appPackageName"
            val sub = "Your Subject"
            myIntent.putExtra(Intent.EXTRA_SUBJECT, sub)
            myIntent.putExtra(Intent.EXTRA_TEXT, body)
            startActivity(Intent.createChooser(myIntent, "Share Using"))*/
        }

        binding!!.barChart.setOnClickListener{
            findNavController().navigate(R.id.statisticsWeekYearFragment)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun copyShareInviteLink() {
        val currentCampaign = "user_invite"
        val currentChannel = "mobile_share"
        val currentReferrerId = sessionManagement.getId().toString()
        val linkGenerator = ShareInviteHelper.generateInviteUrl(requireActivity())
        /*linkGenerator.addParameter("deep_link_value", this.fruitName)
        linkGenerator.addParameter("deep_link_sub1", this.fruitAmountStr)*/
        linkGenerator.addParameter("deep_link_sub2", currentReferrerId)
        linkGenerator.campaign = currentCampaign
        linkGenerator.channel = currentChannel
        Log.d(LOG_TAG, "Link params:" + linkGenerator.userParams.toString())
        val listener: LinkGenerator.ResponseListener = object : LinkGenerator.ResponseListener {
            override fun onResponse(s: String) {
                Log.d(LOG_TAG, "Share invite link: $s")
                //Copy the share invite link to clipboard and indicate it with a toast
                requireActivity().runOnUiThread(Runnable {
                    val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Share invite link", s)
                    clipboard.setPrimaryClip(clip)
                    val toast = Toast.makeText(requireActivity(), "Link copied to clipboard", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 20)
                    toast.show()
                })
                val logInviteMap = HashMap<String, String>()
                logInviteMap["referrerId"] = currentReferrerId
                logInviteMap["campaign"] = currentCampaign
                ShareInviteHelper.logInvite(requireActivity(), currentChannel, logInviteMap)
            }

            override fun onResponseError(s: String) {
                Log.d(LOG_TAG, "onResponseError called")
            }
        }
        linkGenerator.generateLink(requireActivity(), listener)
    }


}
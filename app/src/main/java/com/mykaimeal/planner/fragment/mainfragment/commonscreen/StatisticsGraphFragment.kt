package com.mykaimeal.planner.fragment.mainfragment.commonscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.customview.SpendingChartView
import com.mykaimeal.planner.databinding.FragmentStatisticsGraphBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsGraphFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsGraphBinding
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentStatisticsGraphBinding.inflate(layoutInflater, container, false)
        

        (activity as? MainActivity)?.binding?.apply {
            llIndicator.visibility = View.VISIBLE
            llBottomNavigation.visibility = View.VISIBLE
        }


        sessionManagement = SessionManagement(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
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
        binding.spendingChart.setData(spendingData)*/

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

        binding.spendingChart.setData(data, 1566f, 60f)
        

        initialize()

        return binding.root
    }

    private fun initialize() {
        Log.d("AppsFlyer22", "Deep link found, store")

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
        }

        binding.imgBackStats.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.relInvite.setOnClickListener{
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
        binding.barChart.data = barData
        binding.barChart.invalidate()

        // Customize labels
        val xAxis = binding.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("01 June", "07 June", "14 June", "28 June"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        binding.textInviteFriends.setOnClickListener {
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

        binding.barChart.setOnClickListener{
            findNavController().navigate(R.id.statisticsWeekYearFragment)
        }
    }

    private fun redirectToPlayStore() {
        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.mykaimeal.planner")
        }
        startActivity(playStoreIntent)
    }

    @SuppressLint("RestrictedApi")
    private fun copyShareInviteLink() {

        val currentCampaign = "user_invite"
        val currentChannel = "mobile_share"
        val currentReferrerId = sessionManagement.getId().toString()
        val providerName = sessionManagement.getUserName().toString()
        val providerImage = sessionManagement.getImage().toString()
        val userReferralCode = sessionManagement.getReferralCode().toString()

        // Base OneLink URL (Replace this with your actual OneLink domain)
        val baseOneLink = "https://mykaimealplanner.onelink.me/mPqu/"

        // Manually append query parameters to ensure they appear in the final link
        val deepLinkUrl = Uri.parse(baseOneLink).buildUpon()
            .appendQueryParameter("af_user_id", currentReferrerId)
            .appendQueryParameter("Referrer", userReferralCode)
            .appendQueryParameter("providerName", providerName)
            .appendQueryParameter("providerImage", providerImage)
            .build()
            .toString()

        Log.d("AppsFlyer", "Generated Deep Link: $deepLinkUrl")

        // Prepare share message
        val message = "Hi, I am inviting you to download My-Kai app!\n\nClick on the link below:\n$deepLinkUrl"

        // Open share dialog
        requireActivity().runOnUiThread {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            requireActivity().startActivity(Intent.createChooser(shareIntent, "Share invite link via"))

            // Log invite event to AppsFlyer
            val logInviteMap = hashMapOf("referrerId" to currentReferrerId, "campaign" to currentCampaign)
            ShareInviteHelper.logInvite(requireActivity(), currentChannel, logInviteMap)
        }
    }

/*    @SuppressLint("RestrictedApi")
    private fun copyShareInviteLink() {
        val currentCampaign = "user_invite"
        val currentChannel = "mobile_share"
        val currentReferrerId = sessionManagement.getId().toString()
        val userReferralCode = sessionManagement.getReferralCode().toString()

        // Ensure the generated URL has the right structure
        val linkGenerator = ShareInviteHelper.generateInviteUrl(requireActivity())
        linkGenerator.addParameter("deep_link_sub2", currentReferrerId)
        linkGenerator.addParameter("deep_link_sub3", userReferralCode)
        linkGenerator.campaign = currentCampaign
        linkGenerator.channel = currentChannel

        Log.d(LOG_TAG, "Link params: ${linkGenerator.userParams}")

        val listener: LinkGenerator.ResponseListener = object : LinkGenerator.ResponseListener {
            override fun onResponse(s: String) {
                Log.d(LOG_TAG, "Hi, I am inviting you to download My-kai app!\nclick on the link below:$s")

                val message = "Hi, I am inviting you to download My-kai app!\nclick on the link below:\n$s"

                // Ensure the URL has the correct deep link format
                requireActivity().runOnUiThread {
                    // Create an intent for sharing the deep link
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message) // Add the invite link
                    }

                    // Show the chooser dialog
                    val chooser = Intent.createChooser(shareIntent, "Share invite link via")
                    requireActivity().startActivity(chooser)

                    // Log the invite data
                    val logInviteMap = HashMap<String, String>()
                    logInviteMap["referrerId"] = currentReferrerId
                    logInviteMap["campaign"] = currentCampaign
                    ShareInviteHelper.logInvite(requireActivity(), currentChannel, logInviteMap)
                }
            }

            override fun onResponseError(s: String) {
                Log.d(LOG_TAG, "onResponseError called")
            }
        }

        // Generate the deep link URL
        linkGenerator.generateLink(requireActivity(), listener)
    }*/

}
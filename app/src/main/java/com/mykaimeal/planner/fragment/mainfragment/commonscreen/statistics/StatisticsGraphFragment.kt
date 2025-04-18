package com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
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
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class StatisticsGraphFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsGraphBinding
    private lateinit var sessionManagement: SessionManagement
    private lateinit var statisticsViewModel: StatisticsViewModel
    private var referLink: String =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStatisticsGraphBinding.inflate(layoutInflater, container, false)

        (activity as? MainActivity)?.binding?.apply {
            llIndicator.visibility = View.VISIBLE
            llBottomNavigation.visibility = View.VISIBLE
        }

        statisticsViewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        sessionManagement = SessionManagement(requireActivity())

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })


        // Create chart data
        val data = listOf(
            SpendingChartView.BarData(300f, "01", "Jun", Color.parseColor("#FFA500")),
            SpendingChartView.BarData(600f, "02", "Jun", Color.parseColor("#32CD32")),
            SpendingChartView.BarData(200f, "03", "Jun", Color.parseColor("#FF4040")),)

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

        binding.relInvite.setOnClickListener {
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
        xAxis.valueFormatter =
            IndexAxisValueFormatter(listOf("01 June", "07 June", "14 June", "28 June"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        binding.textInviteFriends.setOnClickListener {

            shareImageWithText("Hey! I put together this cookbook in My" +
                    "Kai, and I think you’ll love it! It’s packed with delicious meals, check it out and let me know what you think!" +
                    "\nclick on the link below:\n\n",
                referLink)

//            dynamicValueUpdate()
            /* copyShareInviteLink()*/
        }

        binding.barChart.setOnClickListener {
            findNavController().navigate(R.id.statisticsWeekYearFragment)
        }


        copyShareInviteLink()
    }

    private fun redirectToPlayStore() {
        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.mykaimeal.planner")
        }
        startActivity(playStoreIntent)
    }

    private fun shareImageWithText(description: String,  link: String) {
        // Download image using Glide
        Glide.with(requireContext())
            .asBitmap() // Request a Bitmap image
            .load(R.mipmap.app_icon) // Provide the URL to load the image from
            .into(object : CustomTarget<Bitmap>() { override fun onResourceReady(resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    try {
                        // Save the image to a file in the app's external storage
                        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shared_image.png"
                        )
                        val fos = FileOutputStream(file)
                        resource.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        fos.close()

                        // Create URI for the file using FileProvider
                        val uri: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            requireActivity().packageName + ".provider", // Make sure this matches your manifest provider
                            file
                        )

                        // Format the message with line breaks
                        val formattedText = """$description$link""".trimIndent()

                        // Create an intent to share the image and text
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_TEXT, formattedText)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        // Launch the share dialog
                        requireContext().startActivity(Intent.createChooser(shareIntent, "Share Image"))

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("ImageShareError", "onResourceReady: ${e.message}")
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Optional: Handle if the image load is cleared or cancelled
                }
            })
    }

    @SuppressLint("RestrictedApi")
    private fun copyShareInviteLink() {

        val afUserId = sessionManagement.getId()?.toString().orEmpty()
        val referrerCode = sessionManagement.getReferralCode()?.toString().orEmpty()
        val providerName = sessionManagement.getUserName()?.toString().orEmpty()
        val providerImage = sessionManagement.getImage()?.toString().orEmpty()

        val baseUrl = "https://mykaimealplanner.onelink.me/mPqu/"

        val fullUrl = Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter("af_user_id", afUserId)
            .appendQueryParameter("Referrer", referrerCode)
            .appendQueryParameter("providerName", providerName)
            .appendQueryParameter("providerImage", providerImage)
            .build()
            .toString()

        referLink = fullUrl
        Log.d("AF_TEST", "Custom Raw Link: $referLink")


        /*    val currentCampaign = "user_invite"
            val currentChannel = "mobile_share"

            val afUserId = sessionManagement.getId()?.toString().orEmpty()
            val referrerCode = sessionManagement.getReferralCode()?.toString().orEmpty()
            val providerName = sessionManagement.getUserName()?.toString().orEmpty()
            val providerImage = sessionManagement.getImage()?.toString().orEmpty()

            val linkGenerator = ShareInviteHelper.generateInviteUrl(requireActivity())
                .setBaseDeeplink("https://mykaimealplanner.onelink.me/mPqu")
                .setCampaign(currentCampaign)
                .setChannel(currentChannel)
                .addParameter("af_user_id", afUserId)
                .addParameter("referrer", referrerCode)
                .addParameter("providerName", providerName)
                .addParameter("providerImage", providerImage)

            Log.d("AF_TEST", "Params: ${linkGenerator.userParams}")

            linkGenerator.generateLink(requireActivity(), object : LinkGenerator.ResponseListener {
                override fun onResponse(s: String) {
                    referLink = s
                    Log.d("AF_TEST", "Generated Link: $s")
                }

                override fun onResponseError(s: String) {
                    Log.e("AF_TEST", "Error Generating Link: $s")
                }
            })*/

    }
}
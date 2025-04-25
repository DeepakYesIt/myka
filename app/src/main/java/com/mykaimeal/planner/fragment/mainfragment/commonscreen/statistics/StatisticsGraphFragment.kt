package com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics

import android.annotation.SuppressLint
import android.app.Dialog
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
import android.widget.CalendarView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.commonworkutils.RoundedBarChartRenderer
import com.mykaimeal.planner.customview.SpendingChartView
import com.mykaimeal.planner.databinding.FragmentStatisticsGraphBinding
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics.model.StatisticsGraphModel
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics.model.StatisticsGraphModelData
import com.mykaimeal.planner.fragment.mainfragment.commonscreen.statistics.viewmodel.StatisticsViewModel
import com.mykaimeal.planner.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class StatisticsGraphFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsGraphBinding
    private lateinit var sessionManagement: SessionManagement
    private lateinit var statisticsViewModel: StatisticsViewModel
    private var referLink: String = ""
    private var lastSelectedDate: Long? = null
    private var currentMonth: String = ""

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

        initialize()

        return binding.root
    }

    private fun initialize() {

        // Set currentMonth from today's date
        val calendar = Calendar.getInstance()
        currentMonth = (calendar.get(Calendar.MONTH) + 1).toString()


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



        binding.textInviteFriends.setOnClickListener {

            shareImageWithText(
                "Hey! I put together this cookbook in My" +
                        "Kai, and I think you’ll love it! It’s packed with delicious meals, check it out and let me know what you think!" +
                        "\nclick on the link below:\n\n",
                referLink
            )

//            dynamicValueUpdate()
            /* copyShareInviteLink()*/
        }

        binding.barChart.setOnClickListener {
            findNavController().navigate(R.id.statisticsWeekYearFragment)
        }

        copyShareInviteLink()

        if (BaseApplication.isOnline(requireContext())) {
            getGraphList()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }

        binding.relMonthYear.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val calendarView = dialog.findViewById<CalendarView>(R.id.calendar)

        dialog.setOnShowListener {
            calendarView?.date = lastSelectedDate ?: Calendar.getInstance().timeInMillis
        }
        // Get today's date
        val today = Calendar.getInstance()
        // Set the minimum date to today
        calendarView?.minDate = today.timeInMillis

        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            lastSelectedDate = calendar.timeInMillis

            // Format for tvMonthYear: "June 2024"
            val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            binding.tvDateCalendar.text = monthYearFormat.format(calendar.time)

            // Update currentMonth (month is 0-based, so add +1)
            currentMonth = (month + 1).toString()

            if (BaseApplication.isOnline(requireContext())) {
                getGraphList()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }

            dialog.dismiss()
        }

        dialog.show()
    }


    private fun getGraphList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            statisticsViewModel.getGraphScreenUrl({
                BaseApplication.dismissMe()
                handleApiGraphResponse(it)
            }, currentMonth)
        }
    }

    private fun handleApiGraphResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessGraphResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessGraphResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, StatisticsGraphModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null) {
                    showSpendingChart(apiModel.data)
                }
            } else {
                if (apiModel.code == ErrorMessage.code) {
                    showAlert(apiModel.message, true)
                } else {
                    showAlert(apiModel.message, false)
                }
            }
        } catch (e: Exception) {
            showAlert(e.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSpendingChart(response: StatisticsGraphModelData) {

        val month = response.month ?: ""
        val graphData = response.graph_data

        val weekValues = mutableListOf<Float>()
        weekValues.add((graphData.week_1 ?: 0).toFloat())
        weekValues.add((graphData.week_2 ?: 0).toFloat())
        weekValues.add((graphData.week_3 ?: 0).toFloat())
        weekValues.add((graphData.week_4 ?: 0).toFloat())

        val entries = mutableListOf<BarEntry>()
        for (i in weekValues.indices) {
            entries.add(BarEntry(i.toFloat(), weekValues[i]))
        }

        val weekLabels = mutableListOf("01 $month", "08 $month", "15 $month", "22 $month")

        val barDataSet = BarDataSet(entries, "Spending ($)").apply {
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            setDrawValues(true)
            colors = listOf(Color.RED, Color.LTGRAY, Color.GREEN, Color.MAGENTA)
        }

        // BarData setup
        val barData = BarData(barDataSet).apply {
            barWidth = 1f
        }

        // Chart setup
        with(binding.barChart) {
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler, 30f)
            data = barData
            setFitBars(false)
            animateY(800)

            axisRight.isEnabled = false
            axisLeft.textColor = Color.BLACK

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = weekValues.size
                textColor = Color.BLACK
                textSize = 12f
                axisMinimum = -0.5f
                axisMaximum = weekValues.size - 0.5f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return weekLabels.getOrNull(value.toInt()) ?: ""
                    }
                }
            }

            invalidate()
        }

        if (response.total_spent != null) {
            binding.textSpent.text = "Total spent $" + response.total_spent.toString().trim()
        }

        if (response.saving != null) {
            binding.textSpent.text = "Your savings are $" + response.saving.toString().trim()
        }
    }


    private fun redirectToPlayStore() {
        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.mykaimeal.planner")
        }
        startActivity(playStoreIntent)
    }

    private fun shareImageWithText(description: String, link: String) {
        // Download image using Glide
        Glide.with(requireContext())
            .asBitmap() // Request a Bitmap image
            .load(R.mipmap.app_icon) // Provide the URL to load the image from
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    try {
                        // Save the image to a file in the app's external storage
                        val file = File(
                            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "shared_image.png"
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
                        requireContext().startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Share Image"
                            )
                        )

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
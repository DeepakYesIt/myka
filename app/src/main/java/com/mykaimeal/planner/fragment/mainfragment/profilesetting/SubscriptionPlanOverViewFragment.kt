package com.mykaimeal.planner.fragment.mainfragment.profilesetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.databinding.FragmentSubscriptionPlanOverViewBinding
import java.util.Arrays
import java.util.concurrent.Executors
import java.util.stream.Collectors

class SubscriptionPlanOverViewFragment : Fragment() {
    private var binding: FragmentSubscriptionPlanOverViewBinding?=null
    private lateinit var slideUp: Animation
    private var billingClient: BillingClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSubscriptionPlanOverViewBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        initialize()

        return binding!!.root
    }

    private fun initialize() {

        binding!!.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }


        startBillingApi()

        binding!!.textSeeAllButton.setOnClickListener {
            binding!!.textSeeAllButton.visibility = View.GONE

            binding!!.rlBottom.visibility = View.VISIBLE

            slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_anim)
            slideUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding!!.llSubPlans.clearAnimation()
                    binding!!.llSubPlans.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding!!.llSubPlans.startAnimation(slideUp)
        }
    }

    private fun startBillingApi() {
        BaseApplication.showMe(requireActivity())
        billingClient = BillingClient.newBuilder(requireActivity())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

//        getPrices()
    }


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult: BillingResult, purchases: List<Purchase>? ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        Log.d("Testing", "Hello")
                        val orderId = purchase.orderId
                        val purchaseToken1 = purchase.purchaseToken
                        Log.d("TESTING_SPARK", "$orderId orderId")
                        Log.d("TESTING_Spark", "$purchaseToken1 purchase token1")
                        //  add_subscription(productId, purchaseToken1);
//                    handlePurchase(productId,purchase);
                        handlePurchase(purchase)
                    }
                }
            } else {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Toast.makeText(
                        requireActivity(),
                        "Already Subscribed",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> Toast.makeText(
                        requireActivity(),
                        "FEATURE_NOT_SUPPORTED",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> Toast.makeText(
                        requireActivity(),
                        "BILLING_UNAVAILABLE",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.USER_CANCELED -> Toast.makeText(
                        requireActivity(),
                        "USER_CANCELLED",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Toast.makeText(
                        requireActivity(),
                        "DEVELOPER_ERROR",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> Toast.makeText(
                        requireActivity(),
                        "ITEM_UNAVAILABLE",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.NETWORK_ERROR -> Toast.makeText(
                        requireActivity(),
                        "NETWORK_ERROR",
                        Toast.LENGTH_LONG
                    ).show()

                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> Toast.makeText(
                        requireActivity(),
                        "SERVICE_DISCONNECTED",
                        Toast.LENGTH_LONG
                    ).show()

                    else -> Toast.makeText(
                        requireActivity(),
                        "Error " + billingResult.debugMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val listener =
            ConsumeResponseListener { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                }
            }
        billingClient!!.consumeAsync(consumeParams, listener)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient!!.acknowledgePurchase(
                    acknowledgePurchaseParams
                ) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                 /*       editor.putString("subscription_id", purchase.orderId.toString())
                        editor.putString(
                            "subscription_PurchaseToken",
                            purchase.purchaseToken
                        )
                        editor.putString("startDate", BaseApplication.startDate())
                        editor.putString("subscription_status", planType)
                        editor.commit()
                        subscription_status = planType
                        Log.d("****", "subscription_id" + purchase.orderId.toString())
                        Log.d(
                            "**** ",
                            "subscription_PurchaseToken" + purchase.purchaseToken
                        )
                        Log.d("****", "startDate" + BaseApplication.startDate())
                        requireActivity().runOnUiThread(Runnable {
                            callingPurchaseSubscriptionApi(purchase.orderId, purchase.purchaseToken)
                        })*/
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Already Subscribed", Toast.LENGTH_LONG).show()
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {

            Toast.makeText(requireActivity(), "Subscription Pending", Toast.LENGTH_LONG).show()
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {

            Toast.makeText(requireActivity(), "UNSPECIFIED_STATE", Toast.LENGTH_LONG).show()
        }
    }


    private fun getPrices() {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val executorService = Executors.newSingleThreadExecutor()
                    executorService.execute {
                        val ids =
                            Arrays.asList<String>(
                                /*AppConstants.Premium_Monthly,
                                AppConstants.Premium_Annual,
                                AppConstants.Gym_Annual,
                                AppConstants.Family_two_user,
                                AppConstants.Family_four_user*/
                            ) // your product IDs
                        val productList =
                            ids.stream().map { productId: String? ->
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(productId!!)
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            }
                                .collect(
                                    Collectors.toList()
                                )
                        val queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(productList).build()
                        billingClient!!.queryProductDetailsAsync(
                            queryProductDetailsParams
                        ) { billingResult1: BillingResult?, productDetailsList: List<ProductDetails> ->
                            for (productDetails in productDetailsList) {
                                Log.d("******", productDetails.productId)
                                assert(productDetails.subscriptionOfferDetails != null)
                                for (subscriptionOfferDetails in productDetails.subscriptionOfferDetails!!) {
                                    when (productDetails.productId) {
                                    /*    AppConstants.Premium_Monthly -> premiumMonthly =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ month"

                                        AppConstants.Premium_Annual -> premiumAnnual =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ year"

                                        AppConstants.Gym_Annual -> gymAnnual =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ month"

                                        AppConstants.Family_two_user -> familytwouser =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ user"

                                        AppConstants.Family_four_user -> familyfouruser =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ user"*/
                                    }
                                }
                            }
                        }
                    }
                    requireActivity().runOnUiThread(Runnable {
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
//                        setPlanList()
                        BaseApplication.dismissMe()
                    })
                }
            }

            override fun onBillingServiceDisconnected() {
                billingClient!!.startConnection(this)
            }
        })
    }

}
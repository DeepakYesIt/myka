package com.mykaimeal.planner.fragment.mainfragment.profilesetting.subscriptionplan

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.bumptech.glide.Glide
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.SubscriptionAdaptor
import com.mykaimeal.planner.apiInterface.BaseUrl
import com.mykaimeal.planner.basedata.AppConstant
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.SessionManagement
import com.mykaimeal.planner.databinding.FragmentHomeSubscriptionAllPlanBinding
import com.mykaimeal.planner.model.SubscriptionModel
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.stream.Collectors

class SubscriptionAllPlanFragment : Fragment() {

    private var binding: FragmentHomeSubscriptionAllPlanBinding?=null
    var adapter: SubscriptionAdaptor? = null
    var datalist: ArrayList<SubscriptionModel> = arrayListOf()
    private var billingClient: BillingClient? = null
    private var premiumMonthly = ""
    private var premiumAnnual = ""
    private var premiumWeekly = ""
    private val rootPlanList: MutableList<SubscriptionModel> = mutableListOf()
    private lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeSubscriptionAllPlanBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding.llBottomNavigation.visibility=View.GONE

        sessionManagement = SessionManagement(requireContext())

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        initialize()

        startBillingApi()

        /*binding!!.imageCrossing.setOnClickListener{
            findNavController().navigateUp()
        }*/

        return binding!!.root
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initialize() {

        if (sessionManagement.getImage() != null) {
            Glide.with(requireContext())
                .load(BaseUrl.imageBaseUrl + sessionManagement.getImage())
                .placeholder(R.drawable.mask_group_icon)
                .error(R.drawable.mask_group_icon)
                .into(binding!!.imageProfile)
        }

        if (sessionManagement.getUserName() != null) {
            binding!!.tvTextNames.text = "You’ve got a gift from\n"+sessionManagement.getUserName()
            binding!!.tvSecretCookBook.text = sessionManagement.getUserName()+"secret cookbook"
        }

        binding!!.crossImages.setOnClickListener{
            findNavController().navigateUp()
        }

        binding!!.relSubscriptionBasic.setOnClickListener{
            binding!!.relSubscriptionBasic.setBackgroundResource(R.drawable.subscription_click_bg)
            binding!!.relPopularPlan.setBackgroundResource(R.drawable.subscription_unclick_bg)
            binding!!.relBestPlan.setBackgroundResource(R.drawable.subscription_unclick_bg)

            binding!!.imgBasicClick.setImageResource(R.drawable.selected_plan_icon)
            binding!!.imgPopularClick.setImageResource(R.drawable.unselelected_plan_icon)
            binding!!.imgBaseClick.setImageResource(R.drawable.unselelected_plan_icon)

         /*   binding!!.tvStarter.setImageResource(R.drawable.selected_plan_icon)
            binding!!.tvPopular.setImageResource(R.drawable.unselelected_plan_icon)
            binding!!.imgBaseClick.setImageResource(R.drawable.unselelected_plan_icon)*/

            binding!!.tvNewKai.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.tvProkaiUser.setTextColor(Color.parseColor("#000000"))
            binding!!.tvLovekaiUser.setTextColor(Color.parseColor("#000000"))

            binding!!.tvNewDollar.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.tvNewDollarMonthly.setTextColor(Color.parseColor("#000000"))
            binding!!.tvNewDollaryearly.setTextColor(Color.parseColor("#000000"))
        }

        binding!!.relPopularPlan.setOnClickListener{
            binding!!.relSubscriptionBasic.setBackgroundResource(R.drawable.subscription_unclick_bg)
            binding!!.relPopularPlan.setBackgroundResource(R.drawable.subscription_click_bg)
            binding!!.relBestPlan.setBackgroundResource(R.drawable.subscription_unclick_bg)
            binding!!.imgBasicClick.setImageResource(R.drawable.unselelected_plan_icon)
            binding!!.imgPopularClick.setImageResource(R.drawable.selected_plan_icon)
            binding!!.imgBaseClick.setImageResource(R.drawable.unselelected_plan_icon)

            binding!!.tvNewKai.setTextColor(Color.parseColor("#000000"))
            binding!!.tvProkaiUser.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.tvLovekaiUser.setTextColor(Color.parseColor("#000000"))


            binding!!.tvNewDollar.setTextColor(Color.parseColor("#000000"))
            binding!!.tvNewDollarMonthly.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.tvNewDollaryearly.setTextColor(Color.parseColor("#000000"))
        }

        binding!!.relBestPlan.setOnClickListener{
            binding!!.relSubscriptionBasic.setBackgroundResource(R.drawable.subscription_unclick_bg)
            binding!!.relPopularPlan.setBackgroundResource(R.drawable.subscription_unclick_bg)
            binding!!.relBestPlan.setBackgroundResource(R.drawable.subscription_click_bg)
            binding!!.imgBasicClick.setImageResource(R.drawable.unselelected_plan_icon)
            binding!!.imgPopularClick.setImageResource(R.drawable.unselelected_plan_icon)
            binding!!.imgBaseClick.setImageResource(R.drawable.selected_plan_icon)

            binding!!.tvNewKai.setTextColor(Color.parseColor("#000000"))
            binding!!.tvProkaiUser.setTextColor(Color.parseColor("#000000"))
            binding!!.tvLovekaiUser.setTextColor(Color.parseColor("#FFFFFF"))

            binding!!.tvNewDollar.setTextColor(Color.parseColor("#000000"))
            binding!!.tvNewDollarMonthly.setTextColor(Color.parseColor("#000000"))
            binding!!.tvNewDollaryearly.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun startBillingApi() {
        BaseApplication.showMe(requireActivity())
        billingClient = BillingClient.newBuilder(requireActivity())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        getPrices()
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
                        requireActivity(), "Already Subscribed", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> Toast.makeText(
                        requireActivity(), "FEATURE_NOT_SUPPORTED", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> Toast.makeText(
                        requireActivity(), "BILLING_UNAVAILABLE", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.USER_CANCELED -> Toast.makeText(
                        requireActivity(), "USER_CANCELLED", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Toast.makeText(
                        requireActivity(), "DEVELOPER_ERROR", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> Toast.makeText(
                        requireActivity(), "ITEM_UNAVAILABLE", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.NETWORK_ERROR -> Toast.makeText(
                        requireActivity(), "NETWORK_ERROR", Toast.LENGTH_LONG).show()

                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> Toast.makeText(
                        requireActivity(), "SERVICE_DISCONNECTED", Toast.LENGTH_LONG).show()

                    else -> Toast.makeText(
                        requireActivity(), "Error " + billingResult.debugMessage, Toast.LENGTH_LONG).show()
                }
            }
        }


    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val listener =
            ConsumeResponseListener {  billingResult, purchaseToken ->
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
                    /* if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                         editor.putString("subscription_id", purchase.orderId.toString())
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
                         })
                     }*/
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
                            mutableListOf<String>(
                                AppConstant.Premium_Monthly,
                                AppConstant.Premium_Annual,
                                AppConstant.Premium_Weekly
                            ) // your product IDs
                        val productList =
                            ids.stream().map { productId: String? ->
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(productId!!)
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            }.collect(Collectors.toList())
                        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
                        billingClient!!.queryProductDetailsAsync(
                            queryProductDetailsParams
                        ) { billingResult1: BillingResult?, productDetailsList: List<ProductDetails> ->
                            for (productDetails in productDetailsList) {
                                Log.d("******", productDetails.productId)
                                assert(productDetails.subscriptionOfferDetails != null)
                                for (subscriptionOfferDetails in productDetails.subscriptionOfferDetails!!) {
                                    when (productDetails.productId) {
                                        AppConstant.Premium_Monthly ->
                                            premiumMonthly = subscriptionOfferDetails.pricingPhases.pricingPhaseList[0].formattedPrice + "/ month"

                                        AppConstant.Premium_Annual -> premiumAnnual =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ year"

                                        AppConstant.Premium_Weekly -> premiumWeekly =
                                            subscriptionOfferDetails.pricingPhases
                                                .pricingPhaseList[0]
                                                .formattedPrice + "/ weekly"

                                    }
                                }
                            }
                        }
                    }
                    requireActivity().runOnUiThread(Runnable {
                        setPlanList()
                        BaseApplication.dismissMe()
                    })
                }
            }

            override fun onBillingServiceDisconnected() {
                billingClient!!.startConnection(this)
            }
        })
    }

    private fun setPlanList() {
        if (rootPlanList != null) {
            rootPlanList.clear()
        }

        rootPlanList.add(SubscriptionModel("My-kai Basic Plan","Plan",premiumMonthly,"",""))
        rootPlanList.add(SubscriptionModel("Popular","My-kai Standard\n" + "Plan ",premiumAnnual,"",""))
        rootPlanList.add(SubscriptionModel("Best Value","Annual\n" + "Plan",premiumWeekly,"",""))
        rootPlanList.add(SubscriptionModel("Best Value","Annual\n" + "Plan",premiumWeekly,"",""))


        /*adapter = NewSubscriptionAdapter(this, rootPlanList, this)
        binding.subscriptionview.setAdapter(adapter)
        setUpOnBoardingIndicator()
        currentOnBoardingIndicator(0)
        binding.subscriptionview.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentOnBoardingIndicator(position)
                productId = ""
                planType = ""
            }
        })*/

    }


}
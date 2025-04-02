package com.mykaimeal.planner.fragment.mainfragment.profilesetting.subscriptionplan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
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
import com.mykaimeal.planner.adapter.AdapterOnBoardingSubscriptionItem
import com.mykaimeal.planner.basedata.AppConstant
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.databinding.FragmentSubscriptionPlanOverViewBinding
import com.mykaimeal.planner.model.OnSubscriptionModel
import com.mykaimeal.planner.model.SubscriptionModel
import java.util.concurrent.Executors
import java.util.stream.Collectors

class SubscriptionPlanOverViewFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionPlanOverViewBinding
    private lateinit var slideUp: Animation
    private var billingClient: BillingClient? = null
    private var premiumMonthly = ""
    private var premiumAnnual = ""
    private var premiumWeekly = ""
    private val rootPlanList: MutableList<SubscriptionModel> = mutableListOf()

    var datalist : ArrayList<OnSubscriptionModel> = arrayListOf()
    private var adapters: AdapterOnBoardingSubscriptionItem?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentSubscriptionPlanOverViewBinding.inflate(layoutInflater, container, false)

        (activity as? MainActivity)?.binding?.apply {
            llIndicator.visibility = View.GONE
            llBottomNavigation.visibility = View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        adapters = AdapterOnBoardingSubscriptionItem(datalist)
        binding.viewpager.adapter = adapters
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

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
                imageView.setImageDrawable(ContextCompat.getDrawable(
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

        binding.imageBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        // List of onboarding items
        datalist.add(OnSubscriptionModel(R.drawable.image_1,true))

        datalist.add(OnSubscriptionModel(R.drawable.image_2,false))

        datalist.add(OnSubscriptionModel(R.drawable.image_3,false))

        datalist.add(OnSubscriptionModel(R.drawable.image_4,false))

        datalist.add(OnSubscriptionModel(R.drawable.image_5,false))

//        datalist.add(OnSubscriptionModel(R.drawable.banner_images_subscription,true))
//
//        datalist.add(OnSubscriptionModel(R.drawable.subscription_onboarding_2,false))
//
//        datalist.add(OnSubscriptionModel(R.drawable.subscription_onboarding_3,false))
//
//        datalist.add(OnSubscriptionModel(R.drawable.subscription_onboarding_4,false))
//
//        datalist.add(OnSubscriptionModel(R.drawable.subscription_onboarding_5,false))

        /// set indicator for onboarding
        setUpOnBoardingIndicator()
        /// set current indicator position
        currentOnBoardingIndicator(0)

        viewPagerFunctionImpl()

        startBillingApi()

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
                    binding.tvDescriptions.text = "Kai plans your meals, Compares store prices, And creates your cart so you don’t have to."
                    binding.tvDescriptions2.text = ""
                    binding.relDescriptions2.visibility=View.INVISIBLE
                } else if (position == 1) {
                    binding.tvHeadingTitle.text = "Endless Meals to Explore"
                    binding.tvDescriptions.text = "Kai gives you access to over 80,000 recipes"
                    binding.tvDescriptions2.text = ""
                    binding.relDescriptions2.visibility=View.INVISIBLE
                } else if (position==2) {
                    binding.relDescriptions2.visibility=View.VISIBLE
                    binding.tvHeadingTitle.text = "Eat Smart, Every Day"
                    binding.tvDescriptions.text = "Kai helps you plan your week with recipes tailored to your preferences"
                    binding.tvDescriptions2.text = "Stay on top of your nutrition with Kai’s daily nutrition tracker"

                }else if (position==3){
                    binding.relDescriptions2.visibility=View.VISIBLE
                    binding.tvHeadingTitle.text = "Maximum Savings, Zero Hassle"
                    binding.tvDescriptions.text = "One tap, and all your weekly ingredients are in your cart"
                    binding.tvDescriptions2.text = "Compare grocery prices at nearby stores & have them delivered  right to your door"
                }else{
                    binding.relDescriptions2.visibility=View.VISIBLE
                    binding.tvHeadingTitle.text = "Show Me the Money!"
                    binding.tvDescriptions.text = "Users save an average of \$64 a week that’s an amazing \$256* a month!"
                    binding.tvDescriptions2.text = "With Kai, smart choices aren't just smart. They’re money in the bank"
                }

                currentOnBoardingIndicator(position)
            }
        })

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
package com.yesitlabs.mykaapp.fragment.mainfragment.profilesetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentNotificationBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.notificationviewmodel.NotificationViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.notificationviewmodel.apiresponse.NotificationApiResponse
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentNotificationBinding? = null
    private lateinit var viewModel: NotificationViewModel

    private var pushNotification: Int = 0
    private var recipeRecommendations: Int = 0
    private var productUpdates: Int = 0
    private var promotionalUpdates: Int = 0
    private var allNotifications: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        setupUI()
        setupViewModel()
        setupBackButtonHandler()
        fetchNotificationList()

        return binding!!.root
    }

    private fun setupUI() {
        val activity = activity as MainActivity
        activity.binding!!.llIndicator.visibility = View.GONE
        activity.binding!!.llBottomNavigation.visibility = View.GONE

        binding!!.apply {
            imgBackNotification.setOnClickListener(this@NotificationFragment)
            textEnableNotification.setOnClickListener(this@NotificationFragment)
            textPushNotification.setOnClickListener(this@NotificationFragment)
            textRecipeRecommendations.setOnClickListener(this@NotificationFragment)
            textProductUpdates.setOnClickListener(this@NotificationFragment)
            textPromotionalUpdates.setOnClickListener(this@NotificationFragment)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[NotificationViewModel::class.java]
    }

    private fun setupBackButtonHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }

    private fun fetchNotificationList() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchNotificationData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun fetchNotificationData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.notificationRequest({ result ->
                BaseApplication.dismissMe()
                handleApiResponse(result)
            }, "", "", "", "")
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, NotificationApiResponse::class.java)
            Log.d("@@@ notification profile", "message :- $data")

            if (apiModel.code == 200 && apiModel.success) {
                updateNotificationStates(apiModel)
                updateUI()
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

    private fun updateNotificationStates(apiModel: NotificationApiResponse) {

        pushNotification = apiModel.data.push_notification!!
        recipeRecommendations = apiModel.data.recipe_recommendations!!
        productUpdates = apiModel.data.product_updates!!
        promotionalUpdates = apiModel.data.promotional_updates!!

        allNotifications = if ((pushNotification == 1 && recipeRecommendations == 1 && productUpdates == 1 && promotionalUpdates == 1)) 1 else 0

    }

    private fun updateUI() {
        binding!!.apply {
            textPushNotification.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.push_notification_icon, 0,
                if (pushNotification == 0) R.drawable.toggle_gray_off_icon else R.drawable.toggle_on_icon, 0
            )
            textRecipeRecommendations.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.recipe_recommendation_icon, 0,
                if (recipeRecommendations == 0) R.drawable.toggle_gray_off_icon else R.drawable.toggle_on_icon, 0
            )
            textProductUpdates.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.product_updates_icon, 0,
                if (productUpdates == 0) R.drawable.toggle_gray_off_icon else R.drawable.toggle_on_icon, 0
            )
            textPromotionalUpdates.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.promotional_icon, 0,
                if (promotionalUpdates == 0) R.drawable.toggle_gray_off_icon else R.drawable.toggle_on_icon, 0
            )
            textEnableNotification.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.notification_icons, 0,
                if (allNotifications == 0) R.drawable.toggle_gray_off_icon else R.drawable.toggle_on_icon, 0
            )
        }
    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgBackNotification -> findNavController().navigateUp()
            R.id.textEnableNotification -> toggleAllNotifications()
            R.id.textPushNotification -> toggleNotificationState(pushNotification, "push")
            R.id.textRecipeRecommendations -> toggleNotificationState(recipeRecommendations, "recipe")
            R.id.textProductUpdates -> toggleNotificationState(productUpdates, "product")
            R.id.textPromotionalUpdates -> toggleNotificationState(promotionalUpdates, "promo")
        }
    }

    private fun toggleAllNotifications() {
        val state = if (allNotifications == 0) "1" else "0"
        makeNotificationRequest(state, state, state, state)
    }

    private fun toggleNotificationState(currentState: Int, type: String) {
        val state = if (currentState == 0) "1" else "0"
        when (type) {
            "push" -> makeNotificationRequest(state, recipeRecommendations.toString(), productUpdates.toString(), promotionalUpdates.toString())
            "recipe" -> makeNotificationRequest(pushNotification.toString(), state, productUpdates.toString(), promotionalUpdates.toString())
            "product" -> makeNotificationRequest(pushNotification.toString(), recipeRecommendations.toString(), state, promotionalUpdates.toString())
            "promo" -> makeNotificationRequest(pushNotification.toString(), recipeRecommendations.toString(), productUpdates.toString(), state)
        }
    }

    private fun makeNotificationRequest(push: String, recipe: String, product: String, promo: String) {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.notificationRequest({ result ->
                BaseApplication.dismissMe()
                handleApiResponse(result)
            }, push, recipe, product, promo)
        }
    }
}

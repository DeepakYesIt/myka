package com.mykameal.planner.fragment.mainfragment.hometab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.databinding.FragmentDirectionSteps2RecipeDetailsFragmentBinding
import java.util.Locale

class DirectionSteps2RecipeDetailsFragment : Fragment() {

    private var binding:FragmentDirectionSteps2RecipeDetailsFragmentBinding?=null
    private var totalProgressValue:Int=0
    private val START_TIME_IN_MILLIS: Long = 1520000
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentDirectionSteps2RecipeDetailsFragmentBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
            }
        })

        binding!!.progressBar.max=2
        totalProgressValue=2
        updateProgress(2)

        binding!!.imgStep2RecipeDetails.setOnClickListener{
            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
        }

        binding!!.textStartTimer.setOnClickListener{
            binding!!.textStartTimer.isEnabled = false
            startTime()
        }

        binding!!.tvPreviousBtn.setOnClickListener{
            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
        }

        binding!!.tvNextStepBtn.setOnClickListener{
            cookedMealsDialog()
        }

        return binding!!.root
    }

    private fun cookedMealsDialog() {
        val dialogCookedMeals: Dialog = context?.let { Dialog(it) }!!
        dialogCookedMeals.setContentView(R.layout.alert_dialog_cooked_meals)
        dialogCookedMeals.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogCookedMeals.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlNextBtn = dialogCookedMeals.findViewById<RelativeLayout>(R.id.rlNextBtn)
        val imageYes = dialogCookedMeals.findViewById<ImageView>(R.id.imageYes)
        val imageNo = dialogCookedMeals.findViewById<ImageView>(R.id.imageNo)
        dialogCookedMeals.show()
        dialogCookedMeals.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlNextBtn.setOnClickListener {
            addFridgeDialog()
            dialogCookedMeals.dismiss()
        }

        imageYes.setOnClickListener{
            imageYes.setImageResource(R.drawable.radio_select_icon)
            imageNo.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageNo.setOnClickListener{
            imageYes.setImageResource(R.drawable.radio_unselect_icon)
            imageNo.setImageResource(R.drawable.radio_select_icon)
        }
    }

    private fun addFridgeDialog() {
        val dialogCookedMeals: Dialog = context?.let { Dialog(it) }!!
        dialogCookedMeals.setContentView(R.layout.alert_dialog_fridge_freezer)
        dialogCookedMeals.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogCookedMeals.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val relDoneBtn = dialogCookedMeals.findViewById<RelativeLayout>(R.id.relDoneBtn)
        val imageFridge = dialogCookedMeals.findViewById<ImageView>(R.id.imageFridge)
        val imageFreezer = dialogCookedMeals.findViewById<ImageView>(R.id.imageFreezer)
        dialogCookedMeals.show()
        dialogCookedMeals.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        relDoneBtn.setOnClickListener {
            dialogCookedMeals.dismiss()
            findNavController().navigate(R.id.rateYourMealFragment)
        }

        imageFridge.setOnClickListener{
            imageFridge.setImageResource(R.drawable.radio_select_icon)
            imageFreezer.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageFreezer.setOnClickListener{
            imageFridge.setImageResource(R.drawable.radio_unselect_icon)
            imageFreezer.setImageResource(R.drawable.radio_select_icon)
        }

    }

    private fun startTime() {
        object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                binding!!.textStartTimer.setTextColor(Color.parseColor("#828282"))
                updateCountDownText()
            }

            override fun onFinish() {
                mTimeLeftInMillis = 120000
                binding!!.textStartTimer.setTextColor(Color.parseColor("#06C169"))
                binding!!.textStartTimer.isEnabled = true
            }
        }.start()
    }

    private fun updateCountDownText() {
        val minutes = mTimeLeftInMillis.toInt() / 1000 / 60
        val seconds = mTimeLeftInMillis.toInt() / 1000 % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d: %02d", minutes, seconds)
        binding!!.tvTiming.text = "00 : "+timeLeftFormatted + ""
    }

    private fun updateProgress(progress: Int) {
        binding!!.progressBar.progress = progress
        binding!!.tvProgressText.text = "$progress /$totalProgressValue"
    }

}
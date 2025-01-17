package com.mykameal.planner.fragment.mainfragment.hometab

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.activity.MealRatingActivity
import com.mykameal.planner.basedata.BaseApplication
import com.mykameal.planner.basedata.NetworkResult
import com.mykameal.planner.databinding.FragmentDirectionSteps2RecipeDetailsFragmentBinding
import com.mykameal.planner.fragment.mainfragment.viewmodel.recipedetails.RecipeDetailsViewModel
import com.mykameal.planner.fragment.mainfragment.viewmodel.walletviewmodel.apiresponse.SuccessResponseModel
import com.mykameal.planner.messageclass.ErrorMessage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DirectionSteps2RecipeDetailsFragment : Fragment() {

    private var binding:FragmentDirectionSteps2RecipeDetailsFragmentBinding?=null
    private var totalProgressValue:Int=0
    private val START_TIME_IN_MILLIS: Long = 1520000
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    private lateinit var viewModel: RecipeDetailsViewModel
    private var mealType: String = ""
    private var uri: String = ""
    var count =1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentDirectionSteps2RecipeDetailsFragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RecipeDetailsViewModel::class.java]

        mealType = arguments?.getString("mealType", "").toString()
        uri = arguments?.getString("uri", "").toString()


        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//              findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
                findNavController().navigateUp()
            }

        })


        totalProgressValue= viewModel.getRecipeData()?.get(0)!!.recipe?.instructionLines?.size!!
        binding!!.progressBar.max=totalProgressValue
        updateProgress(1)

        binding!!.imgStep2RecipeDetails.setOnClickListener{
//            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
            findNavController().navigateUp()
        }

        binding!!.textStartTimer.setOnClickListener{
            binding!!.textStartTimer.isEnabled = false
            startTime()
        }

        binding!!.tvPreviousBtn.setOnClickListener{

            if (totalProgressValue>=count){
                count -= 1
                updateProgress(count)
            }

//            findNavController().navigateUp()
//            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)




        }

        binding!!.tvNextStepBtn.setOnClickListener{
            if (totalProgressValue>count){
                count += 1
                updateProgress(count)
            }else{
                cookedMealsDialog()
            }
//            cookedMealsDialog()
        }



        setData()

        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        if (viewModel.getRecipeData()?.get(0)!!.recipe?.images?.SMALL?.url != null) {
            Glide.with(requireContext())
                .load(viewModel.getRecipeData()?.get(0)!!.recipe?.images?.SMALL?.url)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility = View.GONE
                        return false
                    }
                })
                .into(binding!!.imageData)
        } else {
            binding!!.layProgess.root.visibility = View.GONE
        }

        if (viewModel.getRecipeData()?.get(0)!!.recipe?.label != null) {
            binding!!.tvTitle.text = "" + viewModel.getRecipeData()?.get(0)!!.recipe?.label
            binding!!.textPrepare.text = "" + viewModel.getRecipeData()?.get(0)!!.recipe?.label +":"
        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cookedMealsDialog() {
        val dialogCookedMeals: Dialog = context?.let { Dialog(it) }!!
        dialogCookedMeals.setContentView(R.layout.alert_dialog_cooked_meals)
        dialogCookedMeals.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogCookedMeals.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlNextBtn = dialogCookedMeals.findViewById<RelativeLayout>(R.id.rlNextBtn)
        val tvYes = dialogCookedMeals.findViewById<TextView>(R.id.tvYes)
        val tvNo = dialogCookedMeals.findViewById<TextView>(R.id.tvNo)

        dialogCookedMeals.show()
        dialogCookedMeals.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)



        var type=""

        tvYes.setOnClickListener{
            type="Yes"
            tvYes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_select_icon, 0)
            tvNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_unselect_icon, 0)
        }

        tvNo.setOnClickListener{
            type="NO"
            tvYes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_unselect_icon, 0)
            tvNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_select_icon, 0)
        }

        rlNextBtn.setOnClickListener {
            if (type.equals("",true)){
                BaseApplication.alertError(requireContext(), ErrorMessage.cookedMealsError, false)
            }else{
                if (type.equals("No",true)){
                    type=""
                    dialogCookedMeals.dismiss()
                }else{
                    dialogCookedMeals.dismiss()
                    addFridgeDialog()
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addFridgeDialog() {
        val dialogCookedMeals: Dialog = context?.let { Dialog(it) }!!
        dialogCookedMeals.setContentView(R.layout.alert_dialog_fridge_freezer)
        dialogCookedMeals.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogCookedMeals.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val relDoneBtn = dialogCookedMeals.findViewById<RelativeLayout>(R.id.relDoneBtn)

        val tvFridge = dialogCookedMeals.findViewById<TextView>(R.id.tvFridge)
        val tvFreezer = dialogCookedMeals.findViewById<TextView>(R.id.tvFreezer)


        dialogCookedMeals.show()
        dialogCookedMeals.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        var type=""

        tvFridge.setOnClickListener{
            type="1"
            tvFridge.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_select_icon, 0)
            tvFreezer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_unselect_icon, 0)
        }

        tvFreezer.setOnClickListener{
            type="2"
            tvFridge.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_unselect_icon, 0)
            tvFreezer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_select_icon, 0)
        }

        relDoneBtn.setOnClickListener {
            if (type.equals("",true)){
                BaseApplication.alertError(requireContext(), ErrorMessage.cookedMealsError, false)
            }else{

                // Create a JsonObject for the main JSON structure
                val jsonObject = JsonObject()
                if (uri!= null) {
                    val currentDate = LocalDate.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = currentDate.format(formatter)
                    jsonObject.addProperty("type", mealType)
                    jsonObject.addProperty("plan_type", type)
                    jsonObject.addProperty("uri", uri)
                    jsonObject.addProperty("date", formattedDate)

                }
                Log.d("json object ", "******$jsonObject")

                BaseApplication.showMe(requireContext())
                lifecycleScope.launch {
                    viewModel.recipeAddToPlanRequest({
                        BaseApplication.dismissMe()
                        handleApiAddToPlanResponse(it, dialogCookedMeals)
                    }, jsonObject)
                }

            }

        }


    }

    private fun handleApiAddToPlanResponse(
        result: NetworkResult<String>,
        dialogCookedMeals: Dialog
    ) {
        when (result) {
            is NetworkResult.Success -> handleSuccessAddToPlanResponse(
                result.data.toString(),
                dialogCookedMeals
            )

            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessAddToPlanResponse(data: String, dialogCookedMeals: Dialog) {
        try {
            val apiModel = Gson().fromJson(data, SuccessResponseModel::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                dialogCookedMeals.dismiss()
                Toast.makeText(requireContext(),apiModel.message,Toast.LENGTH_LONG).show()
                val intent=Intent(requireActivity(),MealRatingActivity::class.java)
                intent.putExtra("uri",uri)
                startActivity(intent)
//                findNavController().navigate(R.id.rateYourMealFragment)
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

    private fun openRatingScreen() {

    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
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

    @SuppressLint("SetTextI18n")
    private fun updateCountDownText() {
        val minutes = mTimeLeftInMillis.toInt() / 1000 / 60
        val seconds = mTimeLeftInMillis.toInt() / 1000 % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d: %02d", minutes, seconds)
        binding!!.tvTiming.text = "00 : $timeLeftFormatted"
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(progress: Int) {
        binding!!.progressBar.progress = progress
        binding!!.tvProgressText.text = "$progress /$totalProgressValue"

        binding!!.textPrepareDesc.text = viewModel.getRecipeData()?.get(0)!!.recipe?.instructionLines?.get(progress-1)!!

        if (progress==1){
            binding!!.tvPreviousBtn.visibility=View.GONE
        }else{
            binding!!.tvPreviousBtn.visibility=View.VISIBLE
        }




    }

}
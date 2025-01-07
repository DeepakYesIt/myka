package com.yesitlabs.mykaapp.fragment.mainfragment.hometab

import android.R.attr.value
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.yesitlabs.mykaapp.R
import com.yesitlabs.mykaapp.activity.MainActivity
import com.yesitlabs.mykaapp.adapter.AdapterRecipeItem
import com.yesitlabs.mykaapp.adapter.ChooseDayAdapter
import com.yesitlabs.mykaapp.adapter.CookWareAdapter
import com.yesitlabs.mykaapp.adapter.IngredientsRecipeAdapter
import com.yesitlabs.mykaapp.basedata.BaseApplication
import com.yesitlabs.mykaapp.basedata.NetworkResult
import com.yesitlabs.mykaapp.databinding.FragmentRecipeDetailsBinding
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.RecipeDetailsViewModel
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.apiresponse.Data
import com.yesitlabs.mykaapp.fragment.mainfragment.viewmodel.recipedetails.apiresponse.RecipeDetailsApiResponse
import com.yesitlabs.mykaapp.messageclass.ErrorMessage
import com.yesitlabs.mykaapp.model.DataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects


@AndroidEntryPoint
class RecipeDetailsFragment : Fragment() {

    private var binding: FragmentRecipeDetailsBinding? = null
    private var ingredientsRecipeAdapter: IngredientsRecipeAdapter? = null
    private var cookWareAdapter: CookWareAdapter? = null
    private var adapterRecipeItem: AdapterRecipeItem? = null
    val dataList = ArrayList<DataModel>()
    private val dataList1 = ArrayList<DataModel>()
    private val dataList2 = ArrayList<DataModel>()
    private var rcyChooseDaySch: RecyclerView? = null
    private var tvWeekRange: TextView? = null
    private var chooseDayAdapter: ChooseDayAdapter? = null
    private val calendar = Calendar.getInstance()
    private var selectAll:Boolean?=false
    private var quantity:Int=1
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private lateinit var viewModel: RecipeDetailsViewModel
    private var uri:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecipeDetailsBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RecipeDetailsViewModel::class.java]

        uri=arguments?.getString("uri","").toString()

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.GONE

        setupBackNavigation()


        ingredientsModel()
        initialize()

        // When screen load then api call
        fetchDataOnLoad()

        return binding!!.root
    }

    private fun fetchDataOnLoad() {
        if (BaseApplication.isOnline(requireActivity())) {
            fetchRecipeDetailsData()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }



    private fun fetchRecipeDetailsData() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            viewModel.recipeDetailsRequest({
                BaseApplication.dismissMe()
                handleApiResponse(it)
            }, uri)
        }
    }

    private fun handleApiResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleSuccessResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, RecipeDetailsApiResponse::class.java)
            Log.d("@@@ Recipe Details ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data!=null && apiModel.data.size>0){
                    showData(apiModel.data)
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
    private fun showData(data: MutableList<Data>) {

        if (data[0].recipe?.images?.THUMBNAIL?.url!=null){
            Glide.with(requireContext())
                .load(data[0].recipe?.images?.THUMBNAIL?.url)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.layProgess.root.visibility=View.GONE
                        return false
                    }
                })
                .into(binding!!.imageData)
        }else{
            binding!!.layProgess.root.visibility=View.GONE
        }

        if (data[0].recipe?.label!=null){
            binding!!.tvTitle.text = ""+data[0].recipe?.label
        }

        if (data[0].recipe?.calories!=null){
            binding!!.tvCalories.text = ""+data[0].recipe?.calories?.toInt()
        }

        if (data[0].recipe?.totalNutrients?.FAT?.quantity!=null){
            binding!!.tvFat.text = ""+data[0].recipe?.totalNutrients?.FAT?.quantity?.toInt()
        }

        if (data[0].recipe?.totalNutrients?.PROCNT?.quantity!=null){
            binding!!.tvProtein.text = ""+data[0].recipe?.totalNutrients?.PROCNT?.quantity?.toInt()
        }

        if (data[0].recipe?.totalNutrients?.CHOCDF?.quantity!=null){
            binding!!.tvCarbs.text = ""+data[0].recipe?.totalNutrients?.CHOCDF?.quantity?.toInt()
        }


        if (data[0].recipe?.totalTime!=null){
            binding!!.tvTotaltime.text = ""+data[0].recipe?.totalTime+" min "
        }


    }

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }



    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }


    private fun initialize() {

        binding!!.imgPlusValue.setOnClickListener{
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        binding!!.imgMinusValue.setOnClickListener{
            if (quantity > 1) {
                quantity--
                updateValue()
            }else{
                Toast.makeText(requireActivity(),"Minimum serving atleast value is one",Toast.LENGTH_LONG).show()
            }
        }

        binding!!.tvAddToPlan.setOnClickListener {
            chooseDayDialog()
        }

        binding!!.relBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding!!.llIngredients.setOnClickListener {
            binding!!.textIngredients.setBackgroundResource(R.drawable.select_bg)
            binding!!.textCookWare.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textRecipe.setBackgroundResource(R.drawable.unselect_bg)

            binding!!.textIngredients.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.textCookWare.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textRecipe.setTextColor(Color.parseColor("#3C4541"))

            binding!!.relServingsPeople.visibility = View.VISIBLE
            binding!!.layBottomPlanBasket.visibility = View.VISIBLE
            binding!!.relIngSelectAll.visibility = View.VISIBLE
            binding!!.relCookware.visibility = View.GONE
            binding!!.relRecipe.visibility = View.GONE
            binding!!.textStepInstructions.visibility = View.GONE
            ingredientsModel()

        }

        binding!!.llCookWare.setOnClickListener {
            binding!!.textIngredients.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textCookWare.setBackgroundResource(R.drawable.select_bg)
            binding!!.textRecipe.setBackgroundResource(R.drawable.unselect_bg)

            binding!!.textIngredients.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textCookWare.setTextColor(Color.parseColor("#FFFFFF"))
            binding!!.textRecipe.setTextColor(Color.parseColor("#3C4541"))

            binding!!.relServingsPeople.visibility = View.GONE
            binding!!.layBottomPlanBasket.visibility = View.GONE
            binding!!.relIngSelectAll.visibility = View.GONE
            binding!!.relCookware.visibility = View.VISIBLE
            binding!!.relRecipe.visibility = View.GONE
            binding!!.textStepInstructions.visibility = View.GONE

            cookWareModel()

        }

        binding!!.llRecipe.setOnClickListener {
            binding!!.textIngredients.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textCookWare.setBackgroundResource(R.drawable.unselect_bg)
            binding!!.textRecipe.setBackgroundResource(R.drawable.select_bg)

            binding!!.textIngredients.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textCookWare.setTextColor(Color.parseColor("#3C4541"))
            binding!!.textRecipe.setTextColor(Color.parseColor("#FFFFFF"))

            binding!!.relServingsPeople.visibility = View.GONE
            binding!!.layBottomPlanBasket.visibility = View.GONE
            binding!!.relIngSelectAll.visibility = View.GONE
            binding!!.relCookware.visibility = View.GONE
            binding!!.relRecipe.visibility = View.VISIBLE
            binding!!.textStepInstructions.visibility = View.VISIBLE

            recipeModel()

        }

        binding!!.textStepInstructions.setOnClickListener {
            findNavController().navigate(R.id.directionSteps1RecipeDetailsFragment)
        }

        binding!!.tvSelectAllBtn.setOnClickListener {
            if (selectAll==true){
                ingredientsRecipeAdapter?.setCheckEnabled(false)
                val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.orange_uncheck_box_images)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvSelectAllBtn.setCompoundDrawables(null, null, drawableEnd, null)
                selectAll=false
            }else{
                val drawableEnd = ContextCompat.getDrawable(requireActivity(), R.drawable.orange_checkbox_images)
                drawableEnd!!.setBounds(0, 0, drawableEnd.intrinsicWidth, drawableEnd.intrinsicHeight)
                binding!!.tvSelectAllBtn.setCompoundDrawables(null, null, drawableEnd, null)
                selectAll=true
                ingredientsRecipeAdapter?.setCheckEnabled(true)
            }
        }

    }

    private fun updateValue() {
        binding!!.tvValues.text = String.format("%02d", quantity)
    }

    private fun chooseDayDialog() {
        val dialogChooseDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseDay.setContentView(R.layout.alert_dialog_choose_day)
        dialogChooseDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rcyChooseDaySch = dialogChooseDay.findViewById(R.id.rcyChooseDaySch)
        tvWeekRange = dialogChooseDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseDay.findViewById<ImageView>(R.id.btnPrevious)
        val btnNext = dialogChooseDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseDay.show()
        updateWeekRange()
        dialogChooseDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        cookingScheduleModel()

        rlDoneBtn.setOnClickListener {
            chooseDayMealTypeDialog()
            dialogChooseDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }
    }

    private fun changeWeek(weeks: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        updateWeekRange()
    }

    private fun cookingScheduleModel() {
        val dataList = ArrayList<DataModel>()
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()
        val data6 = DataModel()
        val data7 = DataModel()

        data1.title = "Monday"
        data1.isOpen = false
        data1.type = "CookingSchedule"

        data2.title = "Tuesday"
        data2.isOpen = false
        data2.type = "CookingSchedule"

        data3.title = "Wednesday"
        data3.isOpen = false
        data3.type = "CookingSchedule"

        data4.title = "Thursday"
        data4.isOpen = false
        data4.type = "CookingSchedule"

        data5.title = "Friday"
        data5.isOpen = false
        data5.type = "CookingSchedule"

        data6.title = "Saturday"
        data6.isOpen = false
        data6.type = "CookingSchedule"

        data7.title = "Sunday"
        data7.isOpen = false
        data7.type = "CookingSchedule"

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)
        dataList.add(data6)
        dataList.add(data7)

        chooseDayAdapter = ChooseDayAdapter(dataList, requireActivity())
        rcyChooseDaySch!!.adapter = chooseDayAdapter
    }

    private fun updateWeekRange() {
        val startOfWeek = calendar.apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }.time

        val endOfWeek = calendar.apply {
            add(Calendar.DAY_OF_WEEK, 6)
        }.time

        tvWeekRange!!.text = "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"
        calendar.add(Calendar.DAY_OF_WEEK, -6) // Reset endOfWeek calculation
    }

    private fun chooseDayMealTypeDialog() {
        val dialogChooseMealDay: Dialog = context?.let { Dialog(it) }!!
        dialogChooseMealDay.setContentView(R.layout.alert_dialog_choose_day_meal_type)
        dialogChooseMealDay.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogChooseMealDay.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvWeekRange = dialogChooseMealDay.findViewById(R.id.tvWeekRange)
        val rlDoneBtn = dialogChooseMealDay.findViewById<RelativeLayout>(R.id.rlDoneBtn)
        val btnPrevious = dialogChooseMealDay.findViewById<ImageView>(R.id.btnPrevious)
        val imgBreakfastRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imgBreakfastRadio)
        val imageLunchRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageLunchRadio)
        val imageDinnerRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageDinnerRadio)
        val imageSnacksRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageSnacksRadio)
        val imageBrunchRadio = dialogChooseMealDay.findViewById<ImageView>(R.id.imageBrunchRadio)
        val btnNext = dialogChooseMealDay.findViewById<ImageView>(R.id.btnNext)
        dialogChooseMealDay.show()
        updateWeekRange()
        dialogChooseMealDay.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        imgBreakfastRadio.setOnClickListener {
            imgBreakfastRadio.setImageResource(R.drawable.radio_select_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageLunchRadio.setOnClickListener {
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_select_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageDinnerRadio.setOnClickListener {
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_select_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageSnacksRadio.setOnClickListener{
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_select_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_unselect_icon)
        }

        imageBrunchRadio.setOnClickListener{
            imgBreakfastRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageLunchRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageDinnerRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageSnacksRadio.setImageResource(R.drawable.radio_unselect_icon)
            imageBrunchRadio.setImageResource(R.drawable.radio_select_icon)
        }

        rlDoneBtn.setOnClickListener {

            dialogChooseMealDay.dismiss()
        }

        btnPrevious.setOnClickListener {
            changeWeek(-1)
        }

        btnNext.setOnClickListener {
            changeWeek(1)
        }
    }

    private fun ingredientsModel() {
        if (dataList != null) {
            dataList.clear()
        }
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Olive Oil"
        data1.description = "1 Tbsp"
        data1.isOpen = false
        data1.type = "IngDetails"
        data1.image = R.drawable.olive_image

        data2.title = "Garlic Mayo"
        data2.description = "3 Tbsp"
        data2.isOpen = false
        data2.type = "IngDetails"
        data2.image = R.drawable.garlic_mayo_image

        data3.title = "Olive Oil"
        data3.description = "3 Tbsp"
        data3.isOpen = false
        data3.type = "IngDetails"
        data3.image = R.drawable.olive_oil_image2

        data4.title = "Olive Oil"
        data4.description = "1 kg"
        data4.isOpen = false
        data4.type = "IngDetails"
        data4.image = R.drawable.olive_chicken_image

        data5.title = "Tomato"
        data5.description = "0.5 kg"
        data5.isOpen = false
        data5.type = "IngDetails"
        data5.image = R.drawable.tomato_ing_image

        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        dataList.add(data4)
        dataList.add(data5)



        ingredientsRecipeAdapter = IngredientsRecipeAdapter(dataList, requireActivity())
        binding!!.rcyIngCookWareRecipe.adapter = ingredientsRecipeAdapter
    }

    private fun cookWareModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()
        val data5 = DataModel()

        data1.title = "Cooker"
        data1.isOpen = false
        data1.type = "CookWare"
        data1.image = R.drawable.cooker_image

        data2.title = "Cutting board"
        data2.isOpen = false
        data2.type = "CookWare"
        data2.image = R.drawable.cutting_board_image

        data3.title = "Grater"
        data3.isOpen = false
        data3.type = "CookWare"
        data3.image = R.drawable.grater_image

        data4.title = "Peeler"
        data4.isOpen = false
        data4.type = "CookWare"
        data4.image = R.drawable.peeler_image

        data5.title = "Mixing bowl"
        data5.isOpen = false
        data5.type = "CookWare"
        data5.image = R.drawable.mixing_bowl_image

        dataList1.add(data1)
        dataList1.add(data2)
        dataList1.add(data3)
        dataList1.add(data4)
        dataList1.add(data5)

        cookWareAdapter = CookWareAdapter(dataList1, requireActivity())
        binding!!.rcyIngCookWareRecipe.adapter = cookWareAdapter
    }

    private fun recipeModel() {
        val data1 = DataModel()
        val data2 = DataModel()
        val data3 = DataModel()
        val data4 = DataModel()

        data1.title = "Step 1:"
        data1.description = "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispum"
        data1.isOpen = false
        data1.type = "Recipe"

        data2.title = "Step 2:"
        data2.description =
            "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum Lorem ispum"
        data2.isOpen = false
        data2.type = "Recipe"

        data3.title = "Step 3:"
        data3.description =
            "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum Lorem ispumLorem ispum Lorem ispum"
        data3.isOpen = false
        data3.type = "Recipe"

        data4.title = "Step 4:"
        data4.description =
            "Lorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum Lorem ispumLorem ispum Lorem ispum Lorem ispumLorem ispum"
        data4.isOpen = false
        data4.type = "Recipe"

        dataList2.add(data1)
        dataList2.add(data2)
        dataList2.add(data3)
        dataList2.add(data4)

        adapterRecipeItem = AdapterRecipeItem(dataList2, requireActivity())
        binding!!.rcyIngCookWareRecipe.adapter = adapterRecipeItem
    }

}
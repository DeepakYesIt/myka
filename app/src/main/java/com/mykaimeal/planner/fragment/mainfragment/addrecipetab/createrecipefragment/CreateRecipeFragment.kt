package com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mykaimeal.planner.R
import com.mykaimeal.planner.activity.MainActivity
import com.mykaimeal.planner.adapter.AdapterCookIngredientsItem
import com.mykaimeal.planner.adapter.AdapterCreateIngredientsItem
import com.mykaimeal.planner.basedata.BaseApplication
import com.mykaimeal.planner.basedata.NetworkResult
import com.mykaimeal.planner.commonworkutils.CommonWorkUtils
import com.mykaimeal.planner.commonworkutils.MediaUtility
import com.mykaimeal.planner.commonworkutils.UriToBase64
import com.mykaimeal.planner.commonworkutils.imageUrlToBase64
import com.mykaimeal.planner.databinding.FragmentCreateRecipeBinding
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.CreateRecipeNameModel
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.CreateRecipeNameModelData
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.CreateRecipeSuccessModel
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.Recipe
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.model.RecyclerViewCookIngModel
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefragment.viewmodel.CreateRecipeViewModel
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefromimage.model.RecyclerViewItemModel
import com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.CookBookListResponse
import com.mykaimeal.planner.messageclass.ErrorMessage
import kotlinx.coroutines.launch
import java.io.File

class CreateRecipeFragment : Fragment(), AdapterCreateIngredientsItem.UploadImage,AdapterCookIngredientsItem.CookIngName {

    private var binding: FragmentCreateRecipeBinding? = null
    private var quantity: Int = 1
    private var file: File? = null
    private var ingredientList: MutableList<RecyclerViewItemModel>? = null
    private var cookList: MutableList<RecyclerViewCookIngModel>? = null
    private var adapter: AdapterCreateIngredientsItem? = null
    private var showIngredientImage: ImageView? = null
    private var showCrossIngImage: ImageView? = null
    private var root: RelativeLayout? = null
    private val ingredientLists: MutableList<String>? = null
    private var position: Int? = 0
    private var checkBase64Url:Boolean?=false
    private var recipeMainImageUri: String? = null
    private var cookDescription: String? = null
    private var recipeStatus: String? = "0"
    private var adapterCook: AdapterCookIngredientsItem? = null
    private lateinit var createRecipeViewModel: CreateRecipeViewModel
    private lateinit var commonWorkUtils: CommonWorkUtils

    private var recipeName:String?=""
    private var cookbookList: MutableList<com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data> =
        mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentCreateRecipeBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.GONE

        commonWorkUtils = CommonWorkUtils(requireActivity())

        createRecipeViewModel = ViewModelProvider(requireActivity())[CreateRecipeViewModel::class.java]

        recipeName = arguments?.getString("name", "").toString()

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    addRecipeDiscardDialog()
                }
            })

        initialize()

        return binding!!.root
    }


    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->

                // val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                file = MediaUtility.getPath(requireContext(), uri)?.let { File(it) }
                /*processImage(bitmap)*/
                // Now you can send the image URI to Vision API for processing
                // Convert image to Base64

                binding!!.addImageIcon.visibility = View.GONE
                checkBase64Url=true
                recipeMainImageUri = UriToBase64(requireActivity(), uri)
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(binding!!.addImages)
            }
        }
    }

    private fun initialize() {

        if (cookbookList != null) {
            cookbookList.clear()
        }

        val data = com.mykaimeal.planner.fragment.mainfragment.viewmodel.planviewmodel.apiresponsecookbooklist.Data(
                "", "", 0, "", "Favourites", 0, "", 0)
        cookbookList.add(0, data)

        ingredientList = mutableListOf()

        // Add the first blank EditText item
        ingredientList?.add(RecyclerViewItemModel("", "", false,"",""))

        // Set up RecyclerView and Adapter
        adapter = AdapterCreateIngredientsItem(ingredientList!!, requireActivity(), this)
        binding!!.rcyCreateIngredients.adapter = adapter

        // Handle "+" button click
        binding!!.imageCrtIngPlus.setOnClickListener {
            if (adapter!!.isAllFieldsFilled()){
                adapter!!.addNewItem()
                binding!!.rcyCreateIngredients.scrollToPosition(ingredientList!!.size - 1) // Scroll to the new item
            }else{
                // Highlight empty fields
                adapter!!.highlightEmptyFields(binding!!.rcyCreateIngredients)
            }
        }

        // Update the model when quantity changes
        binding!!.etRecipeName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateBackground(binding!!.llCreateTitle, s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cookList = mutableListOf()

        // Initialize with Step-1 by default
        cookList!!.add(RecyclerViewCookIngModel(1))

        // Setup RecyclerView
        adapterCook = AdapterCookIngredientsItem(cookList!!,requireActivity(),this)
        binding!!.rcyCookInstructions.apply {
            adapter = adapterCook
        }

        // Handle "+" button click
        binding!!.imageCookIns.setOnClickListener {
     /*       adapterCook!!.addNewItem() // Add new step dynamically*/
            if (adapterCook!!.isAllIngFieldFilled()){
                adapterCook!!.addNewItem()
                binding!!.rcyCookInstructions.scrollToPosition(cookList!!.size - 1) // Scroll to the new item
            }else{
                // Highlight empty fields
                adapterCook!!.highlightEmptyIngFields(binding!!.rcyCookInstructions)
            }
        }

        binding!!.imgMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateValue()
            } else {
                Toast.makeText(requireActivity(), "Minimum serving atleast value is one", Toast.LENGTH_LONG).show()
            }
        }

        binding!!.imgPlus.setOnClickListener {
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        binding!!.relBacks.setOnClickListener {
            addRecipeDiscardDialog()
        }

        binding!!.layBottom.setOnClickListener {
            if (validate()) {
                if (BaseApplication.isOnline(requireActivity())) {
                    createRecipeApi()
                } else {
                    BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
                }
            }
            /*  addRecipeSuccessDialog()*/
        }

        binding!!.textPrivate.setOnClickListener {
            val drawableStart =
                ContextCompat.getDrawable(requireActivity(), R.drawable.radio_uncheck_gray_icon)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight
            )
            binding!!.textPublic.setCompoundDrawables(drawableStart, null, null, null)

            val drawableStart1 =
                ContextCompat.getDrawable(requireActivity(), R.drawable.radio_check_icon)
            drawableStart1!!.setBounds(0, 0, drawableStart1.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPrivate.setCompoundDrawables(drawableStart1, null, null, null)
            recipeStatus="0"
        }

        binding!!.textPublic.setOnClickListener {
            val drawableStart =
                ContextCompat.getDrawable(requireActivity(), R.drawable.radio_check_icon)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPublic.setCompoundDrawables(drawableStart, null, null, null)

            val drawableStart1 =
                ContextCompat.getDrawable(requireActivity(), R.drawable.radio_uncheck_gray_icon)
            drawableStart1!!.setBounds(
                0,
                0,
                drawableStart1.intrinsicWidth,
                drawableStart.intrinsicHeight
            )
            binding!!.textPrivate.setCompoundDrawables(drawableStart1, null, null, null)
            recipeStatus="1"
        }

        binding!!.addImages.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop() // Crop image (Optional)
                .compress(1024 * 5) // Compress the image to less than 5 MB
                .maxResultSize(250, 250) // Set max resolution
                .createIntent { intent -> pickImageLauncher.launch(intent) }
        }

        if (BaseApplication.isOnline(requireActivity())) {
            getCookBookList()
        } else {
            BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
        }
    }

    private fun validate(): Boolean {

        if (binding!!.etRecipeName.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.recipeName, false)
            return false
        } else if (binding!!.edtTotalTime.text.toString().trim().isEmpty()) {
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.validTotalTime, false)
            return false
        } /*else if (binding!!.spinnerCookBook.text.toString().trim().isEmpty()){
            commonWorkUtils.alertDialog(requireActivity(), ErrorMessage.selectCookBookError, false)
            return false
        }*/
        return true

    }


    private fun updateBackground(llCreateTitle: LinearLayout, text: String) {
        if (text.isNotEmpty()) {
            llCreateTitle.setBackgroundResource(R.drawable.create_select_bg) // Change this drawable
        } else {
            llCreateTitle.setBackgroundResource(R.drawable.create_unselect_bg)  // Default background
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun createRecipeApi() {
        lifecycleScope.launch {
            if (!checkBase64Url!!) {
                recipeMainImageUri = imageUrlToBase64(recipeMainImageUri!!)
            }

            val jsonObject = JsonObject()
            Log.d("fdfdf", "ffd:--0" + ingredientList!!.size)

            // Create a JsonArray for ingredients
            val ingArray = JsonArray()
            val prepArray = JsonArray()

            // Extract required fields dynamically
            ingredientList?.forEach { item ->
                val ingredientString = "${item.ingredientName},${item.quantity},${item.measurement}"
                ingArray.add(ingredientString)
            }

            // Prepare prep steps
            cookList?.forEach { items ->
                prepArray.add(items.description)
            }

            var cookBookID = ""
            if (binding!!.spinnerCookBook.text.toString().isNotEmpty()) {
                cookBookID = cookbookList[binding!!.spinnerCookBook.selectedIndex].id.toString()
            }

            // Add data to JSON object
            jsonObject.addProperty("recipe_key", recipeStatus.toString())
            jsonObject.addProperty("cook_book", cookBookID)
            jsonObject.addProperty("title", binding!!.etRecipeName.text.toString().trim())
            jsonObject.add("ingr", ingArray)
            jsonObject.addProperty("summary", binding!!.edtSummary.text.toString().trim())
            jsonObject.addProperty("yield", binding!!.textValue.text.toString().trim())
            jsonObject.addProperty("totalTime", binding!!.edtTotalTime.text.toString().trim())
            jsonObject.add("prep", prepArray)
            jsonObject.addProperty("img", recipeMainImageUri ?: "")  // Ensure it's not null
            jsonObject.addProperty("tags", binding!!.etRecipeName.text.toString().trim())

            Log.d("json object", "******$jsonObject")

            BaseApplication.showMe(requireContext())

            // Call API after everything is ready
            createRecipeViewModel.createRecipeRequestApi({
                BaseApplication.dismissMe()
                handleApiCreateRecipeResponse(it)
            }, jsonObject)
        }

        /*  // Create a JsonObject for the main JSON structure
          val jsonObject = JsonObject()

          Log.d("fdfdf","ffd:--0"+ingredientList!!.size)
  //        Log.d("fdfdfddd","ffd: "+cookbookList[binding!!.spinnerCookBook.selectedIndex].id.toString())

          // Create a JsonArray for ingredients
          val ingArray = JsonArray()
          val prepArray = JsonArray()

          // Extract required fields dynamically
          ingredientList?.forEach { item ->
              val ingredientString = "${item.ingredientName},${item.quantity},${item.measurement}"
              ingArray.add(ingredientString)  // Directly adding as JsonPrimitive
          }

          // Prepare prep steps
          cookList?.forEach { items ->
              prepArray.add(items.description)  // Adding description directly
          }

          if (checkBase64Url == false){
              recipeMainImageUri = imageUrlToBase64(recipeMainImageUri!!)
          }
          var cookBookID=""
          if (binding!!.spinnerCookBook.text.toString() != ""){
              cookBookID=cookbookList[binding!!.spinnerCookBook.selectedIndex].id.toString()
          }

          jsonObject.addProperty("recipe_key", recipeStatus.toString())
          jsonObject.addProperty("cook_book", cookBookID)
          jsonObject.addProperty("title", binding!!.etRecipeName.text.toString().trim())
          jsonObject.add("ingr", ingArray)
          jsonObject.addProperty("summary", binding!!.edtSummary.text.toString().trim())
          jsonObject.addProperty("yield", binding!!.textValue.text.toString().trim())
          jsonObject.addProperty("totalTime", binding!!.edtTotalTime.text.toString().trim())
          jsonObject.add("prep",prepArray)
          jsonObject.addProperty("img", recipeMainImageUri)


          jsonObject.addProperty("tags",binding!!.etRecipeName.text.toString().trim())

          Log.d("json object ", "******$jsonObject")

          BaseApplication.showMe(requireContext())
          lifecycleScope.launch {
              createRecipeViewModel.createRecipeRequestApi({
                  BaseApplication.dismissMe()
                  handleApiCreateRecipeResponse(it)
              }, jsonObject)
          }*/
    }

    private fun handleApiCreateRecipeResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessCreateApiResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }
    }

    private fun getCookBookList() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            createRecipeViewModel.getCookBookRequest {
                BaseApplication.dismissMe()
                handleApiCookBookResponse(it)
            }
        }
    }


    private fun searchRecipeByNameApi() {
        BaseApplication.showMe(requireContext())
        lifecycleScope.launch {
            createRecipeViewModel.recipeSearchApi({
                BaseApplication.dismissMe()
                when (it) {
                    is NetworkResult.Success -> {
                        try {
                            val gson = Gson()
                            val recipeNameModel = gson.fromJson(it.data, CreateRecipeNameModel::class.java)
                            if (recipeNameModel.code == 200 && recipeNameModel.success == true) {
                                showDataInUi(recipeNameModel.data)
                            } else {
                                if (recipeNameModel.code == ErrorMessage.code) {
                                    showAlert(recipeNameModel.message, true)
                                }else{
                                    showAlert(recipeNameModel.message, false)
                                }
                            }
                        }catch (e:Exception){
                            Log.d("CreateRecipe:","Message:--"+e.message)
                        }
                    }
                    is NetworkResult.Error -> {
                        showAlert(it.message, false)
                    }
                    else -> {
                        showAlert(it.message, false)
                    }
                }
            },recipeName)
        }
    }

    private fun showDataInUi(recipeNameModelData: List<CreateRecipeNameModelData>?) {
        try {
            if (recipeNameModelData!=null){
                if (recipeNameModelData!=null){
                    val recipe : Recipe? =recipeNameModelData[0].recipe
                    if (recipe!=null){
                        if (recipe.label !=null){
                            binding!!.etRecipeName.setText(recipe.label.toString())
                        }

                        if (recipe.images?.SMALL?.url!=null){
                            val imageUrl = recipe.images.SMALL.url
                            recipeMainImageUri = recipe.images.SMALL.url
                            checkBase64Url=false
                            Glide.with(requireActivity())
                                .load(imageUrl)
                                .error(R.drawable.no_image)
                                .placeholder(R.drawable.no_image)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding!!.layProgess.root.visibility= View.GONE
                                        binding!!.addImageIcon.visibility= View.VISIBLE
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding!!.layProgess.root.visibility= View.GONE
                                        binding!!.addImageIcon.visibility= View.GONE
                                        return false
                                    }
                                })
                                .into(binding!!.addImages)
                        }else{
                            binding!!.layProgess.root.visibility= View.GONE
                            binding!!.addImageIcon.visibility= View.VISIBLE
                        }

                        if (recipe.instructionLines!=null){
                            // Map the instruction lines to RecyclerViewCookIngModel
                            cookList = recipe.instructionLines.mapIndexed { index, instruction ->
                                RecyclerViewCookIngModel(
                                    count = index + 1,
                                    description = instruction,
                                    status = false
                                )
                            }.toMutableList()
                            adapterCook!!.update(cookList!!)
                        }

                        if (recipe.totalTime!=null && recipe.totalTime!=0){
                            binding!!.edtTotalTime.setText(recipe.totalTime.toString())
                        }

                        if (recipe.ingredients!=null){
                            // Map the response values to your IngredientModel list
                            ingredientList = recipe.ingredients.map { response ->
                                RecyclerViewItemModel(
                                    uri = response.image as String, // Set the image URI
                                    ingredientName = response.food as String, // Set the ingredient name
                                    quantity = (response.quantity as Number).toString(), // Convert quantity to String
                                    measurement = response.measure as String, // Set the measurement
                                    status = false // Default status
                                )
                            }.toMutableList()
                            adapter!!.update(ingredientList!!)
                        }
                    }
                }
            }
        }catch (e:Exception){
            Log.d("CreateRecipe:","Message:--"+e.message)
        }
    }

    private fun handleApiCookBookResponse(result: NetworkResult<String>) {
        when (result) {
            is NetworkResult.Success -> handleSuccessCookBookResponse(result.data.toString())
            is NetworkResult.Error -> showAlert(result.message, false)
            else -> showAlert(result.message, false)
        }

        if (recipeName!=""){
            if (BaseApplication.isOnline(requireActivity())) {
                searchRecipeByNameApi()
            } else {
                BaseApplication.alertError(requireContext(), ErrorMessage.networkError, false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessCookBookResponse(data: String) {
        try {
            val apiModel = Gson().fromJson(data, CookBookListResponse::class.java)
            Log.d("@@@ addMea List ", "message :- $data")
            if (apiModel.code == 200 && apiModel.success) {
                if (apiModel.data != null && apiModel.data.size > 0) {
              /*      binding!!.spinnerCookBook.setItems(cookbookList.map { it.name })*/
                    cookbookList.retainAll { it == cookbookList[0] }
                    cookbookList.addAll(apiModel.data)
                    // OR directly modify the original list
                    binding!!.spinnerCookBook.setItems(cookbookList.map { it.name })
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
    private fun handleSuccessCreateApiResponse(data: String) {
        try {
                 val apiModel = Gson().fromJson(data, CreateRecipeSuccessModel::class.java)
                 Log.d("@@@ addMea List ", "message :- $data")
                 if (apiModel.code == 200 && apiModel.success) {
                     addRecipeSuccessDialog()
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

    private fun showAlert(message: String?, status: Boolean) {
        BaseApplication.alertError(requireContext(), message, status)
    }

    private fun updateValue() {
        binding!!.textValue.text = String.format("%02d", quantity)
    }

    private fun addRecipeDiscardDialog() {
        val dialogDiscard: Dialog = context?.let { Dialog(it) }!!
        dialogDiscard.setContentView(R.layout.alert_dialog_discard_recipe)
        dialogDiscard.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogDiscard.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogYesBtn = dialogDiscard.findViewById<TextView>(R.id.tvDialogYesBtn)
        val tvDialogNoBtn = dialogDiscard.findViewById<TextView>(R.id.tvDialogNoBtn)
        dialogDiscard.show()
        dialogDiscard.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogYesBtn.setOnClickListener {
            dialogDiscard.dismiss()
            findNavController().navigateUp()
        }

        tvDialogNoBtn.setOnClickListener {
            dialogDiscard.dismiss()
        }
    }

    private fun addRecipeSuccessDialog() {
        val dialogSuccess: Dialog = context?.let { Dialog(it) }!!
        dialogSuccess.setContentView(R.layout.alert_dialog_add_recipe_success)
        dialogSuccess.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogSuccess.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogSuccess.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogSuccess.show()
        dialogSuccess.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            dialogSuccess.dismiss()
            findNavController().navigate(R.id.planFragment)
        }
    }

    override fun uploadImage(imageView: ImageView?, pos: Int, cross: ImageView?, root: RelativeLayout) {
        // Save references for later use
        this.showIngredientImage = imageView
        this.showCrossIngImage = cross
        this.position = pos
        this.root = root

        ImagePicker.with(requireActivity())
            .crop() // Crop image (Optional)
            .compress(1024 * 5) // Compress the image to less than 5 MB
            .maxResultSize(250, 250) // Set max resolution
            .createIntent { intent -> pickImageLauncher1.launch(intent) }
    }

    private val pickImageLauncher1: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                file = MediaUtility.getPath(requireContext(), uri)?.let { File(it) }

                ingredientList!![position!!].uri = UriToBase64(requireActivity(), uri)
                ingredientList!![position!!].status=true
                showCrossIngImage?.visibility = View.VISIBLE
                showIngredientImage?.setImageURI(uri)
                ingredientLists?.add(file!!.absolutePath)
            }
        }
    }

    override fun crossImage(cross: ImageView?, imageView: ImageView?, textView: TextView?, pos: Int) {
        this.position = pos
        showCrossIngImage?.visibility = View.GONE
    }

    override fun cookIngName(description: String?, pos: Int, cross: ImageView?) {
        // Save references for later use
        this.cookDescription = description
        this.showCrossIngImage = cross
        this.position = pos

    }

    override fun crossCookIngName(cross: ImageView?, textView: TextView?, pos: Int) {

        showCrossIngImage?.visibility = View.VISIBLE
    }

}
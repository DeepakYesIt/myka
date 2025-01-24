package com.mykameal.planner.fragment.mainfragment.addrecipetab

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mykameal.planner.OnItemClickListener
import com.mykameal.planner.R
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.adapter.AdapterCookIngredientsItem
import com.mykameal.planner.adapter.AdapterCreateIngredientsItem
import com.mykameal.planner.commonworkutils.MediaUtility
import com.mykameal.planner.databinding.FragmentCreateRecipeBinding
import com.mykameal.planner.fragment.mainfragment.addrecipetab.createrecipefromimage.model.RecyclerViewItemModel
import java.io.File

class CreateRecipeFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentCreateRecipeBinding? = null
    private var quantity:Int=1
    private var file: File? = null
    private val ingredientList = mutableListOf<RecyclerViewItemModel>()
    private val cookList = mutableListOf<String>()
    private var adapter:AdapterCreateIngredientsItem?=null
    private var adapterCook: AdapterCookIngredientsItem?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = FragmentCreateRecipeBinding.inflate(layoutInflater, container, false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility=View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility=View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
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

                binding!!.addImageIcon.visibility=View.GONE
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(binding!!.addImages)

            }
        }
    }

    private fun initialize() {

        // Add the first blank EditText item
        ingredientList.add(RecyclerViewItemModel("","",false))

        // Set up RecyclerView and Adapter
        adapter = AdapterCreateIngredientsItem(ingredientList,requireActivity(),this)
        binding!!.rcyCreateIngredients.adapter = adapter

        // Handle "+" button click
        binding!!.imageCrtIngPlus.setOnClickListener {
            adapter!!.addNewItem()
            binding!!.rcyCreateIngredients.scrollToPosition(ingredientList.size - 1) // Scroll to the new item
        }

        // Add the first blank EditText item
        cookList.add("")

        // Set up RecyclerView and Adapter
        adapterCook = AdapterCookIngredientsItem(cookList,requireActivity(),this)
        binding!!.rcyCookInstructions.adapter = adapterCook

        // Handle "+" button click
        binding!!.imageCookIns.setOnClickListener {
            adapterCook!!.addNewItem()
            binding!!.rcyCookInstructions.scrollToPosition(cookList.size - 1) // Scroll to the new item
        }

        binding!!.spinnerCookBook.setItems(
            listOf("Christmas", "Favourite","Birthday", "Party", "Coriander"))

        binding!!.imgMinus.setOnClickListener{
            if (quantity > 1) {
                quantity--
                updateValue()
            }else{
                Toast.makeText(requireActivity(),"Minimum serving atleast value is one", Toast.LENGTH_LONG).show()
            }
        }

        binding!!.imgPlus.setOnClickListener{
            if (quantity < 99) {
                quantity++
                updateValue()
            }
        }

        binding!!.relBacks.setOnClickListener{
            addRecipeDiscardDialog()
        }

        binding!!.layBottom.setOnClickListener{
            addRecipeSuccessDialog()
        }

        binding!!.textPrivate.setOnClickListener{
            val drawableStart = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_uncheck_gray_icon)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPublic.setCompoundDrawables(drawableStart, null, null, null)

            val drawableStart1 = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_check_icon)
            drawableStart1!!.setBounds(0, 0, drawableStart1.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPrivate.setCompoundDrawables(drawableStart1, null, null, null)
        }

        binding!!.textPublic.setOnClickListener{
            val drawableStart = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_check_icon)
            drawableStart!!.setBounds(0, 0, drawableStart.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPublic.setCompoundDrawables(drawableStart, null, null, null)

            val drawableStart1 = ContextCompat.getDrawable(requireActivity(), R.drawable.radio_uncheck_gray_icon)
            drawableStart1!!.setBounds(0, 0, drawableStart1.intrinsicWidth, drawableStart.intrinsicHeight)
            binding!!.textPrivate.setCompoundDrawables(drawableStart1, null, null, null)

        }

        binding!!.addImages.setOnClickListener {
            ImagePicker.with(this)
                .crop() // Crop image (Optional)
                .compress(1024 * 5) // Compress the image to less than 5 MB
                .maxResultSize(250, 250) // Set max resolution
                .createIntent { intent -> pickImageLauncher.launch(intent) }
        }

    }

    private fun updateValue() {
        binding!!.textValue.text = String.format("%02d", quantity)
    }

    private fun addRecipeDiscardDialog() {
        val dialogDiscard: Dialog = context?.let { Dialog(it) }!!
        dialogDiscard.setContentView(R.layout.alert_dialog_discard_recipe)
        dialogDiscard.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogDiscard.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDialogYesBtn = dialogDiscard.findViewById<TextView>(R.id.tvDialogYesBtn)
        val tvDialogNoBtn = dialogDiscard.findViewById<TextView>(R.id.tvDialogNoBtn)
        dialogDiscard.show()
        dialogDiscard.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        tvDialogYesBtn.setOnClickListener {
            dialogDiscard.dismiss()
            findNavController().navigateUp()
        }

        tvDialogNoBtn.setOnClickListener{
            dialogDiscard.dismiss()
        }
    }

    private fun addRecipeSuccessDialog() {
        val dialogSuccess: Dialog = context?.let { Dialog(it) }!!
        dialogSuccess.setContentView(R.layout.alert_dialog_add_recipe_success)
        dialogSuccess.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogSuccess.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val rlOkayBtn = dialogSuccess.findViewById<RelativeLayout>(R.id.rlOkayBtn)
        dialogSuccess.show()
        dialogSuccess.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        rlOkayBtn.setOnClickListener {
            dialogSuccess.dismiss()
            findNavController().navigate(R.id.planFragment)
        }

    }

    override fun itemClick(position: Int?, status: String?, type: String?) {

    }

}
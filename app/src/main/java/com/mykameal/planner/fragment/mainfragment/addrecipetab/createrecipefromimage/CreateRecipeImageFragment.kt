package com.mykameal.planner.fragment.mainfragment.addrecipetab.createrecipefromimage

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.mykameal.planner.activity.MainActivity
import com.mykameal.planner.databinding.FragmentCreateRecipeImageBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CreateRecipeImageFragment : Fragment() {

    private var binding: FragmentCreateRecipeImageBinding? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isFlashOn = false
    private var capturedImageUri: android.net.Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCreateRecipeImageBinding.inflate(layoutInflater,container,false)

        (activity as MainActivity?)!!.binding!!.llIndicator.visibility = View.GONE
        (activity as MainActivity?)!!.binding!!.llBottomNavigation.visibility = View.GONE

        initliaze()
        return binding!!.root
    }

    private fun initliaze() {
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        clickListener()
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding!!.previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().setFlashMode(
                if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            ).build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun requestPermissions() {
        requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }


    private fun clickListener() {
        binding!!.galleryBtn.setOnClickListener {
            galleryIntent()
        }

        binding!!.camera.setOnClickListener {
            takePhoto()
        }

        binding!!.flashIcon.setOnClickListener {
            flashWorking()
        }

        binding?.backBtn!!.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Tick button
        binding?.okBtn!!.setOnClickListener {
            Toast.makeText(requireActivity(), "Image saved to $capturedImageUri", Toast.LENGTH_SHORT).show()
            restoreCameraView()
        }

        // Cancel button
        binding!!.noBtn.setOnClickListener {
            Toast.makeText(requireActivity(), "Preview canceled", Toast.LENGTH_SHORT).show()
            restoreCameraView()
        }
    }

    private fun showCapturedPreview() {
        binding!!.previewView.visibility = View.GONE
        binding!!.galleryBtn.visibility = View.GONE
        binding!!.camera.visibility = View.GONE
        binding!!.flashIcon.visibility = View.GONE
        binding!!.backBtn.visibility = View.GONE

        binding!!.ImageView.visibility = View.VISIBLE
        binding!!.ImageView.setImageURI(capturedImageUri)

        binding!!.okBtn.visibility = View.VISIBLE
        binding!!.noBtn.visibility = View.VISIBLE
    }

    private fun restoreCameraView() {
        binding!!.previewView.visibility = View.VISIBLE
        binding!!.galleryBtn.visibility = View.VISIBLE
        binding!!.camera.visibility = View.VISIBLE
        binding!!.flashIcon.visibility = View.VISIBLE
        binding!!.backBtn.visibility = View.VISIBLE

        binding!!.ImageView.visibility = View.GONE
        binding!!.okBtn.visibility = View.GONE
        binding!!.noBtn.visibility = View.GONE
        capturedImageUri = null
    }


    private fun flashWorking() {
        isFlashOn = !isFlashOn
        imageCapture?.flashMode = if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        /*Toast.makeText(this, "Flash ${if (isFlashOn) "On" else "Off"}", Toast.LENGTH_SHORT).show()*/
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MY-KAI-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(requireActivity().contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    capturedImageUri = output.savedUri
//                    Toast.makeText(baseContext, "Image captured successfully", Toast.LENGTH_SHORT).show()
                    showCapturedPreview()
                }
            }
        )
    }

    @SuppressLint("IntentReset")
    private fun galleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        startActivity(intent)
    }


    companion object {
        private const val TAG = "Camera"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PERMISSION_REQUEST_CODE = 100
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

}
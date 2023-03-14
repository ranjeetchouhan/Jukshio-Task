package com.ranjeet.jukshiotask.presentation.camera

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ranjeet.jukshiotask.R
import com.ranjeet.jukshiotask.databinding.ActivityCameraviewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraView : AppCompatActivity() {
    var binding: ActivityCameraviewBinding? = null
    private val viewModel: CameraViewModel by viewModels()

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    var capturedImagesMap = HashMap<Int, Boolean>()
    var photosUri = ArrayList<Uri>()
    var file: Uri? = null
    lateinit var uploadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraviewBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initialize()
        if (allPermissionsGranted()) {
            initializeCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initialize() {
        capturedImagesMap.put(1, false)
        capturedImagesMap.put(2, false)
        binding?.btCapture?.tag = "camera"
        binding?.btCapture?.setOnClickListener {
            if (it.getTag().equals("camera")) {
                takePhoto()
            } else {
                UploadPictures()
            }
        }

        viewModel.photosUrl.observe(this, {
            if (it.size == 1) {
                uploadingDialog.dismiss()
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Pictures uploaded successfully")
                    .setPositiveButton(
                        "Go to home",
                        DialogInterface.OnClickListener { dialog, id ->
                            cameraExecutor.shutdown()
                            finish()
                        },
                    )
                val dialog = builder.create()
                dialog.show()
            }
        })
    }

    private fun UploadPictures() {
        uploadingDialog = Dialog(this)
        uploadingDialog.setContentView(R.layout.view_uploading)
        uploadingDialog.show()
        viewModel.uploadPicture(photosUri)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg",
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this@CameraView),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    // set the saved uri to the image view
                    if (capturedImagesMap[1] == false) {
                        binding?.ivCaptureFirstPic?.setImageURI(savedUri)
                        binding?.ivCaptureFirstPic?.isVisible = true
                        capturedImagesMap[1] = true
                        photosUri.add(savedUri)
                        checkBothImagesCaptured()
                    } else if (capturedImagesMap[2] == false) {
                        binding?.ivCaptureSecondPic?.setImageURI(savedUri)
                        binding?.ivCaptureSecondPic?.isVisible = true
                        capturedImagesMap[2] = true
                        photosUri.add(savedUri)
                        checkBothImagesCaptured()
                    } else {
                        Toast.makeText(baseContext, "Maximum 2 pictures are allow to capture", Toast.LENGTH_LONG).show()
                    }

                    val msg = "Photo capture succeeded"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            },
        )
    }

    private fun initializeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            Runnable {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                var preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                } catch (e: Exception) {
                    Log.e("TAG", "Use case binding failed", e)
                }
            },
            ContextCompat.getMainExecutor(this),
        )
        binding?.tvLimitationMsg?.bringToFront()
    }

    internal fun checkBothImagesCaptured() {
        val all = capturedImagesMap[1] == true && capturedImagesMap[2] == true
        if (all) {
            binding?.btCapture?.tag = "done"
            binding?.btCapture?.setImageDrawable(getDrawable(R.drawable.round_done_24))
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initializeCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraView"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

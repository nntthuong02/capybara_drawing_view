package com.app.recordandplayvideo

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.recordandplayvideo.databinding.ActivityMainBinding
import com.app.recordandplayvideo.drawingview.LayoutMode

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null

    private lateinit var cameraExecutor: ExecutorService
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

            viewBinding.view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Bỏ listener sau khi lấy được chiều cao
                    viewBinding.view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Lấy chiều cao thực tế của DrawingView
                    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                    val marginTop = (screenHeight * 0.6).toInt()
                    val marginHorizontal = (screenWidth / 3)

                    // Áp dụng marginTop
                    val params = viewBinding.view.layoutParams as ConstraintLayout.LayoutParams
                    params.setMargins(marginHorizontal, marginTop, marginHorizontal, 0)
                    viewBinding.view.layoutParams = params
                    viewBinding.view.requestLayout()
                }
            })
        viewBinding.btnCreate9.setOnClickListener {
            viewBinding.drawingView.setLayoutModeCanvas(LayoutMode.SPACED)
            viewBinding.drawingView.duplicateAndFlip()
            viewBinding.drawingView.duplicateBitmap()
        }

        viewBinding.btnCreateCircle.setOnClickListener {
//            val bitmap0 = viewBinding.drawingView.getDrawingBitmap()
//            val bitmap1 = viewBinding.drawingView.flipBitmapVertically(bitmap0!!)
            val bitmap = viewBinding.drawingView.saveCircularDrawingToBitmap()
            viewBinding.apply {
//                imageView.visibility = View.VISIBLE
                circle.visibility = View.VISIBLE
                drawingView.visibility = View.GONE
                view.visibility = View.GONE

                zoomBall.zoom(bitmap)
                zoomBall.visibility = View.VISIBLE
                zoomBall.startAutoZoom()
                // Đặt trực tiếp Bitmap vào ImageView
                imageView.setImageBitmap(bitmap)
            }
        }
        viewBinding.clearButton.setOnClickListener {
            viewBinding.drawingView.clear()
            viewBinding.drawingView.clearCanvas()
            viewBinding.apply {
                imageView.visibility = View.GONE
                circle.visibility = View.GONE
                drawingView.visibility = View.VISIBLE
                view.visibility = View.VISIBLE

                //
                viewBinding.zoomBall.setGlobalScale(0.1f)// Hoặc giá trị mặc định của bạn
                viewBinding.zoomBall.warp(viewBinding.zoomBall.width.toFloat(), viewBinding.zoomBall.height.toFloat())
            }
        }


        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            imageCapture = ImageCapture.Builder().build()



            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }



}
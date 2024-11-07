package com.app.recordandplayvideo

import android.Manifest
import android.content.Intent
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
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
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

        viewBinding.view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
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
            viewBinding.drawingView.duplicateBitmap()
        }
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        Log.d("screenWidth", screenWidth.toString())
        Log.d("screenHeight", screenWidth.toString())

        val view: View = findViewById(R.id.view)
        Log.d("viewBinding.circle.marginStart", viewBinding.circle.marginStart.toString())
        var widtdEdit = 0.0
        val layoutParams = viewBinding.circle.layoutParams as ViewGroup.MarginLayoutParams
        val marginStart = layoutParams.marginStart
        Log.d("marginHorizontal", marginStart.toString())
        val widthCircle = screenWidth - marginStart * 2
        viewBinding.btnCreateCircle.setOnClickListener {
            val bitmap = viewBinding.drawingView.saveCircularDrawingToBitmap()
            viewBinding.apply {
//                imageView.visibility = View.VISIBLE
                circle.visibility = View.VISIBLE
                drawingView.visibility = View.GONE
                view.visibility = View.GONE
                Log.d("CircleSize", "Width: $widthCircle, Height: $widthCircle")

                widtdEdit = widthCircle * (400.0 / 546.0)
                Log.d("ti le ve", (400.0 / 546.0).toString())
                Log.d("ti le mh", (screenWidth.toDouble() / screenHeight.toDouble()).toString())
                Log.d("widtdEdit", widtdEdit.toString())
                zoomBall.setInitDimens(widtdEdit.toInt())
                Log.d("width test", (widthCircle).toString())
                zoomBall.zoom(bitmap)
                zoomBall.visibility = View.VISIBLE
                zoomBall.startAutoZoom()

            }
        }
        viewBinding.clearButton.setOnClickListener {

            viewBinding.apply {
                drawingView.clear()
                drawingView.clearCanvas()
                imageView.visibility = View.GONE
                circle.visibility = View.GONE
                drawingView.visibility = View.VISIBLE
                view.visibility = View.VISIBLE
                Log.d("widtdEdit2", widtdEdit.toString())

                //
                viewBinding.zoomBall.reset()
                viewBinding.zoomBall.warp()

            }
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
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
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                    )
                )
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
                    this, cameraSelector, preview, imageCapture, videoCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

}
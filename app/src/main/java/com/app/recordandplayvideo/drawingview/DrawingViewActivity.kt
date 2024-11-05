package com.app.recordandplayvideo.drawingview
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.res.Resources
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.recordandplayvideo.databinding.ActivityDrawingViewBinding

class DrawingViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrawingViewBinding
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        // Kiểm tra quyền
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
        }
        val drawingView = binding.drawingView // Thay đổi ở đây
        val clearButton = binding.clearButton
        clearButton.setOnClickListener {
            drawingView.clear() // Phương thức này cần được tạo trong lớp DrawingView
        }

        binding.view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Bỏ listener sau khi lấy được chiều cao
                binding.view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Lấy chiều cao thực tế của DrawingView
//                val drawingViewHeight = drawingView.height
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val marginTop = (screenHeight * 0.6).toInt()
                val marginHorizontal = (screenWidth / 3)
//                val marginBottom = drawingView.height - marginHorizontal

                // Áp dụng marginTop
                val params = binding.view.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(marginHorizontal, marginTop, marginHorizontal, 0)
                binding.view.layoutParams = params
                binding.view.requestLayout()
            }
        })

//        val saveButton: Button = findViewById(R.id.save_button) // Kiểm tra ID này
        binding.saveButton.setOnClickListener {
//            drawingView.saveDrawingToBitmap(this)
//            val imageUri = drawingView.saveCircularDrawingToBitmap( this)
//            if (imageUri != null) {
//                val intent = Intent(this, DisplayImageActivity::class.java)
//                intent.putExtra("imageUri", imageUri.toString()) // Chuyển URI dưới dạng String
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
//            }
            val imageUri = drawingView.saveCenteredSquareBitmap(this)
            if (imageUri != null) {
                val intent = Intent(this, DisplayImageActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString()) // Chuyển URI dưới dạng String
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }

        }

        binding.createButton.setOnClickListener {
            drawingView.setLayoutModeCanvas(LayoutMode.SPACED)
            drawingView.duplicateAndFlip()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp
            } else {
                // Quyền bị từ chối
            }
        }
    }
}

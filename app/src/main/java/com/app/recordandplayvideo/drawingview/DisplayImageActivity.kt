package com.app.recordandplayvideo.drawingview

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.app.recordandplayvideo.R

class DisplayImageActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val imageView: ImageView = findViewById(R.id.imageView)

        // Lấy URI từ Intent
        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        if (imageUri != null) {
            imageView.setImageURI(imageUri) // Hiển thị ảnh từ URI
        } else {
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show()
        }
    }
}
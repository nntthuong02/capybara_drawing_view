package com.app.recordandplayvideo

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private var videoUri: Uri? = null  // Biến để lưu URI video

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }


        // Khôi phục URI từ savedInstanceState nếu có
        if (savedInstanceState != null) {
            videoUri = savedInstanceState.getParcelable("videoUri")
        } else {
            videoUri = Uri.parse(intent.getStringExtra("videoUri"))
        }

        playerView = findViewById(R.id.playerView)
        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer

        // Nếu videoUri không null, tạo MediaItem và phát
        videoUri?.let {
            val mediaItem = MediaItem.fromUri(it)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun onPause() {
        super.onPause()
        // Ngừng phát video khi Activity bị pause
        exoPlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        // Ngừng phát video khi Activity dừng
        exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Giải phóng tài nguyên ExoPlayer
        exoPlayer.release()
    }

    // Lưu trạng thái URI video khi Activity bị tạm dừng
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("videoUri", videoUri)
    }
}

package com.app.recordandplayvideo

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PlayVideoActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var videoUri: Uri
    private lateinit var videoDuration: TextView
    private lateinit var playButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        val videoRecyclerView: RecyclerView = findViewById(R.id.videoRecyclerView)
        videoRecyclerView.layoutManager = LinearLayoutManager(this)

        val videoList = getAllVideos()
        val adapter = VideoAdapter(videoList) { videoItem ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("videoUri", videoItem.uri.toString())
            startActivity(intent)
        }
        videoRecyclerView.adapter = adapter
    }




    // Hàm định dạng thời gian video
    private fun formatDuration(duration: Long): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(Date(duration))
    }

    private fun getAllVideos(): List<VideoItem> {
        val videoList = mutableListOf<VideoItem>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_TAKEN // Thêm trường thời gian quay
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
        val queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val cursor: Cursor? = contentResolver.query(queryUri, projection, null, null, sortOrder)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dateTakenColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN) // Lấy cột thời gian quay

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val duration = it.getLong(durationColumn)
                val dateTaken = it.getLong(dateTakenColumn)
                val videoUri = Uri.withAppendedPath(queryUri, id.toString())
                val formattedDuration = formatDuration(duration)
                val formattedDate = formatDate(dateTaken) // Định dạng thời gian quay
                videoList.add(VideoItem(videoUri, formattedDuration, formattedDate))
            }
        }
        return videoList
    }


    // Hàm định dạng thời điểm quay video




    private fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

}

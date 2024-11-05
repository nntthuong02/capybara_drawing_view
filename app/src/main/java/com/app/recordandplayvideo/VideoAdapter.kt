package com.app.recordandplayvideo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoAdapter(
    private val videoList: List<VideoItem>,
    private val itemClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        @SuppressLint("SetTextI18n")
        fun bind(videoItem: VideoItem) {
            durationTextView.text = "Duration: ${videoItem.formattedDuration}"
            dateTextView.text = "Date: ${videoItem.formattedDate}"

            itemView.setOnClickListener { itemClick(videoItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item_layout, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size
}


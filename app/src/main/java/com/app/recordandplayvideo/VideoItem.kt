package com.app.recordandplayvideo

import android.net.Uri

data class VideoItem(
    val uri: Uri,
    val formattedDuration: String,
    val formattedDate: String
)


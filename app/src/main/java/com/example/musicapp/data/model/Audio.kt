package com.example.musicapp.data.model

import android.net.Uri
data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val uri: Uri,
    val artworkUri: Uri?,
    val duration: Long
)
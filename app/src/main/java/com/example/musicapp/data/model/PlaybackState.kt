package com.example.musicapp.data.model

data class PlaybackState(
    val currentAudio: Audio? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
)
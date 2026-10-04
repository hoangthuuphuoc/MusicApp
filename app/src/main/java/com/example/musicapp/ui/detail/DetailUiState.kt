package com.example.musicapp.ui.detail

import com.example.musicapp.data.model.Audio

data class DetailUiState(
    val audio: Audio? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
)
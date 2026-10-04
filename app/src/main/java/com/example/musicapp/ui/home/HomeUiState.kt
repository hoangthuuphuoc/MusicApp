package com.example.musicapp.ui.home

import com.example.musicapp.data.model.Audio
import com.example.musicapp.data.model.PlaybackState

data class HomeUiState(
    val isLoading: Boolean = false,
    val audios: List<Audio> = emptyList(),
    val featuredAudios: List<Audio> = emptyList(),
    val playbackState: PlaybackState = PlaybackState(),
    val error: String? = null
)
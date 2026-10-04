package com.example.musicapp.ui.home

import com.example.musicapp.data.model.Audio
import com.example.musicapp.data.model.PlaybackState

sealed interface HomeUiEvent {

    data object LoadAudio :
        HomeUiEvent


    data class ClickAudio(
        val audio: Audio
    ) : HomeUiEvent


    data object ClickSearch :
        HomeUiEvent


    data object ClickPlayPause :
        HomeUiEvent

    data class PlaybackChanged(
        val playbackState: PlaybackState
    ) : HomeUiEvent

    data object RequestPlaybackState :
        HomeUiEvent
}
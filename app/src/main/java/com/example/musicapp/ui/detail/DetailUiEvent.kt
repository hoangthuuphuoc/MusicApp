package com.example.musicapp.ui.detail

import com.example.musicapp.data.model.PlaybackState

sealed interface DetailUiEvent {

    data class LoadAudio(
        val audioId: Long
    ) : DetailUiEvent


    data object ClickBack :
        DetailUiEvent


    data object ClickPlayPause :
        DetailUiEvent


    data object ClickNext :
        DetailUiEvent


    data object ClickPrevious :
        DetailUiEvent


    data class Seek(
        val position: Int
    ) : DetailUiEvent


    data class PlaybackChanged(
        val playbackState: PlaybackState
    ) : DetailUiEvent


    data object RequestPlaybackState :
        DetailUiEvent
}
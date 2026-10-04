package com.example.musicapp.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.repository.AudioRepository
import com.example.musicapp.data.repository.MusicPlaybackRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application
) : AndroidViewModel(
    application
) {

    private val audioRepository = AudioRepository(
        application
    )


    private val playbackRepository = MusicPlaybackRepository(
        application
    )


    private val _uiState = MutableStateFlow(
        DetailUiState()
    )


    val uiState = _uiState.asStateFlow()


    private val _uiEffect = Channel<DetailUiEffect>(
        Channel.BUFFERED
    )


    val uiEffect = _uiEffect.receiveAsFlow()


    fun onEvent(
        event: DetailUiEvent
    ) {

        when (event) {

            is DetailUiEvent.LoadAudio -> {

                loadAudio(
                    event.audioId
                )
            }


            DetailUiEvent.ClickBack -> {

                viewModelScope.launch {

                    _uiEffect.send(
                        DetailUiEffect.Back
                    )
                }
            }


            DetailUiEvent.ClickPlayPause -> {

                playbackRepository.playPause()
            }


            DetailUiEvent.ClickNext -> {

                playbackRepository.next()
            }


            DetailUiEvent.ClickPrevious -> {

                playbackRepository.previous()
            }


            is DetailUiEvent.Seek -> {

                playbackRepository.seek(
                    event.position
                )
            }

            is DetailUiEvent.PlaybackChanged -> {

                val playback = event.playbackState


                _uiState.value = _uiState.value.copy(

                    audio = playback.currentAudio ?: _uiState.value.audio,

                    isPlaying = playback.isPlaying,

                    currentPosition = playback.currentPosition,

                    duration = if (playback.duration > 0) {

                        playback.duration

                    } else {

                        _uiState.value.duration
                    }
                )
            }


            DetailUiEvent.RequestPlaybackState -> {

                playbackRepository.requestState()
            }
        }
    }


    private fun loadAudio(
        audioId: Long
    ) {

        viewModelScope.launch {

            val audio = audioRepository.getAudioById(
                    audioId
                ) ?: return@launch


            _uiState.value = _uiState.value.copy(
                audio = audio,

                duration = audio.duration.toInt()
            )
        }
    }
}
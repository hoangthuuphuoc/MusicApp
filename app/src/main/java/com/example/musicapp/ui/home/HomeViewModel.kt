package com.example.musicapp.ui.home

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

class HomeViewModel(
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
        HomeUiState()
    )


    val uiState = _uiState.asStateFlow()


    private val _uiEffect = Channel<HomeUiEffect>(
        Channel.BUFFERED
    )


    val uiEffect = _uiEffect.receiveAsFlow()


    fun onEvent(
        event: HomeUiEvent
    ) {

        when (event) {

            HomeUiEvent.LoadAudio -> {

                loadAudio()
            }

            is HomeUiEvent.ClickAudio -> {

                playbackRepository.play(
                    event.audio.id
                )
                viewModelScope.launch {
                    _uiEffect.send(
                        HomeUiEffect.OpenDetail(
                            event.audio.id
                        )
                    )
                }
            }


            HomeUiEvent.ClickSearch -> {

                viewModelScope.launch {

                    _uiEffect.send(
                        HomeUiEffect.OpenSearch
                    )
                }
            }


            HomeUiEvent.ClickPlayPause -> {

                playbackRepository.playPause()
            }

            is HomeUiEvent.PlaybackChanged -> {

                _uiState.value = _uiState.value.copy(
                    playbackState = event.playbackState
                )
            }


            HomeUiEvent.RequestPlaybackState -> {

                playbackRepository.requestState()
            }
        }
    }



    private fun loadAudio() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true, error = null
            )


            runCatching {

                audioRepository.getAudios()

            }.onSuccess { audios ->


                _uiState.value = _uiState.value.copy(
                    isLoading = false,

                    audios = audios,

                    featuredAudios = audios.take(
                        10
                    ),

                    error = null
                )

            }.onFailure { throwable ->


                _uiState.value = _uiState.value.copy(
                    isLoading = false,

                    error = throwable.message
                )
            }
        }
    }
}
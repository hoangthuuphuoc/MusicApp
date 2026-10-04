package com.example.musicapp.ui.search

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

class SearchViewModel(
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
        SearchUiState()
    )

    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<SearchUiEffect>(
        Channel.BUFFERED
    )

    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(
        event: SearchUiEvent
    ) {

        when (event) {

            SearchUiEvent.LoadAudio -> {

                loadAudio()
            }

            is SearchUiEvent.Search -> {

                search(
                    event.query
                )
            }

            is SearchUiEvent.ClickAudio -> {

                playbackRepository.play(
                    event.audio.id
                )

                viewModelScope.launch {

                    _uiEffect.send(
                        SearchUiEffect.OpenDetail(
                            event.audio.id
                        )
                    )
                }
            }

            SearchUiEvent.ClearSearch -> {

                _uiState.value = _uiState.value.copy(
                        query = "", resultAudios = _uiState.value.allAudios
                    )
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
                        isLoading = false, allAudios = audios, resultAudios = audios, error = null
                    )

            }.onFailure { throwable ->

                _uiState.value = _uiState.value.copy(
                        isLoading = false, error = throwable.message
                    )
            }
        }
    }

    private fun search(
        query: String
    ) {

        val allAudios = _uiState.value.allAudios

        val resultAudios = if (query.isBlank()) {

            allAudios

        } else {

            allAudios.filter { audio ->

                audio.title.contains(
                    query, ignoreCase = true
                ) || audio.artist.contains(
                    query, ignoreCase = true
                )
            }
        }

        _uiState.value = _uiState.value.copy(
                query = query, resultAudios = resultAudios
            )
    }
}
package com.example.musicapp.ui.search

import com.example.musicapp.data.model.Audio

sealed interface SearchUiEvent {

    data object LoadAudio :
        SearchUiEvent

    data class Search(
        val query: String
    ) : SearchUiEvent

    data class ClickAudio(
        val audio: Audio
    ) : SearchUiEvent

    data object ClearSearch :
        SearchUiEvent
}
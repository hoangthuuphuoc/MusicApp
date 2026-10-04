package com.example.musicapp.ui.search

import com.example.musicapp.data.model.Audio

data class SearchUiState(
    val query: String = "",
    val allAudios: List<Audio> = emptyList(),
    val resultAudios: List<Audio> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
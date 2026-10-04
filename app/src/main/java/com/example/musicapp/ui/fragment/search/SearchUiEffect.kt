package com.example.musicapp.ui.search

sealed interface SearchUiEffect {

    data class OpenDetail(
        val audioId: Long
    ) : SearchUiEffect
}
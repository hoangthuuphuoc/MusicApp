package com.example.musicapp.ui.home

sealed interface HomeUiEffect {

    data class OpenDetail(
        val audioId: Long
    ) : HomeUiEffect

    data object OpenSearch :
        HomeUiEffect
}
package com.example.musicapp.ui.detail

sealed interface DetailUiEffect {

    data object Back :
        DetailUiEffect
}
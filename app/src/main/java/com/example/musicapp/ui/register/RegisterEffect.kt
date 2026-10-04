package com.example.musicapp.ui.register

sealed interface RegisterUiEffect {

    data object OpenHome :
        RegisterUiEffect


    data object OpenLogin :
        RegisterUiEffect


    data object Back :
        RegisterUiEffect


    data class ShowMessage(
        val message: String
    ) : RegisterUiEffect
}
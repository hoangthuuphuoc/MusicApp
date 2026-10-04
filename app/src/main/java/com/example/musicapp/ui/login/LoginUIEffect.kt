package com.example.musicapp.ui.auth.login

sealed interface LoginUiEffect {

    data object OpenHome :
        LoginUiEffect

    data object OpenRegister :
        LoginUiEffect

    data object Back :
        LoginUiEffect

    data class ShowMessage(
        val message: String
    ) : LoginUiEffect
}
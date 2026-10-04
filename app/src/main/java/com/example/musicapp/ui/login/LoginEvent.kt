package com.example.musicapp.ui.auth.login

sealed interface LoginUiEvent {

    data class EmailChanged(
        val email: String
    ) : LoginUiEvent

    data class PasswordChanged(
        val password: String
    ) : LoginUiEvent

    data object ClickLogin :
        LoginUiEvent

    data object ClickRegister :
        LoginUiEvent

    data object ClickBack :
        LoginUiEvent
}
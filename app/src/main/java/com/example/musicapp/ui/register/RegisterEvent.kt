package com.example.musicapp.ui.register

sealed interface RegisterUiEvent {

    data class FullNameChanged(
        val fullName: String
    ) : RegisterUiEvent


    data class EmailChanged(
        val email: String
    ) : RegisterUiEvent


    data class PasswordChanged(
        val password: String
    ) : RegisterUiEvent


    data object ClickCreateAccount :
        RegisterUiEvent


    data object ClickSignIn :
        RegisterUiEvent


    data object ClickBack :
        RegisterUiEvent
}
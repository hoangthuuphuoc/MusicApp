package com.example.musicapp.ui.auth.login

data class LoginUiState(

    val email: String = "",

    val password: String = "",

    val isLoading: Boolean = false,

    val emailError: String? = null,

    val passwordError: String? = null,

    val error: String? = null
)
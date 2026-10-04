package com.example.musicapp.ui.register

data class RegisterUiState(

    val fullName: String = "",

    val email: String = "",

    val password: String = "",

    val isLoading: Boolean = false,

    val fullNameError: String? = null,

    val emailError: String? = null,

    val passwordError: String? = null,

    val error: String? = null
)
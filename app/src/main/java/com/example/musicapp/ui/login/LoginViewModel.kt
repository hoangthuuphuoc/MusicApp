package com.example.musicapp.ui.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(
        LoginUiState()
    )

    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<LoginUiEffect>(
        Channel.BUFFERED
    )

    val uiEffect = _uiEffect.receiveAsFlow()


    fun onEvent(
        event: LoginUiEvent
    ) {

        when (event) {

            is LoginUiEvent.EmailChanged -> {

                _uiState.value = _uiState.value.copy(
                    email = event.email, emailError = null, error = null
                )
            }


            is LoginUiEvent.PasswordChanged -> {

                _uiState.value = _uiState.value.copy(
                    password = event.password, passwordError = null, error = null
                )
            }


            LoginUiEvent.ClickLogin -> {

                login()
            }


            LoginUiEvent.ClickRegister -> {

                viewModelScope.launch {

                    _uiEffect.send(
                        LoginUiEffect.OpenRegister
                    )
                }
            }


            LoginUiEvent.ClickBack -> {

                viewModelScope.launch {

                    _uiEffect.send(
                        LoginUiEffect.Back
                    )
                }
            }
        }
    }


    private fun login() {


        if (_uiState.value.isLoading) {
            return
        }


        viewModelScope.launch {


            _uiState.value = _uiState.value.copy(
                isLoading = true, emailError = null, passwordError = null, error = null
            )


            val isValid = validateLogin()



            if (!isValid) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )

                return@launch
            }


            val state = _uiState.value


            val result = authRepository.login(
                email = state.email.trim(), password = state.password
            )



            if (result.isSuccess) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = null
                )


                _uiEffect.send(
                    LoginUiEffect.OpenHome
                )

                return@launch
            }


            val exception = result.exceptionOrNull()


            handleLoginError(
                exception
            )
        }
    }


    private fun validateLogin(): Boolean {

        val state = _uiState.value


        val email = state.email.trim()


        val password = state.password


        val emailError = when {

            email.isBlank() -> {

                "Vui lòng nhập email"
            }


            email.contains(" ") -> {

                "Email không được chứa khoảng trắng"
            }


            email.length > 254 -> {

                "Email quá dài"
            }


            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {

                "Email không đúng định dạng"
            }


            else -> {

                null
            }
        }

        val passwordError = when {

            password.isBlank() -> {

                "Vui lòng nhập mật khẩu"
            }

            password.length < 6 -> {

                "Mật khẩu phải có ít nhất 6 ký tự"
            }


            password.length > 128 -> {

                "Mật khẩu quá dài"
            }


            else -> {

                null
            }
        }



        _uiState.value = _uiState.value.copy(
            emailError = emailError, passwordError = passwordError
        )


        return emailError == null && passwordError == null
    }


    private suspend fun handleLoginError(
        exception: Throwable?
    ) {

        val message = when (exception) {

            is FirebaseAuthInvalidCredentialsException -> {

                "Email hoặc mật khẩu không chính xác"
            }


            is FirebaseAuthInvalidUserException -> {

                when (exception.errorCode) {


                    "ERROR_USER_DISABLED" -> {

                        "Tài khoản đã bị vô hiệu hóa"
                    }


                    "ERROR_USER_NOT_FOUND" -> {

                        "Tài khoản không tồn tại"
                    }

                    else -> {

                        "Tài khoản không hợp lệ"
                    }
                }
            }


            is FirebaseNetworkException -> {

                "Không có kết nối Internet"
            }


            is FirebaseTooManyRequestsException -> {

                "Bạn đăng nhập quá nhiều lần. Vui lòng thử lại sau"
            }


            is FirebaseAuthException -> {

                when (exception.errorCode) {

                    "ERROR_OPERATION_NOT_ALLOWED" -> {

                        "Phương thức đăng nhập này chưa được bật"
                    }


                    "ERROR_USER_TOKEN_EXPIRED" -> {

                        "Phiên đăng nhập đã hết hạn"
                    }


                    "ERROR_INVALID_EMAIL" -> {

                        "Email không hợp lệ"
                    }


                    "ERROR_WRONG_PASSWORD" -> {

                        "Mật khẩu không chính xác"
                    }


                    "ERROR_INVALID_CREDENTIAL" -> {

                        "Email hoặc mật khẩu không chính xác"
                    }


                    else -> {

                        "Đăng nhập thất bại"
                    }
                }
            }

            null -> {

                "Đăng nhập thất bại"
            }

            else -> {

                exception.message ?: "Đã xảy ra lỗi. Vui lòng thử lại"
            }
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false, error = message
        )

        _uiEffect.send(
            LoginUiEffect.ShowMessage(
                message
            )
        )
    }
}
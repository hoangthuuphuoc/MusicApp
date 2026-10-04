package com.example.musicapp.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val authRepository =
        AuthRepository()


    private val _uiState =
        MutableStateFlow(
            RegisterUiState()
        )

    val uiState =
        _uiState.asStateFlow()



    private val _uiEffect =
        Channel<RegisterUiEffect>(
            Channel.BUFFERED
        )

    val uiEffect =
        _uiEffect.receiveAsFlow()




    fun onEvent(
        event: RegisterUiEvent
    ) {

        when (event) {


            is RegisterUiEvent.FullNameChanged -> {

                _uiState.value =
                    _uiState.value.copy(
                        fullName = event.fullName,
                        fullNameError = null,
                        error = null
                    )
            }


            is RegisterUiEvent.EmailChanged -> {

                _uiState.value =
                    _uiState.value.copy(
                        email = event.email,
                        emailError = null,
                        error = null
                    )
            }



            is RegisterUiEvent.PasswordChanged -> {

                _uiState.value =
                    _uiState.value.copy(
                        password = event.password,
                        passwordError = null,
                        error = null
                    )
            }



            RegisterUiEvent.ClickCreateAccount -> {

                register()
            }



            RegisterUiEvent.ClickSignIn -> {

                viewModelScope.launch {

                    _uiEffect.send(
                        RegisterUiEffect.OpenLogin
                    )
                }
            }



            RegisterUiEvent.ClickBack -> {

                viewModelScope.launch {

                    _uiEffect.send(
                        RegisterUiEffect.Back
                    )
                }
            }
        }
    }


    private fun register() {


        if (_uiState.value.isLoading) {
            return
        }


        viewModelScope.launch {



            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    fullNameError = null,
                    emailError = null,
                    passwordError = null,
                    error = null
                )



            val isValid =
                validateRegister()



            if (!isValid) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false
                    )

                return@launch
            }


            val state =
                _uiState.value




            val result =
                authRepository.register(

                    fullName =
                        state.fullName.trim(),

                    email =
                        state.email.trim(),

                    password =
                        state.password
                )




            if (result.isSuccess) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )


                _uiEffect.send(
                    RegisterUiEffect.OpenHome
                )


                return@launch
            }



            val exception =
                result.exceptionOrNull()


            handleRegisterError(
                exception
            )
        }
    }



    private fun validateRegister(): Boolean {

        val state =
            _uiState.value


        val fullName =
            state.fullName.trim()


        val email =
            state.email.trim()



        val password =
            state.password



        val fullNameError =
            when {

                fullName.isBlank() -> {

                    "Vui lòng nhập họ và tên"
                }


                fullName.length < 2 -> {

                    "Họ và tên phải có ít nhất 2 ký tự"
                }


                fullName.length > 50 -> {

                    "Họ và tên không được vượt quá 50 ký tự"
                }



                fullName.all {
                    it.isDigit()
                } -> {

                    "Họ và tên không hợp lệ"
                }


                !fullName.all {
                    it.isLetter() ||
                            it.isWhitespace()
                } -> {

                    "Họ và tên không được chứa ký tự đặc biệt"
                }


                else -> {

                    null
                }
            }



        val emailError =
            when {

                email.isBlank() -> {

                    "Vui lòng nhập email"
                }


                email.contains(" ") -> {

                    "Email không được chứa khoảng trắng"
                }


                email.length > 254 -> {

                    "Email quá dài"
                }


                !Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches() -> {

                    "Email không đúng định dạng"
                }


                else -> {

                    null
                }
            }



        val passwordError =
            when {

                password.isBlank() -> {

                    "Vui lòng nhập mật khẩu"
                }


                password.length < 6 -> {

                    "Mật khẩu phải có ít nhất 6 ký tự"
                }


                password.length > 128 -> {

                    "Mật khẩu không được vượt quá 128 ký tự"
                }



                password.none {
                    it.isLetter()
                } -> {

                    "Mật khẩu phải có ít nhất 1 chữ cái"
                }


                password.none {
                    it.isDigit()
                } -> {

                    "Mật khẩu phải có ít nhất 1 chữ số"
                }



                password.any {
                    it.isWhitespace()
                } -> {

                    "Mật khẩu không được chứa khoảng trắng"
                }


                else -> {

                    null
                }
            }
        _uiState.value =
            _uiState.value.copy(

                fullNameError =
                    fullNameError,

                emailError =
                    emailError,

                passwordError =
                    passwordError
            )


        return fullNameError == null &&
                emailError == null &&
                passwordError == null
    }


    private suspend fun handleRegisterError(
        exception: Throwable?
    ) {

        val message =
            when (exception) {



                is FirebaseAuthUserCollisionException -> {

                    "Email này đã được đăng ký"
                }



                is FirebaseAuthWeakPasswordException -> {

                    "Mật khẩu quá yếu"
                }



                is FirebaseAuthInvalidCredentialsException -> {

                    "Email không hợp lệ"
                }



                is FirebaseNetworkException -> {

                    "Không có kết nối Internet"
                }


                is FirebaseTooManyRequestsException -> {

                    "Bạn đã thao tác quá nhiều lần. Vui lòng thử lại sau"
                }


                is FirebaseAuthException -> {

                    when (exception.errorCode) {

                        "ERROR_EMAIL_ALREADY_IN_USE" -> {

                            "Email này đã được đăng ký"
                        }


                        "ERROR_INVALID_EMAIL" -> {

                            "Email không hợp lệ"
                        }


                        "ERROR_WEAK_PASSWORD" -> {

                            "Mật khẩu quá yếu"
                        }


                        "ERROR_OPERATION_NOT_ALLOWED" -> {

                            "Chức năng đăng ký bằng email chưa được bật"
                        }


                        else -> {

                            "Đăng ký tài khoản thất bại"
                        }
                    }
                }


                null -> {

                    "Đăng ký tài khoản thất bại"
                }



                else -> {

                    exception.message
                        ?: "Đã xảy ra lỗi. Vui lòng thử lại"
                }
            }




        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                error = message
            )


        _uiEffect.send(
            RegisterUiEffect.ShowMessage(
                message
            )
        )
    }
}
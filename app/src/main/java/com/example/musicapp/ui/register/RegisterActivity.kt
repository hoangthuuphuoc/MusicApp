package com.example.musicapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapp.databinding.ActivityRegisterBinding
import com.example.musicapp.ui.fragment.onboarding.OnboardingActivity
import com.example.musicapp.ui.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }


    private val viewModel:
            RegisterViewModel by viewModels()


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContentView(
            binding.root
        )


        setupInsets()

        setupInput()

        setupClickListener()

        observeState()

        observeEffect()
    }

    private fun setupInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { view, insets ->


            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )


            val keyboard =
                insets.getInsets(
                    WindowInsetsCompat.Type.ime()
                )


            view.setPadding(

                systemBars.left,

                systemBars.top,

                systemBars.right,

                maxOf(
                    systemBars.bottom,
                    keyboard.bottom
                )
            )

            insets
        }
    }

    private fun setupInput() {

        binding.etFullName
            .doAfterTextChanged { text ->


                viewModel.onEvent(

                    RegisterUiEvent.FullNameChanged(
                        text.toString()
                    )
                )
            }


        binding.etEmail
            .doAfterTextChanged { text ->


                viewModel.onEvent(

                    RegisterUiEvent.EmailChanged(
                        text.toString()
                    )
                )
            }


        binding.etPassword
            .doAfterTextChanged { text ->


                viewModel.onEvent(

                    RegisterUiEvent.PasswordChanged(
                        text.toString()
                    )
                )
            }
    }



    private fun setupClickListener() {


        binding.btnCreateAccount
            .setOnClickListener {


                viewModel.onEvent(
                    RegisterUiEvent.ClickCreateAccount
                )
            }


        binding.tvSignIn
            .setOnClickListener {


                viewModel.onEvent(
                    RegisterUiEvent.ClickSignIn
                )
            }



        binding.ibBack
            .setOnClickListener {


                viewModel.onEvent(
                    RegisterUiEvent.ClickBack
                )
            }
    }



    private fun observeState() {


        lifecycleScope.launch {


            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {


                viewModel.uiState.collect { state ->


                    binding.etFullName.error =
                        state.fullNameError


                    binding.etEmail.error =
                        state.emailError


                    binding.etPassword.error =
                        state.passwordError




                    binding.btnCreateAccount.isEnabled =
                        !state.isLoading


                    binding.etFullName.isEnabled =
                        !state.isLoading


                    binding.etEmail.isEnabled =
                        !state.isLoading


                    binding.etPassword.isEnabled =
                        !state.isLoading


                    binding.btnCreateAccount.text =
                        if (state.isLoading) {

                            "Đang tạo tài khoản..."

                        } else {

                            "Create Account"
                        }
                }
            }
        }
    }


    private fun observeEffect() {


        lifecycleScope.launch {


            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {


                viewModel.uiEffect.collect { effect ->


                    when (effect) {



                        RegisterUiEffect.OpenHome -> {


                            val intent =
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )


                            startActivity(
                                intent
                            )


                            finishAffinity()
                        }


                        RegisterUiEffect.OpenLogin -> {


                            val intent =
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )


                            startActivity(
                                intent
                            )


                            finish()
                        }



                        RegisterUiEffect.Back -> {


                            finish()
                        }
                        is RegisterUiEffect.ShowMessage -> {


                            Toast.makeText(
                                this@RegisterActivity,
                                effect.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
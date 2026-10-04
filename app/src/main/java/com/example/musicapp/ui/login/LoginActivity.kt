package com.example.musicapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapp.MainActivity
import com.example.musicapp.databinding.ActivityLoginBinding
import com.example.musicapp.ui.auth.login.LoginUiEffect
import com.example.musicapp.ui.auth.login.LoginUiEvent
import com.example.musicapp.ui.auth.login.LoginViewModel
import com.example.musicapp.ui.fragment.onboarding.OnboardingActivity
import com.example.musicapp.ui.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

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

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val keyboard = insets.getInsets(
                WindowInsetsCompat.Type.ime()
            )

            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right, maxOf(
                    systemBars.bottom, keyboard.bottom
                )
            )

            insets
        }
    }


    private fun setupInput() {

        binding.etEmail.doAfterTextChanged { text ->

            viewModel.onEvent(
                LoginUiEvent.EmailChanged(
                    text.toString()
                )
            )
        }


        binding.etPassword.doAfterTextChanged { text ->

            viewModel.onEvent(
                LoginUiEvent.PasswordChanged(
                    text.toString()
                )
            )
        }
    }


    private fun setupClickListener() {

        binding.btnSignIn.setOnClickListener {

            viewModel.onEvent(
                LoginUiEvent.ClickLogin
            )
        }


        binding.tvRegisterNow.setOnClickListener {

            viewModel.onEvent(
                LoginUiEvent.ClickRegister
            )
        }


        binding.ibBack.setOnClickListener {

            viewModel.onEvent(
                LoginUiEvent.ClickBack
            )
        }
    }


    private fun observeState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->
                    binding.etEmail.error = state.emailError
                    binding.etPassword.error = state.passwordError


                    binding.btnSignIn.isEnabled = !state.isLoading


                    binding.etEmail.isEnabled = !state.isLoading

                    binding.etPassword.isEnabled = !state.isLoading
                    binding.anAnimation.isVisible
                    if (state.isLoading) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    binding.btnSignIn.text = if (state.isLoading) {

                        "Đang kiểm tra..."

                    } else {

                        "Sign In"
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

                        LoginUiEffect.OpenHome -> {

                            val intent = Intent(
                                this@LoginActivity, MainActivity::class.java
                            )

                            startActivity(intent)

                            finish()
                        }


                        LoginUiEffect.OpenRegister -> {

                            val intent = Intent(
                                this@LoginActivity, RegisterActivity::class.java
                            )

                            startActivity(intent)
                        }

                        LoginUiEffect.Back -> {

                            finish()
                        }

                        is LoginUiEffect.ShowMessage -> {

                            Toast.makeText(
                                this@LoginActivity, effect.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
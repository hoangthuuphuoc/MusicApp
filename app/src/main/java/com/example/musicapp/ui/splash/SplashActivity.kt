package com.example.musicapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import com.example.musicapp.databinding.ActivitySplashBinding
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.musicapp.ui.getstarted.GetStartActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        delay()
        nextPage()
    }

    private fun delay() {
        viewModel.delay()
    }

    private fun nextPage() {
        lifecycleScope.launch {
            viewModel.loading.collect {
                if (it) {
                    val intent= Intent(this@SplashActivity, GetStartActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            }
        }
    }

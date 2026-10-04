package com.example.musicapp.ui.getstarted

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.databinding.ActivityGetstartedBinding
import com.example.musicapp.ui.fragment.onboarding.OnboardingActivity

class GetStartActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityGetstartedBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setClickListener()
    }

    private fun setClickListener() {
        binding.tvButton.setOnClickListener {
            val intent = Intent(this@GetStartActivity, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
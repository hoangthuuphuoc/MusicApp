package com.example.musicapp.ui.fragment.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityOnboardingBinding
import com.example.musicapp.ui.adpater.OnBoardingAdapter
import com.example.musicapp.ui.login.LoginActivity

class OnboardingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOnboardingBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: OnBoardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = OnBoardingAdapter(this)

        binding.vp2Fragment.adapter = adapter
        binding.vp2Fragment.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handleView(position)
            }
        })
        setClickListener()
    }

    private fun setClickListener() {
        binding.tvOnboardingNext.setOnClickListener {
            nextPage()
        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
            startActivity(intent)

        }
    }

    private fun handleView(positon: Int) {
        when (positon) {
            0 -> {
                binding.vDot1.setBackgroundResource(R.drawable.bg_dot_is_selected)
                binding.vDot2.setBackgroundResource(R.drawable.bg_dot_normal)
            }

            1 -> {
                binding.vDot1.setBackgroundResource(R.drawable.bg_dot_normal)
                binding.vDot2.setBackgroundResource(R.drawable.bg_dot_is_selected)
            }
        }
    }

    private fun nextPage() {

        val currentItem = binding.vp2Fragment.currentItem

        if (currentItem < adapter.itemCount - 1) {
            binding.vp2Fragment.setCurrentItem(currentItem + 1, true)
        } else {
            startActivity(
                Intent(
                    this, LoginActivity::class.java
                )
            )
            finish()
        }
    }

}
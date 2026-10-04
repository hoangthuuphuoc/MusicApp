package com.example.musicapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.ui.navigation.MainPagerAdapter

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var pagerAdapter: MainPagerAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        setContentView(
            binding.root
        )
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestPermissions()

        setupViewPager()

        setupBottomNavigation()
    }

    private fun setupViewPager() {

        pagerAdapter = MainPagerAdapter(
            this
        )

        binding.vp2Navigation.adapter = pagerAdapter

        binding.vp2Navigation.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(
                    position: Int
                ) {
                    super.onPageSelected(
                        position
                    )

                    binding.bottomNavigation.selectedItemId = when (position) {

                        0 -> R.id.it_home

                        1 -> R.id.it_search

                        else -> R.id.it_home
                    }
                }
            })
    }

    private fun setupBottomNavigation() {

        binding.bottomNavigation.setOnItemSelectedListener { item ->

                when (item.itemId) {

                    R.id.it_home -> {

                        binding.vp2Navigation.currentItem = 0

                        true
                    }

                    R.id.it_search -> {

                        binding.vp2Navigation.currentItem = 1

                        true
                    }

                    else -> false
                }
            }
    }

    fun openSearch() {

        binding.bottomNavigation.selectedItemId = R.id.it_search

        binding.vp2Navigation.setCurrentItem(
                1, true
            )
    }

    private fun requestPermissions() {

        val permissions = mutableListOf<String>()

        val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this, audioPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(
                audioPermission
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(
                permissions.toTypedArray()
            )
        }
    }
}

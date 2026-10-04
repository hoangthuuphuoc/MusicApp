package com.example.musicapp.ui.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapp.MainActivity
import com.example.musicapp.ui.home.HomeFragment
import com.example.musicapp.ui.search.SearchFragment

class MainPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(
    activity
) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(
        position: Int
    ): Fragment {

        return when (position) {

            0 ->
                HomeFragment()

            1 ->
                SearchFragment()

            else ->
                HomeFragment()
        }
    }
}

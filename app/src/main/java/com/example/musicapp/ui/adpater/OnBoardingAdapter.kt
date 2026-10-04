package com.example.musicapp.ui.adpater

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapp.ui.fragment.onboarding.OnboardingFragment

class OnBoardingAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.newInstance(position)
    }
}
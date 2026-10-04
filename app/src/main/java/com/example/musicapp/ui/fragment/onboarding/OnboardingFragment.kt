package com.example.musicapp.ui.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {
    private val binding by lazy {
        FragmentOnboardingBinding.inflate(layoutInflater)
    }

    companion object {
        private const val KEY_PAGE = "KEY_PAGE"
        fun newInstance(page: Int): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_PAGE, page)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextPage()
    }

    private fun nextPage() {
        val current = arguments?.getInt(KEY_PAGE)
        when (current) {
            0 -> {
                binding.ivImg.setImageResource(R.drawable.img_fragment)
                binding.tvTittle.text = "Discover Your Sound"
                binding.tvTittle1.text =
                    "Find curated releases, personalized charts,\n and underground tracks tailored precisely to \nyour mood."
            }

            1 -> {
                binding.ivImg.setImageResource(R.drawable.img_fragment2)
                binding.tvTittle.text = "Live & Connected"
                binding.tvTittle1.text =
                    "Join real-time broadcasts,chat securely with\nyour favorite hosts, and sync listen sessions \nwith friends."
            }
        }

    }
}
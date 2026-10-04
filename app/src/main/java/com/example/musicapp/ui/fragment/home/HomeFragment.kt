package com.example.musicapp.ui.home

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentHomeBinding
import com.example.musicapp.receiver.PlaybackStateReceiver
import com.example.musicapp.service.MusicPlaybackAction
import com.example.musicapp.ui.detail.DetailActivity
import com.example.musicapp.ui.home.adapter.HomeFeaturedAdapter
import com.example.musicapp.ui.home.adapter.HomeSongAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    private val binding
        get() = _binding!!


    private val viewModel: HomeViewModel by viewModels()


    private lateinit var songAdapter: HomeSongAdapter


    private lateinit var featuredAdapter: HomeFeaturedAdapter

    private lateinit var playbackReceiver: PlaybackStateReceiver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
            inflater, container, false
        )


        return binding.root
    }


    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view, savedInstanceState
        )
        setupBroadcastReceiver()


        setupRecyclerView()


        setupClickListener()


        observeState()


        observeEffect()



        viewModel.onEvent(
            HomeUiEvent.LoadAudio
        )
    }


    private fun setupBroadcastReceiver() {

        playbackReceiver = PlaybackStateReceiver { playbackState ->


            viewModel.onEvent(
                HomeUiEvent.PlaybackChanged(
                    playbackState
                )
            )
        }
    }


    override fun onStart() {

        super.onStart()


        val filter = IntentFilter(
            MusicPlaybackAction.PLAYBACK_STATE_CHANGED
        )


        ContextCompat.registerReceiver(
            requireContext(), playbackReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )

        viewModel.onEvent(
            HomeUiEvent.RequestPlaybackState
        )
    }

    override fun onStop() {

        requireContext().unregisterReceiver(
            playbackReceiver
        )


        super.onStop()
    }


    private fun setupRecyclerView() {

        featuredAdapter = HomeFeaturedAdapter { audio ->


            viewModel.onEvent(
                HomeUiEvent.ClickAudio(
                    audio
                )
            )
        }


        songAdapter = HomeSongAdapter { audio ->


            viewModel.onEvent(
                HomeUiEvent.ClickAudio(
                    audio
                )
            )
        }


        binding.rvAlbum.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )


        binding.rvAlbum.adapter = featuredAdapter


        binding.rvStream.layoutManager = LinearLayoutManager(
            requireContext()
        )


        binding.rvStream.adapter = songAdapter
    }


    private fun setupClickListener() {

        binding.edtSearch.setOnClickListener {


            viewModel.onEvent(
                HomeUiEvent.ClickSearch
            )
        }


        binding.ivPlay.setOnClickListener {


            viewModel.onEvent(
                HomeUiEvent.ClickPlayPause
            )
        }
    }


    private fun observeState() {

        viewLifecycleOwner.lifecycleScope.launch {


            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {


                viewModel.uiState.collect { state ->

                    featuredAdapter.submitList(
                        state.featuredAudios
                    )


                    songAdapter.submitList(
                        state.audios
                    )
                    renderPlayback(
                        state
                    )
                }
            }
        }
    }


    private fun renderPlayback(
        state: HomeUiState
    ) {

        val playbackState = state.playbackState


        val audio = playbackState.currentAudio ?: return


        binding.tvCurrentSong.text = audio.title


        binding.tvCurrentArtist.text = audio.artist


        binding.ivPlay.setImageResource(

            if (playbackState.isPlaying) {

                R.drawable.ic_play

            } else {

                R.drawable.ic_pause
            }
        )


        Glide.with(
            binding.ivCurrentSong
        )

            .load(
                audio.artworkUri
            )

            .placeholder(
                R.drawable.img_fragment
            )

            .error(
                R.drawable.img_fragment
            )

            .into(
                binding.ivCurrentSong
            )
    }


    private fun observeEffect() {

        viewLifecycleOwner.lifecycleScope.launch {


            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {


                viewModel.uiEffect.collect { effect ->


                    when (effect) {


                        is HomeUiEffect.OpenDetail -> {

                            val intent = Intent(
                                requireContext(), DetailActivity::class.java
                            ).apply {


                                putExtra(
                                    MusicPlaybackAction.EXTRA_AUDIO_ID, effect.audioId
                                )
                            }


                            startActivity(
                                intent
                            )
                        }


                        HomeUiEffect.OpenSearch -> {

                            (activity as? MainActivity)?.openSearch()
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {

        super.onDestroyView()


        _binding = null
    }
}
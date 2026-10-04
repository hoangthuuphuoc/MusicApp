package com.example.musicapp.ui.detail

import android.content.IntentFilter
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityPlayerBinding
import com.example.musicapp.receiver.PlaybackStateReceiver
import com.example.musicapp.service.MusicPlaybackAction
import com.example.musicapp.utils.TimeUtils
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {


    private val binding by lazy {

        ActivityPlayerBinding.inflate(
            layoutInflater
        )
    }


    private val viewModel: DetailViewModel by viewModels()


    private lateinit var playbackReceiver: PlaybackStateReceiver


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            binding.root
        )
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupBroadcastReceiver()


        getAudio()


        setupClickListener()


        observeState()


        observeEffect()
    }

    private fun setupBroadcastReceiver() {

        playbackReceiver = PlaybackStateReceiver { playbackState ->


            viewModel.onEvent(
                DetailUiEvent.PlaybackChanged(
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
            this, playbackReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )


        viewModel.onEvent(
            DetailUiEvent.RequestPlaybackState
        )
    }


    override fun onStop() {

        unregisterReceiver(
            playbackReceiver
        )


        super.onStop()
    }


    private fun getAudio() {

        val audioId = intent.getLongExtra(
            MusicPlaybackAction.EXTRA_AUDIO_ID, -1L
        )


        if (audioId == -1L) {

            finish()

            return
        }


        viewModel.onEvent(
            DetailUiEvent.LoadAudio(
                audioId
            )
        )
    }


    private fun setupClickListener() {

        binding.ivDetailBack.setOnClickListener {


            viewModel.onEvent(
                DetailUiEvent.ClickBack
            )
        }


        binding.ivDetailPlayPause.setOnClickListener {


            viewModel.onEvent(
                DetailUiEvent.ClickPlayPause
            )
        }


        binding.ivDetailNext.setOnClickListener {


            viewModel.onEvent(
                DetailUiEvent.ClickNext
            )
        }


        binding.ivDetailPrevious.setOnClickListener {


            viewModel.onEvent(
                DetailUiEvent.ClickPrevious
            )
        }


        binding.sbDetailProgress.setOnSeekBarChangeListener(

            object : SeekBar.OnSeekBarChangeListener {


                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                }


                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }


                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {

                    val position = seekBar?.progress ?: 0


                    viewModel.onEvent(
                        DetailUiEvent.Seek(
                            position
                        )
                    )
                }
            })

    }


    private fun observeState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->


                    renderState(
                        state
                    )
                }
            }
        }
    }


    private fun renderState(
        state: DetailUiState
    ) {

        val audio = state.audio ?: return


        binding.tvDetailSongTitle.text = audio.title


        binding.tvDetailArtist.text = audio.artist


        binding.tvDetailCurrentTime.text = TimeUtils.formatTime(
            state.currentPosition.toLong()
        )


        binding.tvDetailDuration.text = TimeUtils.formatTime(
            state.duration.toLong()
        )


        binding.sbDetailProgress.max = state.duration


        binding.sbDetailProgress.progress = state.currentPosition


        binding.ivDetailPlayPause.setImageResource(

            if (state.isPlaying) {

                R.drawable.ic_play

            } else {

                R.drawable.ic_pause
            }
        )


        Glide.with(
            binding.ivDetailCover
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
                binding.ivDetailCover
            )
    }


    private fun observeEffect() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiEffect.collect { effect ->


                    when (effect) {

                        DetailUiEffect.Back -> {

                            finish()
                        }
                    }
                }
            }
        }
    }
}
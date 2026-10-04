package com.example.musicapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.musicapp.data.model.Audio
import com.example.musicapp.data.model.PlaybackState
import com.example.musicapp.service.MusicPlaybackAction

class PlaybackStateReceiver(
    private val onStateChanged: (PlaybackState) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(
        context: Context?, intent: Intent?
    ) {

        if (intent?.action != MusicPlaybackAction.PLAYBACK_STATE_CHANGED) {
            return
        }

        val audioId = intent.getLongExtra(
            MusicPlaybackAction.EXTRA_AUDIO_ID, -1L
        )

        val title = intent.getStringExtra(
            MusicPlaybackAction.EXTRA_TITLE
        ) ?: "Unknown"

        val artist = intent.getStringExtra(
            MusicPlaybackAction.EXTRA_ARTIST
        ) ?: "Unknown Artist"

        val audioUriString = intent.getStringExtra(
            MusicPlaybackAction.EXTRA_AUDIO_URI
        )

        val artworkUriString = intent.getStringExtra(
            MusicPlaybackAction.EXTRA_ARTWORK_URI
        )

        val isPlaying = intent.getBooleanExtra(
            MusicPlaybackAction.EXTRA_IS_PLAYING, false
        )

        val currentPosition = intent.getIntExtra(
            MusicPlaybackAction.EXTRA_CURRENT_POSITION, 0
        )

        val duration = intent.getIntExtra(
            MusicPlaybackAction.EXTRA_DURATION, 0
        )

        val audio = if (audioId != -1L && audioUriString != null) {

            Audio(
                id = audioId, title = title, artist = artist, uri = Uri.parse(
                    audioUriString
                ), artworkUri = artworkUriString?.let {
                    Uri.parse(it)
                }, duration = duration.toLong()
            )
        } else {

            null
        }
        val playbackState = PlaybackState(
            currentAudio = audio,
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration
        )
        onStateChanged(
            playbackState
        )
    }
}
package com.example.musicapp.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.musicapp.service.MusicPlaybackAction
import com.example.musicapp.service.MusicPlaybackService

class MusicPlaybackRepository(
    context: Context
) {

    private val appContext = context.applicationContext


    fun play(
        audioId: Long
    ) {

        val intent = Intent(
            appContext, MusicPlaybackService::class.java
        ).apply {

            action = MusicPlaybackAction.PLAY

            putExtra(
                MusicPlaybackAction.EXTRA_AUDIO_ID, audioId
            )
        }

        ContextCompat.startForegroundService(
            appContext, intent
        )
    }


    fun playPause() {

        sendAction(
            MusicPlaybackAction.PLAY_PAUSE
        )
    }


    fun next() {

        sendAction(
            MusicPlaybackAction.NEXT
        )
    }


    fun previous() {

        sendAction(
            MusicPlaybackAction.PREVIOUS
        )
    }


    fun seek(
        position: Int
    ) {

        val intent = Intent(
            appContext, MusicPlaybackService::class.java
        ).apply {

            action = MusicPlaybackAction.SEEK

            putExtra(
                MusicPlaybackAction.EXTRA_POSITION, position
            )
        }

        appContext.startService(
            intent
        )
    }


    fun stop() {

        sendAction(
            MusicPlaybackAction.STOP
        )
    }


    fun requestState() {

        sendAction(
            MusicPlaybackAction.REQUEST_STATE
        )
    }


    private fun sendAction(
        actionValue: String
    ) {

        val intent = Intent(
            appContext, MusicPlaybackService::class.java
        ).apply {

            action = actionValue
        }

        appContext.startService(
            intent
        )
    }
}
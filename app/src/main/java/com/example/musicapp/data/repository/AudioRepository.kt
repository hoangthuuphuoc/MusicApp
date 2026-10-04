package com.example.musicapp.data.repository

import android.content.Context
import com.example.musicapp.data.local.AudioLocalDataSource
import com.example.musicapp.data.model.Audio

class AudioRepository(
    context: Context
) {

    private val localDataSource = AudioLocalDataSource(
        context
    )

    suspend fun getAudios(): List<Audio> {

        return localDataSource.getAudios()
    }

    suspend fun getAudioById(
        audioId: Long
    ): Audio? {

        return localDataSource.getAudios().firstOrNull { audio ->

                audio.id == audioId
            }
    }
}
package com.example.musicapp.utils

import java.util.Locale

object TimeUtils {

    fun formatTime(
        milliseconds: Long
    ): String {

        val totalSeconds = milliseconds / 1000

        val minutes = totalSeconds / 60

        val seconds = totalSeconds % 60

        return String.format(
            Locale.getDefault(), "%d:%02d", minutes, seconds
        )
    }
}
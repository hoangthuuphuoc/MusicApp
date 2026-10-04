package com.example.musicapp.service

object MusicPlaybackAction {

    const val PLAY = "play"
    const val PLAY_PAUSE = "play_pause"
    const val NEXT = "next"
    const val PREVIOUS = "previous"
    const val SEEK = "seek"
    const val STOP = "stop"

    const val REQUEST_STATE = "request_state"


    const val PLAYBACK_STATE_CHANGED =
        "PLAYBACK_STATE_CHANGED"


    const val EXTRA_AUDIO_ID = "audio_id"
    const val EXTRA_TITLE = "title"
    const val EXTRA_ARTIST = "artist"

    const val EXTRA_AUDIO_URI = "audio_uri"
    const val EXTRA_ARTWORK_URI = "artwork_uri"

    const val EXTRA_IS_PLAYING = "is_playing"

    const val EXTRA_CURRENT_POSITION = "current_position"

    const val EXTRA_DURATION = "duration"

    const val EXTRA_POSITION = "position"
}
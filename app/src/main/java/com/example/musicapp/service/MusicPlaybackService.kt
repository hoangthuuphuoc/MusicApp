package com.example.musicapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.musicapp.R
import com.example.musicapp.data.model.Audio
import com.example.musicapp.data.model.PlaybackState
import com.example.musicapp.data.repository.AudioRepository
import com.example.musicapp.ui.detail.DetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MusicPlaybackService : Service() {

    companion object {

        private const val CHANNEL_ID = "music_playback_channel"

        private const val NOTIFICATION_ID = 1001
    }


    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main
    )


    private val handler = Handler(
        Looper.getMainLooper()
    )

    private lateinit var audioRepository: AudioRepository


    private var mediaPlayer: MediaPlayer? = null


    private var audioList: List<Audio> = emptyList()


    private var currentIndex = -1


    private var playbackState = PlaybackState()


    private val progressRunnable = object : Runnable {

        override fun run() {

            val player = mediaPlayer ?: return

            if (player.isPlaying) {

                playbackState = playbackState.copy(
                    currentPosition = player.currentPosition,

                    duration = player.duration
                )

                broadcastPlaybackState()

                handler.postDelayed(
                    this, 500
                )
            }
        }
    }


    override fun onCreate() {

        super.onCreate()

        audioRepository = AudioRepository(
            applicationContext
        )

        createNotificationChannel()
    }


    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {

        when (intent?.action) {

            MusicPlaybackAction.PLAY -> {

                startForeground(
                    NOTIFICATION_ID, createNotification()
                )

                val audioId = intent.getLongExtra(
                    MusicPlaybackAction.EXTRA_AUDIO_ID, -1L
                )

                playById(
                    audioId
                )
            }


            MusicPlaybackAction.PLAY_PAUSE -> {

                playPause()
            }


            MusicPlaybackAction.NEXT -> {

                next()
            }


            MusicPlaybackAction.PREVIOUS -> {

                previous()
            }


            MusicPlaybackAction.SEEK -> {

                val position = intent.getIntExtra(
                    MusicPlaybackAction.EXTRA_POSITION, 0
                )

                seekTo(
                    position
                )
            }


            MusicPlaybackAction.REQUEST_STATE -> {

                broadcastPlaybackState()


                if (mediaPlayer == null) {

                    stopSelf(
                        startId
                    )
                }
            }


            MusicPlaybackAction.STOP -> {

                stopPlayback()
            }
        }

        return START_NOT_STICKY
    }


    private fun playById(
        audioId: Long
    ) {

        if (audioId == -1L) {
            return
        }

        serviceScope.launch {

            if (audioList.isEmpty()) {

                audioList = audioRepository.getAudios()
            }

            val index = audioList.indexOfFirst { audio ->

                audio.id == audioId
            }

            if (index == -1) {
                return@launch
            }

            currentIndex = index

            playCurrent()
        }
    }


    private fun playCurrent() {

        val audio = audioList.getOrNull(
            currentIndex
        ) ?: return


        handler.removeCallbacks(
            progressRunnable
        )


        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {

            setDataSource(
                this@MusicPlaybackService, audio.uri
            )


            setOnPreparedListener { player ->

                player.start()


                playbackState = PlaybackState(
                    currentAudio = audio,

                    isPlaying = true,

                    currentPosition = 0,

                    duration = player.duration
                )



                broadcastPlaybackState()


                startProgress()


                updateNotification()
            }


            setOnCompletionListener {

                next()
            }


            setOnErrorListener { _, _, _ ->

                next()

                true
            }


            prepareAsync()
        }


        playbackState = PlaybackState(
            currentAudio = audio,

            isPlaying = false,

            currentPosition = 0,

            duration = audio.duration.toInt()
        )


        broadcastPlaybackState()


        updateNotification()
    }


    private fun playPause() {

        val player = mediaPlayer ?: return


        if (player.isPlaying) {

            player.pause()


            handler.removeCallbacks(
                progressRunnable
            )


            playbackState = playbackState.copy(
                isPlaying = false,

                currentPosition = player.currentPosition
            )

        } else {

            player.start()


            playbackState = playbackState.copy(
                isPlaying = true
            )


            startProgress()
        }


        broadcastPlaybackState()


        updateNotification()
    }


    private fun next() {

        if (audioList.isEmpty()) {
            return
        }


        currentIndex = if (currentIndex < audioList.lastIndex) {

            currentIndex + 1

        } else {

            0
        }


        playCurrent()
    }


    private fun previous() {

        if (audioList.isEmpty()) {
            return
        }


        currentIndex = if (currentIndex > 0) {

            currentIndex - 1

        } else {

            audioList.lastIndex
        }


        playCurrent()
    }


    private fun seekTo(
        position: Int
    ) {

        val player = mediaPlayer ?: return


        val seekPosition = position.coerceIn(
            0, player.duration
        )


        player.seekTo(
            seekPosition
        )


        playbackState = playbackState.copy(
            currentPosition = seekPosition
        )


        broadcastPlaybackState()
    }


    private fun startProgress() {

        handler.removeCallbacks(
            progressRunnable
        )

        handler.post(
            progressRunnable
        )
    }


    private fun broadcastPlaybackState() {

        val audio = playbackState.currentAudio


        val intent = Intent(
            MusicPlaybackAction.PLAYBACK_STATE_CHANGED
        ).apply {

            setPackage(
                packageName
            )


            putExtra(
                MusicPlaybackAction.EXTRA_AUDIO_ID, audio?.id ?: -1L
            )


            putExtra(
                MusicPlaybackAction.EXTRA_TITLE, audio?.title
            )


            putExtra(
                MusicPlaybackAction.EXTRA_ARTIST, audio?.artist
            )


            putExtra(
                MusicPlaybackAction.EXTRA_AUDIO_URI, audio?.uri?.toString()
            )


            putExtra(
                MusicPlaybackAction.EXTRA_ARTWORK_URI, audio?.artworkUri?.toString()
            )


            putExtra(
                MusicPlaybackAction.EXTRA_IS_PLAYING, playbackState.isPlaying
            )


            putExtra(
                MusicPlaybackAction.EXTRA_CURRENT_POSITION, playbackState.currentPosition
            )


            putExtra(
                MusicPlaybackAction.EXTRA_DURATION, playbackState.duration
            )
        }


        sendBroadcast(
            intent
        )
    }


    private fun stopPlayback() {

        handler.removeCallbacks(
            progressRunnable
        )


        mediaPlayer?.release()


        mediaPlayer = null


        audioList = emptyList()


        currentIndex = -1


        playbackState = PlaybackState()


        broadcastPlaybackState()


        stopForeground(
            STOP_FOREGROUND_REMOVE
        )


        stopSelf()
    }


    private fun createNotification(): Notification {

        val state = playbackState


        val previousPendingIntent = createServicePendingIntent(
            MusicPlaybackAction.PREVIOUS, 1
        )


        val playPausePendingIntent = createServicePendingIntent(
            MusicPlaybackAction.PLAY_PAUSE, 2
        )


        val nextPendingIntent = createServicePendingIntent(
            MusicPlaybackAction.NEXT, 3
        )


        val builder = NotificationCompat.Builder(
            this, CHANNEL_ID
        )

            .setSmallIcon(
                R.mipmap.ic_launcher
            )

            .setContentTitle(
                state.currentAudio?.title ?: "Music App"
            )

            .setContentText(
                state.currentAudio?.artist ?: "Music"
            )

            .setOnlyAlertOnce(
                true
            )

            .setSilent(
                true
            )

            .setOngoing(
                state.isPlaying
            )

            .addAction(
                android.R.drawable.ic_media_previous,

                "Previous",

                previousPendingIntent
            )

            .addAction(

                if (state.isPlaying) {

                    android.R.drawable.ic_media_pause

                } else {

                    android.R.drawable.ic_media_play
                },

                if (state.isPlaying) {

                    "Pause"

                } else {

                    "Play"
                },

                playPausePendingIntent
            )

            .addAction(
                android.R.drawable.ic_media_next,

                "Next",

                nextPendingIntent
            )


        val audio = state.currentAudio


        if (audio != null) {

            val detailIntent = Intent(
                this, DetailActivity::class.java
            ).apply {

                putExtra(
                    MusicPlaybackAction.EXTRA_AUDIO_ID, audio.id
                )
            }


            val detailPendingIntent = PendingIntent.getActivity(
                this,
                10,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


            builder.setContentIntent(
                detailPendingIntent
            )
        }


        return builder.build()
    }


    private fun createServicePendingIntent(
        actionValue: String, requestCode: Int
    ): PendingIntent {

        val intent = Intent(
            this, MusicPlaybackService::class.java
        ).apply {

            action = actionValue
        }


        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun updateNotification() {

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )


        notificationManager.notify(
            NOTIFICATION_ID, createNotification()
        )
    }


    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_LOW
            )


            val notificationManager = getSystemService(
                NotificationManager::class.java
            )


            notificationManager.createNotificationChannel(
                channel
            )
        }
    }


    override fun onDestroy() {

        handler.removeCallbacks(
            progressRunnable
        )


        mediaPlayer?.release()


        mediaPlayer = null


        serviceScope.cancel()


        super.onDestroy()
    }


    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}
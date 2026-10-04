package com.example.musicapp.data.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.musicapp.data.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioLocalDataSource(
    private val context: Context
) {

    suspend fun getAudios(): List<Audio> = withContext(Dispatchers.IO) {

        val audios = mutableListOf<Audio>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " + "${MediaStore.Audio.Media.DURATION} > 0"

        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection, projection, selection, null, sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media._ID
            )

            val titleColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.TITLE
            )

            val artistColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ARTIST
            )

            val albumIdColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ALBUM_ID
            )

            val durationColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DURATION
            )

            val artworkBaseUri = Uri.parse(
                "content://media/external/audio/albumart"
            )

            while (cursor.moveToNext()) {

                val id = cursor.getLong(
                    idColumn
                )

                val title = cursor.getString(
                    titleColumn
                ) ?: "Unknown"

                val rawArtist = cursor.getString(
                    artistColumn
                )

                val artist = if (rawArtist.isNullOrBlank() || rawArtist == "<unknown>") {
                    "Unknown Artist"
                } else {
                    rawArtist
                }

                val albumId = cursor.getLong(
                    albumIdColumn
                )

                val duration = cursor.getLong(
                    durationColumn
                )

                val audioUri = ContentUris.withAppendedId(
                    collection, id
                )

                val artworkUri = if (albumId > 0) {

                    ContentUris.withAppendedId(
                        artworkBaseUri, albumId
                    )

                } else {

                    null
                }

                val audio = Audio(
                    id = id,
                    title = title,
                    artist = artist,
                    uri = audioUri,
                    artworkUri = artworkUri,
                    duration = duration
                )

                audios.add(
                    audio
                )
            }
        }

        audios
    }
}
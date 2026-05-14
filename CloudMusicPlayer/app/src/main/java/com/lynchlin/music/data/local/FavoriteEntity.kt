package com.lynchlin.music.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lynchlin.music.data.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val songId: Long,
    val name: String,
    val artistJson: String?,
    val album: String?,
    val picId: String?,
    val urlId: String?,
    val lyricId: String?,
    val source: String?,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toSong(): Song {
        val artists: List<String>? = artistJson?.let {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                Gson().fromJson(it, type)
            } catch (_: Exception) { null }
        }
        return Song(
            id = songId,
            name = name,
            artist = artists,
            album = album,
            picId = picId,
            urlId = urlId,
            lyricId = lyricId,
            source = source
        )
    }

    companion object {
        fun fromSong(song: Song): FavoriteEntity = FavoriteEntity(
            songId = song.id,
            name = song.name,
            artistJson = song.artist?.let { Gson().toJson(it) },
            album = song.album,
            picId = song.picId,
            urlId = song.urlId,
            lyricId = song.lyricId,
            source = song.source
        )
    }
}

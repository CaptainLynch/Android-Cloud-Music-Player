package com.lynchlin.music.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lynchlin.music.data.model.Song
import com.lynchlin.music.data.repository.FavoritesRepository
import com.lynchlin.music.network.RetrofitClient
import com.lynchlin.music.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    val favoriteSongs: StateFlow<List<Song>> = FavoritesRepository.favoriteSongs

    val isPlaying = MusicPlayerManager.isPlaying
    val currentSong = MusicPlayerManager.currentSong

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _albumArtCache = mutableMapOf<String, String>()

    init {
        FavoritesRepository.init(application)
        MusicPlayerManager.init(application)
        MusicPlayerManager.onSongReady = { song ->
            playSongFromQueue(song)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            FavoritesRepository.toggleFavorite(song)
        }
    }

    suspend fun loadAlbumArt(picId: String): String? {
        _albumArtCache[picId]?.let { return it }
        return try {
            val response = RetrofitClient.apiService.getAlbumArt(id = picId)
            _albumArtCache[picId] = response.url
            response.url
        } catch (_: Exception) {
            null
        }
    }

    fun playSong(song: Song) {
        val songs = favoriteSongs.value
        if (songs.isNotEmpty()) {
            val startIdx = songs.indexOfFirst { it.id == song.id }
            MusicPlayerManager.playQueue(songs, if (startIdx >= 0) startIdx else 0)
        } else {
            MusicPlayerManager.playQueue(listOf(song), 0)
        }
    }

    fun playAll() {
        val songs = favoriteSongs.value
        if (songs.isNotEmpty()) {
            MusicPlayerManager.playQueue(songs, 0)
        }
    }

    fun togglePlayPause() {
        MusicPlayerManager.togglePlayPause()
    }

    fun playNext() {
        MusicPlayerManager.playNext()
    }

    fun playPrevious() {
        MusicPlayerManager.playPrevious()
    }

    private fun playSongFromQueue(song: Song) {
        val urlId = song.urlId ?: run {
            _error.value = "No playable URL for: ${song.name}"
            return
        }
        val source = song.source ?: "netease"
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getSongUrl(
                    id = urlId,
                    source = source
                )
                MusicPlayerManager.playExternalUrl(response.url, song)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load song URL"
                e.printStackTrace()
            }
        }
    }
}

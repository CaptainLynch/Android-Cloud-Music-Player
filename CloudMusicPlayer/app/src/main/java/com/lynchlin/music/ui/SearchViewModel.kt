package com.lynchlin.music.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lynchlin.music.data.model.Song
import com.lynchlin.music.network.RetrofitClient
import com.lynchlin.music.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Platform(val value: String, val label: String)

val availablePlatforms = listOf(
    Platform("netease", "网易云"),
    Platform("kugou", "酷狗"),
    Platform("kuwo", "酷我"),
    Platform("migu", "咪咕"),
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedPlatform = MutableStateFlow(availablePlatforms[0])
    val selectedPlatform: StateFlow<Platform> = _selectedPlatform.asStateFlow()

    private val _searchPlatform = MutableStateFlow(availablePlatforms[0])
    val searchPlatform: StateFlow<Platform> = _searchPlatform.asStateFlow()

    private val _albumArtCache = mutableMapOf<String, String>()
    private val _lyricCache = mutableMapOf<String, String>()

    val isPlaying = MusicPlayerManager.isPlaying
    val currentSong = MusicPlayerManager.currentSong

    init {
        MusicPlayerManager.init(application)
        MusicPlayerManager.onSongReady = { song ->
            playSongFromQueue(song)
        }
    }

    fun setPlatform(platform: Platform) {
        _selectedPlatform.value = platform
    }

    fun searchMusic(keyword: String) {
        if (keyword.isBlank()) return

        val platform = _selectedPlatform.value
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = RetrofitClient.apiService.searchMusic(
                    keyword = keyword,
                    type = platform.value
                )
                _searchPlatform.value = platform
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch data"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun playSong(song: Song) {
        val results = _searchResults.value
        if (results.isNotEmpty()) {
            val startIdx = results.indexOfFirst { it.id == song.id }
            MusicPlayerManager.playQueue(results, if (startIdx >= 0) startIdx else 0)
        } else {
            MusicPlayerManager.playQueue(listOf(song), 0)
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

    fun playQueueAt(index: Int) {
        val queue = MusicPlayerManager.playQueue.value
        if (index in queue.indices) {
            val song = queue[index]
            MusicPlayerManager.setCurrentIndex(index)
            playSongFromQueue(song)
        }
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

    suspend fun loadLyric(lyricId: String): String? {
        _lyricCache[lyricId]?.let { return it }
        return try {
            val response = RetrofitClient.apiService.getLyric(id = lyricId)
            val lyric = response.lyric
            if (lyric != null) _lyricCache[lyricId] = lyric
            lyric
        } catch (_: Exception) {
            null
        }
    }
}

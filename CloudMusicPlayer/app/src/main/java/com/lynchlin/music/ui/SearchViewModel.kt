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

    val favoriteSongs: StateFlow<List<Song>> = FavoritesRepository.favoriteSongs
    val favoriteIds: StateFlow<Set<Long>> = FavoritesRepository.favoriteIds

    private val _albumArtCache = mutableMapOf<String, String>()
    private val _lyricCache = mutableMapOf<String, String>()

    val isPlaying = MusicPlayerManager.isPlaying
    val currentSong = MusicPlayerManager.currentSong
    val isCurrentSongFavorite = MusicPlayerManager.isCurrentSongFavorite

    init {
        FavoritesRepository.init(application)
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
                val metingSongs = RetrofitClient.apiService.searchMusic(
                    server = platform.value,
                    keyword = keyword
                )
                _searchPlatform.value = platform
                val songs = metingSongs.mapIndexed { index, ms ->
                    Song(
                        id = (ms.url.hashCode().toLong() shl 32) or (index.toLong() and 0xFFFFFFFF),
                        name = ms.title,
                        artist = listOf(ms.author),
                        album = null,
                        picId = ms.pic,
                        urlId = ms.url,
                        lyricId = ms.lrc,
                        source = platform.value
                    )
                }
                _searchResults.value = songs
            } catch (e: Exception) {
                _error.value = e.message ?: "搜索失败"
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

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            FavoritesRepository.toggleFavorite(song)
        }
    }

    fun isFavorite(songId: Long): Boolean {
        return FavoritesRepository.isFavorite(songId)
    }

    private fun playSongFromQueue(song: Song) {
        val audioUrl = song.urlId ?: run {
            _error.value = "No playable URL for: ${song.name}"
            return
        }
        viewModelScope.launch {
            try {
                MusicPlayerManager.playExternalUrl(audioUrl, song)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load song URL"
                e.printStackTrace()
            }
        }
    }

    suspend fun loadAlbumArt(picId: String): String? {
        _albumArtCache[picId]?.let { return it }
        return try {
            _albumArtCache[picId] = picId
            picId
        } catch (_: Exception) {
            null
        }
    }

    suspend fun loadLyric(lyricId: String): String? {
        _lyricCache[lyricId]?.let { return it }
        return try {
            val request = okhttp3.Request.Builder().url(lyricId).build()
            val response = okhttp3.OkHttpClient().newCall(request).execute()
            val body = response.body?.string()
            if (body != null) {
                _lyricCache[lyricId] = body
            }
            body
        } catch (_: Exception) {
            null
        }
    }
}
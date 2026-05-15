package com.lynchlin.music.ui.netease

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lynchlin.music.data.model.*
import com.lynchlin.music.data.settings.NeteaseSettings
import com.lynchlin.music.network.NeteaseApiService
import com.lynchlin.music.network.NeteaseOriginApiService
import com.lynchlin.music.network.NeteaseRetrofitClient
import com.lynchlin.music.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class NeteasePage {
    HOME, LOGIN_SETTINGS, PLAYLIST_DETAIL
}

class NeteaseViewModel(application: Application) : AndroidViewModel(application) {
    // --- Navigation ---
    private val _currentPage = MutableStateFlow(NeteasePage.HOME)
    val currentPage: StateFlow<NeteasePage> = _currentPage.asStateFlow()

    // --- Playlists ---
    private val _playlists = MutableStateFlow<List<NeteasePlaylist>>(emptyList())
    val playlists: StateFlow<List<NeteasePlaylist>> = _playlists.asStateFlow()

    // --- Playlist Detail ---
    private val _playlistTracks = MutableStateFlow<List<NeteaseTrack>>(emptyList())
    val playlistTracks: StateFlow<List<NeteaseTrack>> = _playlistTracks.asStateFlow()
    private val _currentPlaylist = MutableStateFlow<NeteasePlaylist?>(null)
    val currentPlaylist: StateFlow<NeteasePlaylist?> = _currentPlaylist.asStateFlow()

    // --- Daily Recommend ---
    private val _dailyRecommendSongs = MutableStateFlow<List<NeteaseTrack>>(emptyList())
    val dailyRecommendSongs: StateFlow<List<NeteaseTrack>> = _dailyRecommendSongs.asStateFlow()

    // --- Private Content / Radar ---
    private val _personalizedPlaylists = MutableStateFlow<List<PersonalizedPlaylist>>(emptyList())
    val personalizedPlaylists: StateFlow<List<PersonalizedPlaylist>> = _personalizedPlaylists.asStateFlow()

    // --- Loading / Error ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // --- Login State ---
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // --- Direct Mode ---
    private val _directMode = MutableStateFlow(NeteaseSettings.directMode)
    val directMode: StateFlow<Boolean> = _directMode.asStateFlow()

    val isLoggedIn: Boolean get() = NeteaseSettings.isLoggedIn()
    val savedPhone: String get() = NeteaseSettings.phone
    val savedNickname: String get() = NeteaseSettings.nickname
    val apiUrl: String get() = NeteaseSettings.apiUrl

    // --- Playback state (delegates to existing MusicPlayerManager) ---
    val isPlaying get() = MusicPlayerManager.isPlaying
    val currentSong get() = MusicPlayerManager.currentSong

    // --- NETEASE_TRACK -> Song map for current playlist ---
    private val _playingTrackIndex = MutableStateFlow(-1)
    private var currentTrackList: List<NeteaseTrack> = emptyList()
    private var currentAlbumArtUrl: String? = null

    init {
        NeteaseSettings.init(application)
        if (isLoggedIn) loadUserPlaylists()

        MusicPlayerManager.onTrackEnded = {
            val idx = _playingTrackIndex.value
            val list = currentTrackList
            if (idx >= 0 && idx < list.size - 1) {
                playTrack(list[idx + 1], list, idx + 1)
                true
            } else false
        }
    }

    private fun api(): NeteaseApiService =
        NeteaseRetrofitClient.getProxyService(NeteaseSettings.apiUrl)

    private fun directApi(): NeteaseOriginApiService =
        NeteaseRetrofitClient.getDirectService()

    private val useDirect: Boolean get() = NeteaseSettings.directMode

    // ===== Navigation =====

    fun goToHome() {
        currentTrackList = emptyList()
        _playingTrackIndex.value = -1
        _currentPage.value = NeteasePage.HOME
    }
    fun goToLoginSettings() { _currentPage.value = NeteasePage.LOGIN_SETTINGS }

    fun openPlaylistDetail(playlist: NeteasePlaylist) {
        _currentPlaylist.value = playlist
        _currentPage.value = NeteasePage.PLAYLIST_DETAIL
        loadPlaylistTracks(playlist.id)
    }

    fun goBackFromPlaylistDetail() {
        _currentPlaylist.value = null
        _playlistTracks.value = emptyList()
        currentTrackList = emptyList()
        _playingTrackIndex.value = -1
        _currentPage.value = NeteasePage.HOME
    }

    // ===== Settings =====

    fun saveApiUrl(url: String) {
        NeteaseSettings.apiUrl = url.trim()
        NeteaseRetrofitClient.invalidate()
    }

    fun toggleDirectMode() {
        val newMode = !NeteaseSettings.directMode
        NeteaseSettings.directMode = newMode
        _directMode.value = newMode
        if (newMode) {
            NeteaseRetrofitClient.invalidateDirect()
        }
        logout()
    }

    // ===== Login =====

    fun login(phone: String, password: String) {
        if (phone.isBlank() || password.isBlank()) {
            _loginError.value = "请输入手机号和密码"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                val resp = if (useDirect) {
                    directApi().loginCellphone(mapOf(
                        "phone" to phone,
                        "password" to password,
                        "countrycode" to "86",
                        "rememberLogin" to "true"
                    ))
                } else {
                    api().loginCellphone(phone = phone, password = password)
                }
                if (resp.code == 200 && resp.cookie != null) {
                    NeteaseSettings.cookie = resp.cookie
                    NeteaseSettings.uid = resp.profile?.userId ?: resp.account?.id ?: 0L
                    NeteaseSettings.nickname = resp.profile?.nickname ?: ""
                    NeteaseSettings.phone = phone
                    _loginError.value = null
                    _currentPage.value = NeteasePage.HOME
                    loadUserPlaylists()
                } else {
                    _loginError.value = resp.message ?: "登录失败，请检查手机号和密码"
                }
            } catch (e: Exception) {
                _loginError.value = "连接失败: ${e.message}，请确认 API 服务地址是否正确"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        NeteaseSettings.logout()
        if (useDirect) {
            NeteaseRetrofitClient.invalidateDirect()
        }
        _playlists.value = emptyList()
        _dailyRecommendSongs.value = emptyList()
        _personalizedPlaylists.value = emptyList()
        currentTrackList = emptyList()
        _playingTrackIndex.value = -1
        _currentPage.value = NeteasePage.HOME
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    // ===== Playlists =====

    fun loadUserPlaylists() {
        if (!isLoggedIn) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val resp = api().userPlaylist(
                    uid = NeteaseSettings.uid,
                    cookie = NeteaseSettings.cookie
                )
                if (resp.code == 200 && resp.playlist != null) {
                    _playlists.value = resp.playlist
                } else {
                    _error.value = "获取歌单失败"
                }
            } catch (e: Exception) {
                _error.value = "获取歌单失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPlaylistTracks(playlistId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val resp = api().playlistAllTracks(
                    id = playlistId,
                    cookie = NeteaseSettings.cookie
                )
                if (resp.code == 200) {
                    val tracks = resp.playlist?.tracks ?: emptyList()
                    _playlistTracks.value = tracks
                } else {
                    _error.value = "获取歌单歌曲失败"
                }
            } catch (e: Exception) {
                _error.value = "获取歌单歌曲失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== Daily Recommendations =====

    fun loadDailyRecommend() {
        if (!isLoggedIn) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val resp = if (useDirect) {
                    directApi().dailyRecommendSongs(emptyMap<String, Any>())
                } else {
                    api().dailyRecommendSongs(
                        cookie = NeteaseSettings.cookie
                    )
                }
                if (resp.code == 200 && resp.data?.dailySongs != null) {
                    _dailyRecommendSongs.value = resp.data.dailySongs
                } else {
                    _error.value = "获取每日推荐失败"
                }
            } catch (e: Exception) {
                _error.value = "获取每日推荐失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== Personalized / Private ---

    fun loadPersonalized() {
        if (!isLoggedIn) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val resp = if (useDirect) {
                    directApi().personalizedPrivateContent(mapOf(
                        "limit" to 10,
                        "offset" to 0
                    ))
                } else {
                    api().personalizedPrivateContentList(
                        cookie = NeteaseSettings.cookie
                    )
                }
                if (resp.code == 200 && resp.result != null) {
                    _personalizedPlaylists.value = resp.result
                } else {
                    _error.value = "获取私人内容失败"
                }
            } catch (e: Exception) {
                _error.value = "获取私人内容失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== Playback =====

    fun playTrack(track: NeteaseTrack, trackList: List<NeteaseTrack> = emptyList(), index: Int = 0) {
        currentTrackList = trackList.ifEmpty { listOf(track) }
        _playingTrackIndex.value = index
        currentAlbumArtUrl = track.albumPicUrl()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val urlResp = if (useDirect) {
                    directApi().songUrl(mapOf(
                        "ids" to "[${track.id}]",
                        "br" to 320000,
                        "csrf_token" to ""
                    ))
                } else {
                    api().songUrl(
                        id = track.id,
                        cookie = NeteaseSettings.cookie
                    )
                }
                val url = urlResp.data?.firstOrNull()?.url
                if (url != null) {
                    val song = track.toSong()
                    MusicPlayerManager.playExternalUrl(url, song)
                } else {
                    _error.value = "暂无播放资源: ${track.name}"
                }
            } catch (e: Exception) {
                _error.value = "获取播放链接失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun playDailyRecommend(index: Int) {
        val songs = _dailyRecommendSongs.value
        if (index in songs.indices) {
            playTrack(songs[index], songs, index)
        }
    }

    fun playPlaylistTrack(index: Int) {
        val tracks = _playlistTracks.value
        if (index in tracks.indices) {
            playTrack(tracks[index], tracks, index)
        }
    }

    fun togglePlayPause() = MusicPlayerManager.togglePlayPause()

    fun playNext() {
        val idx = _playingTrackIndex.value
        val list = currentTrackList
        if (idx >= 0 && idx < list.size - 1) {
            playTrack(list[idx + 1], list, idx + 1)
        }
    }

    fun playPrevious() {
        val idx = _playingTrackIndex.value
        if (idx > 0) {
            playTrack(currentTrackList[idx - 1], currentTrackList, idx - 1)
        }
    }

    fun getAlbumArtUrl(): String? = currentAlbumArtUrl

    fun clearError() {
        _error.value = null
    }
}

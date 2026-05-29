package com.lynchlin.music.player

import android.content.Context
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lynchlin.music.data.model.Song
import com.lynchlin.music.data.repository.FavoritesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object MusicPlayerManager {

    private var exoPlayer: ExoPlayer? = null
    private var service: MediaPlaybackService? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playQueue = MutableStateFlow<List<Song>>(emptyList())
    val playQueue: StateFlow<List<Song>> = _playQueue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isCurrentSongFavorite = MutableStateFlow(false)
    val isCurrentSongFavorite: StateFlow<Boolean> = _isCurrentSongFavorite.asStateFlow()

    // 回调监听器列表（支持多个 ViewModel 注册）
    private val onSongReadyListeners = mutableListOf<(Song) -> Unit>()
    private val onTrackEndedListeners = mutableListOf<() -> Boolean>()
    private val onPlaybackErrorListeners = mutableListOf<(String) -> Unit>()

    fun addOnSongReadyListener(listener: (Song) -> Unit) {
        onSongReadyListeners.add(listener)
    }

    fun removeOnSongReadyListener(listener: (Song) -> Unit) {
        onSongReadyListeners.remove(listener)
    }

    fun addOnTrackEndedListener(listener: () -> Boolean) {
        onTrackEndedListeners.add(listener)
    }

    fun removeOnTrackEndedListener(listener: () -> Boolean) {
        onTrackEndedListeners.remove(listener)
    }

    fun addOnPlaybackErrorListener(listener: (String) -> Unit) {
        onPlaybackErrorListeners.add(listener)
    }

    fun removeOnPlaybackErrorListener(listener: (String) -> Unit) {
        onPlaybackErrorListeners.remove(listener)
    }

    // 触发 onTrackEnded 监听器，返回 true 表示已处理
    fun notifyTrackEnded(): Boolean {
        android.util.Log.d("MusicPlayer", "触发 onTrackEnded 回调，监听器数量: ${onTrackEndedListeners.size}")
        for (listener in onTrackEndedListeners) {
            if (listener()) {
                return true
            }
        }
        return false
    }

    internal fun bindPlayer(player: ExoPlayer, svc: MediaPlaybackService) {
        if (exoPlayer != null) return
        exoPlayer = player
        service = svc

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                android.util.Log.d("MusicPlayer", "isPlaying changed: $isPlaying")
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateName = when (playbackState) {
                    Player.STATE_IDLE -> "STATE_IDLE"
                    Player.STATE_BUFFERING -> "STATE_BUFFERING"
                    Player.STATE_READY -> "STATE_READY"
                    Player.STATE_ENDED -> "STATE_ENDED"
                    else -> "UNKNOWN($playbackState)"
                }
                android.util.Log.d("MusicPlayer", "Playback state changed: $stateName")
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                android.util.Log.d("MusicPlayer", "MediaItem transition: reason=$reason")
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) return
                _duration.value = player.duration
                _currentPosition.value = 0L
            }

            override fun onPlayerError(error: PlaybackException) {
                val songName = _currentSong.value?.name ?: "未知歌曲"
                val msg = when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
                    PlaybackException.ERROR_CODE_IO_NO_PERMISSION -> "播放失败：该音源暂不可用，请尝试其他平台"
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "播放失败：网络连接异常"
                    else -> "播放失败：${error.message ?: "未知错误"}"
                }
                android.util.Log.e("MusicPlayer", "Playback error: $msg, errorCode=${error.errorCode}", error)

                // 重置播放状态
                _isPlaying.value = false
                _currentPosition.value = 0L
                _duration.value = 0L

                android.util.Log.d("MusicPlayer", "触发 onPlaybackError 回调，监听器数量: ${onPlaybackErrorListeners.size}")
                onPlaybackErrorListeners.forEach { listener ->
                    listener(msg)
                }
            }
        })

        setupPositionUpdater()
    }

    internal fun unbindPlayer() {
        exoPlayer = null
        service = null
    }

    fun init(context: Context) {
        if (exoPlayer != null) return
        FavoritesRepository.init(context)
        val intent = Intent(context, MediaPlaybackService::class.java)
        context.startForegroundService(intent)
    }

    fun toggleFavoriteCurrent() {
        val song = _currentSong.value ?: return
        scope.launch {
            FavoritesRepository.toggleFavorite(song)
            _isCurrentSongFavorite.value = FavoritesRepository.isFavorite(song.id)
        }
    }

    private fun updateFavoriteStatus(song: Song?) {
        if (song == null) {
            _isCurrentSongFavorite.value = false
            return
        }
        scope.launch {
            _isCurrentSongFavorite.value = FavoritesRepository.isFavorite(song.id)
        }
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        android.util.Log.d("MusicPlayer", "playQueue: songs=${songs.size}, startIndex=$startIndex")
        val player = exoPlayer ?: return
        if (songs.isEmpty()) return

        _playQueue.value = songs
        _currentIndex.value = startIndex

        val song = songs[startIndex]
        android.util.Log.d("MusicPlayer", "playQueue: 播放歌曲 ${song.name}")
        playSongFromQueue(song)
    }

    fun playNext() {
        val queue = _playQueue.value
        val idx = _currentIndex.value
        if (queue.isEmpty() || idx < 0 || idx >= queue.size - 1) return

        val nextIdx = idx + 1
        _currentIndex.value = nextIdx
        playSongFromQueue(queue[nextIdx])
    }

    fun playPrevious() {
        val queue = _playQueue.value
        val idx = _currentIndex.value
        if (queue.isEmpty() || idx <= 0) return

        val prevIdx = idx - 1
        _currentIndex.value = prevIdx
        playSongFromQueue(queue[prevIdx])
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    fun startPlayback() {
        exoPlayer?.play()
    }

    fun pausePlayback() {
        exoPlayer?.pause()
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun release() {
        service = null
    }

    // 重试播放当前歌曲
    fun retryCurrentSong() {
        val song = _currentSong.value ?: return
        val url = song.urlId ?: return
        android.util.Log.d("MusicPlayer", "retryCurrentSong: song=${song.name}")
        playExternalUrl(url, song)
    }

    fun getExoPlayerPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    fun updatePosition(positionMs: Long) {
        _currentPosition.value = positionMs
    }

    private fun setupPositionUpdater() {
        val player = exoPlayer ?: return
        scope.launch {
            flow {
                while (true) {
                    emit(player.currentPosition)
                    delay(500)
                }
            }.collect { pos ->
                if (player.isPlaying) {
                    _currentPosition.value = pos
                }
            }
        }
    }

    private fun playSongFromQueue(song: Song) {
        android.util.Log.d("MusicPlayer", "playSongFromQueue: song=${song.name}, exoPlayer=${exoPlayer != null}")
        if (exoPlayer == null) return
        _currentSong.value = song
        _currentPosition.value = 0L
        updateFavoriteStatus(song)
        android.util.Log.d("MusicPlayer", "触发 onSongReady 回调，监听器数量: ${onSongReadyListeners.size}")
        onSongReadyListeners.forEach { listener ->
            listener(song)
        }
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun playExternalUrl(url: String, song: Song) {
        android.util.Log.d("MusicPlayer", "playExternalUrl: song=${song.name}, url=${url.take(80)}")

        // 验证 URL 格式
        if (url.isBlank() || !url.startsWith("http")) {
            android.util.Log.e("MusicPlayer", "Invalid URL: $url")
            onPlaybackErrorListeners.forEach { it("无效的播放链接") }
            return
        }

        val player = exoPlayer
        if (player == null) {
            android.util.Log.e("MusicPlayer", "ExoPlayer not ready, retrying in 500ms")
            scope.launch {
                kotlinx.coroutines.delay(500)
                val retryPlayer = exoPlayer
                if (retryPlayer == null) {
                    android.util.Log.e("MusicPlayer", "ExoPlayer still not ready after retry")
                    onPlaybackErrorListeners.forEach { it("播放器未就绪，请稍后重试") }
                    return@launch
                }
                android.util.Log.d("MusicPlayer", "ExoPlayer ready after retry, starting playback")
                _currentSong.value = song
                _currentPosition.value = 0L
                _duration.value = 0L
                startPlaybackInternal(retryPlayer, url, song)
            }
            return
        }
        android.util.Log.d("MusicPlayer", "ExoPlayer ready, starting playback immediately")
        _currentSong.value = song
        _currentPosition.value = 0L
        _duration.value = 0L
        startPlaybackInternal(player, url, song)
    }

    private fun startPlaybackInternal(player: ExoPlayer, url: String, song: Song) {
        player.stop()
        // stop 后等待 ExoPlayer 完成资源释放，避免切换音源时 prepare 被忽略
        scope.launch {
            delay(100)
            player.setMediaItem(MediaItem.fromUri(url))
            player.prepare()
            player.play()
            updateFavoriteStatus(song)

            // 3 秒超时检测：如果播放未启动则触发错误回调
            launch {
                delay(3000)
                if (!player.isPlaying && _currentSong.value?.id == song.id) {
                    android.util.Log.e("MusicPlayer", "Playback start timeout for: ${song.name}")
                    onPlaybackErrorListeners.forEach { it("播放超时: ${song.name}，请重试") }
                }
            }
        }
    }
}

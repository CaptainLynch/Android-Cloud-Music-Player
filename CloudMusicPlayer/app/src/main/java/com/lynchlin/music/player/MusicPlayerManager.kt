package com.lynchlin.music.player

import android.content.Context
import android.content.Intent
import androidx.media3.common.MediaItem
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

    var onSongReady: ((Song) -> Unit) = {}
    var onTrackEnded: (() -> Boolean)? = null

    internal fun bindPlayer(player: ExoPlayer, svc: MediaPlaybackService) {
        if (exoPlayer != null) return
        exoPlayer = player
        service = svc

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = player.duration
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) return
                _duration.value = player.duration
                _currentPosition.value = 0L
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
        val player = exoPlayer ?: return
        if (songs.isEmpty()) return

        _playQueue.value = songs
        _currentIndex.value = startIndex

        val song = songs[startIndex]
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
        if (exoPlayer == null) return
        _currentSong.value = song
        _currentPosition.value = 0L
        updateFavoriteStatus(song)
        onSongReady(song)
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun playExternalUrl(url: String, song: Song) {
        val player = exoPlayer
        if (player == null) {
            android.util.Log.e("MusicPlayer", "ExoPlayer not ready, retrying in 500ms")
            scope.launch {
                kotlinx.coroutines.delay(500)
                val retryPlayer = exoPlayer
                if (retryPlayer == null) {
                    android.util.Log.e("MusicPlayer", "ExoPlayer still not ready after retry")
                    return@launch
                }
                _currentSong.value = song
                retryPlayer.stop()
                retryPlayer.setMediaItem(MediaItem.fromUri(url))
                retryPlayer.prepare()
                retryPlayer.play()
                updateFavoriteStatus(song)
            }
            return
        }
        _currentSong.value = song
        player.stop()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
        updateFavoriteStatus(song)
    }
}

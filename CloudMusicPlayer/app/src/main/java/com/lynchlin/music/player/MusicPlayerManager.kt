package com.lynchlin.music.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lynchlin.music.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MusicPlayerManager {

    private var exoPlayer: ExoPlayer? = null

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

    var onSongReady: ((Song) -> Unit) = {}

    fun init(context: Context) {
        if (exoPlayer != null) return
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = this@apply.duration
                    }
                    if (playbackState == Player.STATE_ENDED) {
                        playNext()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) return
                    _duration.value = this@apply.duration
                    _currentPosition.value = 0L
                }
            })
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

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun getExoPlayerPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    fun updatePosition(positionMs: Long) {
        _currentPosition.value = positionMs
    }

    private fun playSongFromQueue(song: Song) {
        if (exoPlayer == null) return
        _currentSong.value = song
        _currentPosition.value = 0L
        onSongReady(song)
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun playExternalUrl(url: String, song: Song) {
        val player = exoPlayer ?: return
        player.stop()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }
}

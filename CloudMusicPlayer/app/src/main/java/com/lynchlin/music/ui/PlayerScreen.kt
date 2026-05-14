package com.lynchlin.music.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.lynchlin.music.data.model.Song
import com.lynchlin.music.player.MusicPlayerManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel()
) {
    val currentSong by MusicPlayerManager.currentSong.collectAsState()
    val isPlaying by MusicPlayerManager.isPlaying.collectAsState()
    val position by MusicPlayerManager.currentPosition.collectAsState()
    val totalDuration by MusicPlayerManager.duration.collectAsState()
    val playQueue by MusicPlayerManager.playQueue.collectAsState()

    val favoriteIds by viewModel.favoriteIds.collectAsState()

    var albumArtUrl by remember { mutableStateOf<String?>(null) }
    var lyrics by remember { mutableStateOf<String?>(null) }
    var showQueue by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    // Update position periodically
    LaunchedEffect(Unit) {
        while (true) {
            MusicPlayerManager.updatePosition(MusicPlayerManager.getExoPlayerPosition())
            delay(250)
        }
    }

    // Load album art
    LaunchedEffect(currentSong) {
        currentSong?.let { song ->
            albumArtUrl = null
            lyrics = null
            val picId = song.picId
            if (!picId.isNullOrBlank()) {
                try {
                    val resp = viewModel.loadAlbumArt(picId)
                    if (!resp.isNullOrBlank()) albumArtUrl = resp
                } catch (_: Exception) { }
            }
            val lyricId = song.lyricId
            if (!lyricId.isNullOrBlank()) {
                try {
                    val resp = viewModel.loadLyric(lyricId)
                    lyrics = resp
                } catch (_: Exception) { }
            }
        }
    }

    // Update slider when not seeking
    LaunchedEffect(position, isSeeking) {
        if (!isSeeking && totalDuration > 0) {
            sliderPosition = position.toFloat() / totalDuration.toFloat()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    currentSong?.let { song ->
                        val isFav = song.id in favoriteIds
                        IconButton(onClick = { viewModel.toggleFavorite(song) }) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFav) "取消收藏" else "收藏",
                                tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { showQueue = !showQueue }) {
                        Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Queue")
                    }
                }
            )
        }
    ) { padding ->
        if (showQueue) {
            QueuePanel(
                queue = playQueue,
                currentSong = currentSong,
                onSongClick = { index ->
                    viewModel.playQueueAt(index)
                    showQueue = false
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            PlayerContent(
                song = currentSong,
                isPlaying = isPlaying,
                position = position,
                totalDuration = totalDuration,
                sliderPosition = sliderPosition,
                albumArtUrl = albumArtUrl,
                lyrics = lyrics,
                onSeekStart = { isSeeking = true },
                onSeek = { pos ->
                    sliderPosition = pos
                    MusicPlayerManager.seekTo((pos * totalDuration).toLong())
                },
                onSeekEnd = { isSeeking = false },
                onPlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.playNext() },
                onPrevious = { viewModel.playPrevious() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun PlayerContent(
    song: Song?,
    isPlaying: Boolean,
    position: Long,
    totalDuration: Long,
    sliderPosition: Float,
    albumArtUrl: String?,
    lyrics: String?,
    onSeekStart: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekEnd: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Album art
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (albumArtUrl != null) {
                AsyncImage(
                    model = albumArtUrl,
                    contentDescription = "Album art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Song info
        Text(
            text = song?.name ?: "No track",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = song?.artist?.joinToString(", ") ?: "Unknown Artist",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(8.dp))

        // Lyrics preview
        if (!lyrics.isNullOrBlank()) {
            Text(
                text = extractCurrentLyric(lyrics, position) ?: lyrics.take(100),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        // Seek bar
        Slider(
            value = sliderPosition,
            onValueChange = {
                onSeekStart()
                onSeek(it)
            },
            onValueChangeFinished = onSeekEnd,
            modifier = Modifier.fillMaxWidth()
        )

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(position),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTime(totalDuration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))

        // Playback controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { onPrevious() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.width(24.dp))

            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(72.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.width(24.dp))

            IconButton(
                onClick = { onNext() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun QueuePanel(
    queue: List<Song>,
    currentSong: Song?,
    onSongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Play Queue (${queue.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(queue.withIndex().toList(), key = { it.index }) { (index, song) ->
                val isCurrent = song.id == currentSong?.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.name,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = song.artist?.joinToString(", ") ?: "Unknown",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (isCurrent) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Now playing",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private fun extractCurrentLyric(lrc: String, positionMs: Long): String? {
    val lines = lrc.lines()
    val posSec = positionMs / 1000.0

    data class LyricLine(val time: Double, val text: String)

    val parsed = lines.flatMap { line ->
        val matches = Regex("""\[(\d+):(\d+(?:\.\d+)?)\]""").findAll(line)
        val timestamps = matches.map { m ->
            val min = m.groupValues[1].toDouble()
            val sec = m.groupValues[2].toDouble()
            min * 60 + sec
        }.toList()
        if (timestamps.isEmpty()) return@flatMap emptyList()
        val text = line.replace(Regex("""\[(\d+):(\d+(?:\.\d+)?)\]"""), "").trim()
        if (text.isBlank()) return@flatMap emptyList()
        timestamps.map { LyricLine(it, text) }
    }.sortedBy { it.time }

    if (parsed.isEmpty()) return null

    var currentLine: String? = null
    for (i in parsed.indices) {
        if (parsed[i].time <= posSec) {
            currentLine = parsed[i].text
        } else break
    }
    return currentLine
}

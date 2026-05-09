package com.lynchlin.music.ui.netease

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.lynchlin.music.data.model.NeteasePlaylist
import com.lynchlin.music.data.model.NeteaseTrack
import com.lynchlin.music.data.model.PersonalizedPlaylist
import com.lynchlin.music.player.MusicPlayerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeteaseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NeteaseViewModel = viewModel()
) {
    val currentPage by viewModel.currentPage.collectAsState()

    when (currentPage) {
        NeteasePage.HOME -> NeteaseHomeScreen(
            onBack = onBack,
            modifier = modifier,
            viewModel = viewModel
        )
        NeteasePage.LOGIN_SETTINGS -> LoginSettingsScreen(
            onBack = { viewModel.goToHome() },
            modifier = modifier,
            viewModel = viewModel
        )
        NeteasePage.PLAYLIST_DETAIL -> PlaylistDetailScreen(
            onBack = { viewModel.goBackFromPlaylistDetail() },
            modifier = modifier,
            viewModel = viewModel
        )
    }
}

// ========================================
// HOME SCREEN
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NeteaseHomeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NeteaseViewModel = viewModel()
) {
    val playlists by viewModel.playlists.collectAsState()
    val dailySongs by viewModel.dailyRecommendSongs.collectAsState()
    val personalizedPlaylists by viewModel.personalizedPlaylists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("网易云音乐") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.goToLoginSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!viewModel.isLoggedIn) {
                NotLoggedInBanner(
                    onLogin = { viewModel.goToLoginSettings() }
                )
                return@Column
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("歌单")
                    }
                }
                Tab(selected = selectedTab == 1, onClick = {
                    selectedTab = 1
                    if (dailySongs.isEmpty()) viewModel.loadDailyRecommend()
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("每日推荐")
                    }
                }
                Tab(selected = selectedTab == 2, onClick = {
                    selectedTab = 2
                    if (personalizedPlaylists.isEmpty()) viewModel.loadPersonalized()
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("私人雷达")
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("重试")
                        }
                    }
                }
            } else {
                when (selectedTab) {
                    0 -> PlaylistList(playlists = playlists, viewModel = viewModel)
                    1 -> DailyRecommendList(songs = dailySongs, viewModel = viewModel)
                    2 -> PersonalizedList(playlists = personalizedPlaylists, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun NotLoggedInBanner(onLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "尚未登录网易云",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "请先配置 API 服务并登录以查看个人歌单",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onLogin) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("前往设置与登录")
            }
        }
    }
}

// ========================================
// PLAYLIST LIST
// ========================================

@Composable
private fun PlaylistList(
    playlists: List<NeteasePlaylist>,
    viewModel: NeteaseViewModel
) {
    if (playlists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无歌单", color = MaterialTheme.colorScheme.outline)
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(playlists) { playlist ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openPlaylistDetail(playlist) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playlist.coverImgUrl != null) {
                            AsyncImage(
                                model = playlist.coverImgUrl,
                                contentDescription = playlist.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.MusicNote,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${playlist.trackCount} 首 · ${playlist.creator?.nickname ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ========================================
// DAILY RECOMMEND LIST
// ========================================

@Composable
private fun DailyRecommendList(
    songs: List<NeteaseTrack>,
    viewModel: NeteaseViewModel
) {
    if (songs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("点击上方 "每日推荐" 标签加载", color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.loadDailyRecommend() }) {
                    Text("加载每日推荐")
                }
            }
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                "今日为你推荐 ${songs.size} 首",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        itemsIndexed(songs) { index, track ->
            TrackRow(track = track, index = index, onClick = { viewModel.playDailyRecommend(index) })
        }
    }
}

// ========================================
// PERSONALIZED / PRIVATE RADAR
// ========================================

@Composable
private fun PersonalizedList(
    playlists: List<PersonalizedPlaylist>,
    viewModel: NeteaseViewModel
) {
    if (playlists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("点击上方 "私人雷达" 标签加载", color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.loadPersonalized() }) {
                    Text("加载私人内容")
                }
            }
        }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(playlists) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val playlist = NeteasePlaylist(
                            id = item.id,
                            name = item.name,
                            coverImgUrl = item.picUrl,
                            trackCount = item.trackCount,
                            creator = item.creator
                        )
                        viewModel.openPlaylistDetail(playlist)
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.picUrl != null) {
                            AsyncImage(
                                model = item.picUrl,
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(28.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.trackCount} 首 · ${item.creator?.nickname ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ========================================
// PLAYLIST DETAIL SCREEN
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NeteaseViewModel = viewModel()
) {
    val playlist by viewModel.currentPlaylist.collectAsState()
    val tracks by viewModel.playlistTracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentSong by MusicPlayerManager.currentSong.collectAsState()
    val isPlaying by MusicPlayerManager.isPlaying.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(playlist?.name ?: "歌单详情", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
        } else if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("暂无歌曲", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Play all button
                playlist?.let { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = p.coverImgUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${p.trackCount} 首",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalButton(onClick = { viewModel.playPlaylistTrack(0) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("播放全部")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(tracks) { index, track ->
                        val isCurrent = track.id == currentSong?.id
                        TrackRow(
                            track = track,
                            index = index,
                            onClick = { viewModel.playPlaylistTrack(index) },
                            isCurrent = isCurrent,
                            isPlaying = isCurrent && isPlaying
                        )
                    }
                }
            }
        }
    }
}

// ========================================
// SHARED TRACK ROW
// ========================================

@Composable
private fun TrackRow(
    track: NeteaseTrack,
    index: Int,
    onClick: () -> Unit,
    isCurrent: Boolean = false,
    isPlaying: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(28.dp)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                val picUrl = track.albumPicUrl()
                if (picUrl != null) {
                    AsyncImage(
                        model = picUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = track.artists?.joinToString(" / ") { it.name } ?: "未知歌手",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isCurrent && isPlaying) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ========================================
// LOGIN / SETTINGS SCREEN
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NeteaseViewModel = viewModel()
) {
    var apiUrlField by remember { mutableStateOf(viewModel.apiUrl) }
    var phoneField by remember { mutableStateOf(viewModel.savedPhone) }
    var passwordField by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("设置与登录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // API Server Config
            item {
                Text("API 服务器配置", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiUrlField,
                    onValueChange = {
                        apiUrlField = it
                        viewModel.saveApiUrl(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("NeteaseCloudMusicApi 地址") },
                    placeholder = { Text("http://127.0.0.1:3000") },
                    supportingText = {
                        Text("部署 Binaryify/NeteaseCloudMusicApi 后的服务地址")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }

            // Login status
            item {
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("登录状态", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (viewModel.isLoggedIn) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "已登录",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    viewModel.savedNickname.ifBlank { "网易云用户" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "手机号: ${viewModel.savedPhone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("退出登录")
                            }
                        }
                    }
                } else {
                    Text(
                        "未登录，请输入手机号和密码登录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Phone login form
            item {
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("手机号登录", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneField,
                    onValueChange = { phoneField = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("手机号") },
                    placeholder = { Text("输入网易云账号手机号") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )
            }

            item {
                OutlinedTextField(
                    value = passwordField,
                    onValueChange = { passwordField = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("密码") },
                    placeholder = { Text("输入密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (phoneField.isNotBlank() && passwordField.isNotBlank()) {
                            viewModel.login(phoneField, passwordField)
                        }
                    })
                )
            }

            // Login error
            if (loginError != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = loginError ?: "",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Login button
            item {
                Button(
                    onClick = { viewModel.login(phoneField, passwordField) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && phoneField.isNotBlank() && passwordField.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isLoading) "登录中..." else "登录")
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text(
                    "提示: 请先在自己的服务器上部署 NeteaseCloudMusicApi，\n然后在上方输入 API 地址和网易云账号信息。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

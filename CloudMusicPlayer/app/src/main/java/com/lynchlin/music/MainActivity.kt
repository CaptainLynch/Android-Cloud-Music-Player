package com.lynchlin.music

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.lynchlin.music.player.MusicPlayerManager
import com.lynchlin.music.ui.FavoritesScreen
import com.lynchlin.music.ui.PlayerScreen
import com.lynchlin.music.ui.SearchScreen
import com.lynchlin.music.ui.netease.NeteaseScreen
import com.lynchlin.music.ui.theme.CloudMusicPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val openPlayerFromIntent = intent?.getBooleanExtra("open_player", false) == true
        setContent {
            CloudMusicPlayerTheme {
                var showPlayer by remember { mutableStateOf(openPlayerFromIntent) }
                var showNetease by remember { mutableStateOf(false) }
                var showFavorites by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        showNetease -> NeteaseScreen(
                            onBack = { showNetease = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                        showPlayer -> PlayerScreen(
                            onBack = { showPlayer = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                        showFavorites -> FavoritesScreen(
                            onBack = { showFavorites = false },
                            onOpenPlayer = { showPlayer = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                        else -> SearchScreen(
                            onOpenPlayer = { showPlayer = true },
                            onOpenNetease = { showNetease = true },
                            onOpenFavorites = { showFavorites = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            MusicPlayerManager.release()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

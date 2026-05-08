package com.lynchlin.music

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
import com.lynchlin.music.ui.PlayerScreen
import com.lynchlin.music.ui.SearchScreen
import com.lynchlin.music.ui.theme.CloudMusicPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudMusicPlayerTheme {
                var showPlayer by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showPlayer) {
                        PlayerScreen(
                            onBack = { showPlayer = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        SearchScreen(
                            onOpenPlayer = { showPlayer = true },
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
}

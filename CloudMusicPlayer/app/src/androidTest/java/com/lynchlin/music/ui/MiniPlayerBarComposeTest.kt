package com.lynchlin.music.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MiniPlayerBarComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `MiniPlayerBar displays song name`() {
        composeTestRule.setContent {
            MiniPlayerBar(
                songName = "Test Song",
                artist = "Test Artist",
                isPlaying = false,
                onPlayPauseClick = {},
                onClick = {}
            )
        }
        composeTestRule.onNodeWithText("Test Song").assertExists()
    }

    @Test
    fun `MiniPlayerBar displays artist name`() {
        composeTestRule.setContent {
            MiniPlayerBar(
                songName = "Test Song",
                artist = "Test Artist",
                isPlaying = false,
                onPlayPauseClick = {},
                onClick = {}
            )
        }
        composeTestRule.onNodeWithText("Test Artist").assertExists()
    }

    @Test
    fun `MiniPlayerBar shows PlayArrow when not playing`() {
        composeTestRule.setContent {
            MiniPlayerBar(
                songName = "Song",
                artist = "Artist",
                isPlaying = false,
                onPlayPauseClick = {},
                onClick = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
    }

    @Test
    fun `MiniPlayerBar shows Pause when playing`() {
        composeTestRule.setContent {
            MiniPlayerBar(
                songName = "Song",
                artist = "Artist",
                isPlaying = true,
                onPlayPauseClick = {},
                onClick = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Pause").assertExists()
    }
}

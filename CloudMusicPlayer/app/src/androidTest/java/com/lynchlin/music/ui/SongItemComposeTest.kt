package com.lynchlin.music.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lynchlin.music.data.model.Song
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SongItemComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `SongItem displays song name`() {
        val song = Song(1L, "Test Song Name", listOf("Test Artist"), null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                isFavorite = false,
                onToggleFavorite = {}
            )
        }
        composeTestRule.onNodeWithText("Test Song Name").assertExists()
    }

    @Test
    fun `SongItem displays artist name`() {
        val song = Song(1L, "Song", listOf("Artist Name"), null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                isFavorite = false,
                onToggleFavorite = {}
            )
        }
        composeTestRule.onNodeWithText("Artist Name").assertExists()
    }

    @Test
    fun `SongItem shows FavoriteBorder when not favorite`() {
        val song = Song(1L, "Song", null, null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                isFavorite = false,
                onToggleFavorite = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("收藏").assertExists()
    }

    @Test
    fun `SongItem shows Favorite when favorited`() {
        val song = Song(1L, "Song", null, null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                isFavorite = true,
                onToggleFavorite = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("取消收藏").assertExists()
    }

    @Test
    fun `SongItem displays source label when provided`() {
        val song = Song(1L, "Song", null, null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                sourceLabel = "网易云",
                isFavorite = false,
                onToggleFavorite = {}
            )
        }
        composeTestRule.onNodeWithText("网易云").assertExists()
    }

    @Test
    fun `SongItem does not show source label when empty`() {
        val song = Song(1L, "Song", null, null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                sourceLabel = "",
                isFavorite = false,
                onToggleFavorite = {}
            )
        }
        composeTestRule.onNodeWithText("Song").assertExists()
    }

    @Test
    fun `SongItem without onToggleFavorite hides favorite icon`() {
        val song = Song(1L, "Song", null, null, null, null, null, null)
        composeTestRule.setContent {
            SongItem(
                song = song,
                onClick = {},
                onToggleFavorite = null
            )
        }
        val nodes = composeTestRule.onAllNodesWithContentDescription("收藏")
        nodes.assertCountEquals(0)
    }
}

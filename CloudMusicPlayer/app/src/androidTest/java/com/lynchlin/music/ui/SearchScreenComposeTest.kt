package com.lynchlin.music.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `SearchScreen shows platform filter chips`() {
        composeTestRule.setContent {
            SearchScreen(
                onOpenNetease = {},
                onOpenFavorites = {}
            )
        }
        composeTestRule.onNodeWithText("网易云").assertExists()
        composeTestRule.onNodeWithText("酷狗").assertExists()
        composeTestRule.onNodeWithText("酷我").assertExists()
        composeTestRule.onNodeWithText("咪咕").assertExists()
    }

    @Test
    fun `SearchScreen shows search field`() {
        composeTestRule.setContent {
            SearchScreen(
                onOpenNetease = {},
                onOpenFavorites = {}
            )
        }
        composeTestRule.onNodeWithText("Search music...").assertExists()
    }

    @Test
    fun `SearchScreen shows 歌单 and 收藏 buttons`() {
        composeTestRule.setContent {
            SearchScreen(
                onOpenNetease = {},
                onOpenFavorites = {}
            )
        }
        composeTestRule.onNodeWithText("歌单").assertExists()
        composeTestRule.onNodeWithText("收藏").assertExists()
    }
}

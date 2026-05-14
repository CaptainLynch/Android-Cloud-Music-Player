package com.lynchlin.music.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `FavoritesScreen shows title`() {
        composeTestRule.setContent {
            FavoritesScreen(onBack = {})
        }
        composeTestRule.onNodeWithText("我的收藏").assertExists()
    }

    @Test
    fun `FavoritesScreen shows empty state when no favorites`() {
        composeTestRule.setContent {
            FavoritesScreen(onBack = {})
        }
        composeTestRule.onNodeWithText("暂无收藏歌曲").assertExists()
        composeTestRule.onNodeWithText("在搜索结果中点击心形图标收藏歌曲").assertExists()
    }

    @Test
    fun `FavoritesScreen shows back button`() {
        composeTestRule.setContent {
            FavoritesScreen(onBack = {})
        }
        composeTestRule.onNodeWithContentDescription("返回").assertExists()
    }
}

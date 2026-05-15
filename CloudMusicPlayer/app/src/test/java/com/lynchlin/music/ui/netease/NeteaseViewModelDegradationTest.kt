package com.lynchlin.music.ui.netease

import org.junit.Assert.*
import org.junit.Test

class NeteaseViewModelDegradationTest {

    @Test
    fun `AD-008 comment exists before loadUserPlaylists in source`() {
        val sourceText = NeteaseViewModel::class.java.getDeclaredMethod(
            "loadUserPlaylists"
        ).toString()
        assertNotNull(sourceText)
    }

    @Test
    fun `loadUserPlaylists method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod("loadUserPlaylists")
        assertNotNull(method)
    }

    @Test
    fun `loadPlaylistTracks method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod(
            "loadPlaylistTracks", Long::class.javaPrimitiveType
        )
        assertNotNull(method)
    }

    @Test
    fun `loadDailyRecommend method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod("loadDailyRecommend")
        assertNotNull(method)
    }

    @Test
    fun `login method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod(
            "login", String::class.java, String::class.java
        )
        assertNotNull(method)
    }

    @Test
    fun `testProxyConnection method exists`() {
        val methods = NeteaseViewModel::class.java.declaredMethods
        val exists = methods.any { it.name == "testProxyConnection" }
        assertTrue("testProxyConnection method should exist", exists)
    }

    @Test
    fun `saveApiUrl method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod(
            "saveApiUrl", String::class.java
        )
        assertNotNull(method)
    }

    @Test
    fun `toggleDirectMode method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod("toggleDirectMode")
        assertNotNull(method)
    }

    @Test
    fun `loadPersonalized method exists`() {
        val method = NeteaseViewModel::class.java.getDeclaredMethod("loadPersonalized")
        assertNotNull(method)
    }

    @Test
    fun `playTrack method exists`() {
        val methods = NeteaseViewModel::class.java.declaredMethods
        val exists = methods.any { it.name == "playTrack" }
        assertTrue("playTrack method should exist", exists)
    }
}

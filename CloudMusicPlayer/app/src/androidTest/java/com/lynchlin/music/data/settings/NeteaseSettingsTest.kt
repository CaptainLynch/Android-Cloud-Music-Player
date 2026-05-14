package com.lynchlin.music.data.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NeteaseSettingsTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NeteaseSettings.init(context)
        resetAll()
    }

    @After
    fun tearDown() {
        resetAll()
    }

    private fun resetAll() {
        NeteaseSettings.apiUrl = "http://127.0.0.1:3000"
        NeteaseSettings.cookie = ""
        NeteaseSettings.uid = 0L
        NeteaseSettings.nickname = ""
        NeteaseSettings.phone = ""
        NeteaseSettings.directMode = false
    }

    @Test
    fun `default apiUrl`() {
        assertEquals("http://127.0.0.1:3000", NeteaseSettings.apiUrl)
    }

    @Test
    fun `apiUrl read after write`() {
        NeteaseSettings.apiUrl = "http://192.168.1.1:3000"
        assertEquals("http://192.168.1.1:3000", NeteaseSettings.apiUrl)
    }

    @Test
    fun `cookie read after write`() {
        NeteaseSettings.cookie = "MUSIC_U=test_cookie"
        assertEquals("MUSIC_U=test_cookie", NeteaseSettings.cookie)
    }

    @Test
    fun `uid read after write`() {
        NeteaseSettings.uid = 123456L
        assertEquals(123456L, NeteaseSettings.uid)
    }

    @Test
    fun `nickname read after write`() {
        NeteaseSettings.nickname = "TestUser"
        assertEquals("TestUser", NeteaseSettings.nickname)
    }

    @Test
    fun `phone read after write`() {
        NeteaseSettings.phone = "13800138000"
        assertEquals("13800138000", NeteaseSettings.phone)
    }

    @Test
    fun `directMode default is false`() {
        assertFalse(NeteaseSettings.directMode)
    }

    @Test
    fun `directMode read after write`() {
        NeteaseSettings.directMode = true
        assertTrue(NeteaseSettings.directMode)
        NeteaseSettings.directMode = false
        assertFalse(NeteaseSettings.directMode)
    }

    @Test
    fun `isLoggedIn returns false when not logged in`() {
        NeteaseSettings.cookie = ""
        NeteaseSettings.uid = 0L
        assertFalse(NeteaseSettings.isLoggedIn())
    }

    @Test
    fun `isLoggedIn returns false when only cookie set`() {
        NeteaseSettings.cookie = "some_cookie"
        NeteaseSettings.uid = 0L
        assertFalse(NeteaseSettings.isLoggedIn())
    }

    @Test
    fun `isLoggedIn returns false when only uid set`() {
        NeteaseSettings.cookie = ""
        NeteaseSettings.uid = 123L
        assertFalse(NeteaseSettings.isLoggedIn())
    }

    @Test
    fun `isLoggedIn returns true when both cookie and uid set`() {
        NeteaseSettings.cookie = "MUSIC_U=abc123"
        NeteaseSettings.uid = 12345L
        assertTrue(NeteaseSettings.isLoggedIn())
    }

    @Test
    fun `logout clears cookie uid and nickname`() {
        NeteaseSettings.cookie = "some_cookie"
        NeteaseSettings.uid = 999L
        NeteaseSettings.nickname = "User"
        NeteaseSettings.logout()
        assertEquals("", NeteaseSettings.cookie)
        assertEquals(0L, NeteaseSettings.uid)
        assertEquals("", NeteaseSettings.nickname)
    }

    @Test
    fun `logout does not affect apiUrl and directMode`() {
        NeteaseSettings.apiUrl = "http://test.com:3000"
        NeteaseSettings.directMode = true
        NeteaseSettings.logout()
        assertEquals("http://test.com:3000", NeteaseSettings.apiUrl)
        assertTrue(NeteaseSettings.directMode)
    }
}

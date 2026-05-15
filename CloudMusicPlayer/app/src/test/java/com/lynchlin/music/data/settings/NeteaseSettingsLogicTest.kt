package com.lynchlin.music.data.settings

import org.junit.Assert.*
import org.junit.Test

class NeteaseSettingsLogicTest {

    @Test
    fun `DEFAULT_API_URL is defined`() {
        assertEquals("https://your-netease-api.example.com", NeteaseSettings.DEFAULT_API_URL)
    }

    @Test
    fun `isLoggedIn requires cookie and uid`() {
        assertFalse(NeteaseSettings.isLoggedIn())
    }
}

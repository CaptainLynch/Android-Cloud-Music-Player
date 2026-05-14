package com.lynchlin.music.network

import org.junit.Assert.*
import org.junit.Test

class NeteaseApiModeTest {

    @Test
    fun `enum has two values`() {
        assertEquals(2, NeteaseApiMode.entries.size)
    }

    @Test
    fun `enum contains PROXY`() {
        assertNotNull(NeteaseApiMode.valueOf("PROXY"))
    }

    @Test
    fun `enum contains DIRECT`() {
        assertNotNull(NeteaseApiMode.valueOf("DIRECT"))
    }

    @Test
    fun `PROXY and DIRECT are distinct`() {
        assertNotEquals(NeteaseApiMode.PROXY, NeteaseApiMode.DIRECT)
    }
}

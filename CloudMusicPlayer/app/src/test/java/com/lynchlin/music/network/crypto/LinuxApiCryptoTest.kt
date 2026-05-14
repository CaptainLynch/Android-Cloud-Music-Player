package com.lynchlin.music.network.crypto

import org.junit.Assert.*
import org.junit.Test

class LinuxApiCryptoTest {

    @Test
    fun `encrypt returns non-null hex string`() {
        val result = LinuxApiCrypto.encrypt("{\"test\":\"data\"}")
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `encrypt returns valid hex`() {
        val result = LinuxApiCrypto.encrypt("{\"key\":\"value\"}")
        assertTrue(result.matches(Regex("[0-9a-f]+")))
    }

    @Test
    fun `encrypt produces different output for different inputs`() {
        val r1 = LinuxApiCrypto.encrypt("{\"a\":1}")
        val r2 = LinuxApiCrypto.encrypt("{\"a\":2}")
        assertNotEquals(r1, r2)
    }

    @Test
    fun `encrypt is deterministic`() {
        val input = "{\"test\":\"deterministic\"}"
        val r1 = LinuxApiCrypto.encrypt(input)
        val r2 = LinuxApiCrypto.encrypt(input)
        assertEquals(r1, r2)
    }

    @Test
    fun `encrypt handles empty JSON object`() {
        val result = LinuxApiCrypto.encrypt("{}")
        assertTrue(result.isNotEmpty())
        assertTrue(result.matches(Regex("[0-9a-f]+")))
    }

    @Test
    fun `encrypt output length is multiple of 32 hex chars per 16-byte block`() {
        val result = LinuxApiCrypto.encrypt("{\"phone\":\"13800138000\",\"password\":\"test123\"}")
        assertTrue(result.length % 32 == 0)
    }
}

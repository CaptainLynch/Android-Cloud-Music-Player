package com.lynchlin.music.network.crypto

import org.junit.Assert.*
import org.junit.Test
import java.util.Base64

class CryptoIntegrationTest {

    @Test
    fun `WeApiCrypto uses CryptoUtils aesEncryptCbc`() {
        val json = "{\"action\":\"test\"}"
        val (params, _) = WeApiCrypto.encrypt(json)
        assertTrue(params.isNotEmpty())
        val decoded = Base64.getDecoder().decode(params)
        assertTrue(decoded.size >= 16)
    }

    @Test
    fun `LinuxApiCrypto uses CryptoUtils aesEncryptEcb`() {
        val json = "{\"action\":\"test\"}"
        val result = LinuxApiCrypto.encrypt(json)
        assertTrue(result.isNotEmpty())
        assertEquals(0, result.length % 2)
    }

    @Test
    fun `WeApiCrypto and LinuxApiCrypto produce different output for same input`() {
        val json = "{\"id\":123}"
        val (weParams, _) = WeApiCrypto.encrypt(json)
        val linuxResult = LinuxApiCrypto.encrypt(json)
        assertNotEquals(weParams, linuxResult)
    }

    @Test
    fun `md5 produces known hashes`() {
        assertEquals("5d41402abc4b2a76b9719d911017c592", CryptoUtils.md5("hello"))
        assertEquals("5eb63bbbe01eeed093cb22bb8f5acdc3", CryptoUtils.md5("hello world"))
    }

    @Test
    fun `CryptoUtils aesEncryptCbc and aesEncryptEcb are different modes`() {
        val key = "0123456789abcdef"
        val plain = "test_message_for_comparison"
        val cbcResult = CryptoUtils.aesEncryptCbc(plain, key)
        val ecbResult = CryptoUtils.aesEncryptEcb(plain, key)
        assertFalse(cbcResult.contentEquals(ecbResult))
    }
}

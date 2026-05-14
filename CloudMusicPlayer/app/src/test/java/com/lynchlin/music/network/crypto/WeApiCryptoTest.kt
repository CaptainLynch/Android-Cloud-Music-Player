package com.lynchlin.music.network.crypto

import org.junit.Assert.*
import org.junit.Test
import java.util.Base64

class WeApiCryptoTest {

    @Test
    fun `encrypt returns non-null pair`() {
        val (params, encSecKey) = WeApiCrypto.encrypt("{\"phone\":\"13800138000\"}")
        assertNotNull(params)
        assertNotNull(encSecKey)
    }

    @Test
    fun `encrypt params is non-empty`() {
        val (params, encSecKey) = WeApiCrypto.encrypt("{\"test\":true}")
        assertTrue(params.isNotEmpty())
        assertTrue(encSecKey.isNotEmpty())
    }

    @Test
    fun `encrypt encSecKey is 256 hex characters`() {
        val (_, encSecKey) = WeApiCrypto.encrypt("{\"test\":\"data\"}")
        assertEquals(256, encSecKey.length)
        assertTrue(encSecKey.matches(Regex("[0-9a-f]{256}")))
    }

    @Test
    fun `encrypt params is valid base64`() {
        val (params, _) = WeApiCrypto.encrypt("{\"key\":\"value\"}")
        val decoded = try {
            Base64.getDecoder().decode(params)
            true
        } catch (_: Exception) {
            false
        }
        assertTrue("params should be valid base64", decoded)
    }

    @Test
    fun `encrypt produces different params for different inputs`() {
        val (p1, _) = WeApiCrypto.encrypt("{\"a\":1}")
        val (p2, _) = WeApiCrypto.encrypt("{\"b\":2}")
        assertNotEquals(p1, p2)
    }

    @Test
    fun `encrypt produces different encSecKey for different calls`() {
        val (_, k1) = WeApiCrypto.encrypt("{\"x\":\"y\"}")
        val (_, k2) = WeApiCrypto.encrypt("{\"x\":\"y\"}")
        assertNotEquals(k1, k2)
    }

    @Test
    fun `encrypt handles empty JSON object`() {
        val (params, encSecKey) = WeApiCrypto.encrypt("{}")
        assertTrue(params.isNotEmpty())
        assertTrue(encSecKey.isNotEmpty())
    }

    @Test
    fun `encrypt handles complex JSON`() {
        val json = """{"phone":"13800138000","password":"test123","countrycode":"86","rememberLogin":"true"}"""
        val (params, encSecKey) = WeApiCrypto.encrypt(json)
        assertTrue(params.isNotEmpty())
        assertEquals(256, encSecKey.length)
    }

    @Test
    fun `encrypt params length grows with input length`() {
        val (shortParam, _) = WeApiCrypto.encrypt("{}")
        val (longParam, _) = WeApiCrypto.encrypt("{\"data\":\"${"x".repeat(100)}\"}")
        assertTrue(longParam.length > shortParam.length)
    }
}

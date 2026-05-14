package com.lynchlin.music.network.crypto

import org.junit.Assert.*
import org.junit.Test

class CryptoUtilsTest {

    @Test
    fun `generateAesKey returns 16-character string`() {
        val key = CryptoUtils.generateAesKey()
        assertEquals(16, key.length)
        val validChars = "0123456789abcdeffedcba9876543210"
        key.forEach { c -> assertTrue("Invalid char: $c", c in validChars) }
    }

    @Test
    fun `generateAesKey produces different keys`() {
        val keys = (1..10).map { CryptoUtils.generateAesKey() }
        val unique = keys.toSet()
        assertTrue("Should produce different keys", unique.size > 1)
    }

    @Test
    fun `md5 produces correct hash for known input`() {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", CryptoUtils.md5("abc"))
        assertEquals("098f6bcd4621d373cade4e832627b4f6", CryptoUtils.md5("test"))
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", CryptoUtils.md5(""))
    }

    @Test
    fun `md5 is deterministic`() {
        val input = "hello world 12345"
        val h1 = CryptoUtils.md5(input)
        val h2 = CryptoUtils.md5(input)
        assertEquals(h1, h2)
    }

    @Test
    fun `md5 is length 32 hex`() {
        val hash = CryptoUtils.md5("anything")
        assertEquals(32, hash.length)
        assertTrue(hash.matches(Regex("[0-9a-f]{32}")))
    }

    @Test
    fun `aesEncryptCbc produces encrypted output`() {
        val key = "0123456789abcdef"
        val plain = "Hello World!"
        val encrypted = CryptoUtils.aesEncryptCbc(plain, key)
        assertNotNull(encrypted)
        assertTrue(encrypted.isNotEmpty())
        val plainBytes = plain.toByteArray(Charsets.UTF_8)
        assertFalse(plainBytes.contentEquals(encrypted))
    }

    @Test
    fun `aesEncryptCbc is deterministic with same key and iv`() {
        val key = "0123456789abcdef"
        val plain = "test data"
        val enc1 = CryptoUtils.aesEncryptCbc(plain, key)
        val enc2 = CryptoUtils.aesEncryptCbc(plain, key)
        assertArrayEquals(enc1, enc2)
    }

    @Test
    fun `aesEncryptCbc produces different output for different keys`() {
        val plain = "same data"
        val key1 = "0123456789abcdef"
        val key2 = "fedcba9876543210"
        val enc1 = CryptoUtils.aesEncryptCbc(plain, key1)
        val enc2 = CryptoUtils.aesEncryptCbc(plain, key2)
        assertFalse(enc1.contentEquals(enc2))
    }

    @Test
    fun `aesEncryptCbc produces different output for different inputs`() {
        val key = "0123456789abcdef"
        val enc1 = CryptoUtils.aesEncryptCbc("hello", key)
        val enc2 = CryptoUtils.aesEncryptCbc("world", key)
        assertFalse(enc1.contentEquals(enc2))
    }

    @Test
    fun `aesEncryptEcb produces encrypted output`() {
        val key = "rFgB&h#%2?^eDg:Q"
        val plain = "Hello World!"
        val encrypted = CryptoUtils.aesEncryptEcb(plain, key)
        assertNotNull(encrypted)
        assertTrue(encrypted.isNotEmpty())
        val plainBytes = plain.toByteArray(Charsets.UTF_8)
        assertFalse(plainBytes.contentEquals(encrypted))
    }

    @Test
    fun `aesEncryptEcb is deterministic`() {
        val key = "rFgB&h#%2?^eDg:Q"
        val plain = "test data"
        val enc1 = CryptoUtils.aesEncryptEcb(plain, key)
        val enc2 = CryptoUtils.aesEncryptEcb(plain, key)
        assertArrayEquals(enc1, enc2)
    }

    @Test
    fun `aesEncryptEcb produces different output for different keys`() {
        val plain = "same data"
        val key1 = "rFgB&h#%2?^eDg:Q"
        val key2 = "aaaaaaaaaaaaaaaa"
        val enc1 = CryptoUtils.aesEncryptEcb(plain, key1)
        val enc2 = CryptoUtils.aesEncryptEcb(plain, key2)
        assertFalse(enc1.contentEquals(enc2))
    }

    @Test
    fun `bytesToHex produces lowercase hex`() {
        val bytes = byteArrayOf(0x0f.toByte(), 0x1a.toByte(), 0xff.toByte())
        val hex = CryptoUtils.bytesToHex(bytes)
        assertEquals("0f1aff", hex)
    }

    @Test
    fun `bytesToHex empty array`() {
        val hex = CryptoUtils.bytesToHex(byteArrayOf())
        assertEquals("", hex)
    }

    @Test
    fun `aesEncryptCbc handles empty string`() {
        val key = "0123456789abcdef"
        val encrypted = CryptoUtils.aesEncryptCbc("", key)
        assertNotNull(encrypted)
    }

    @Test
    fun `aesEncryptEcb handles empty string`() {
        val key = "rFgB&h#%2?^eDg:Q"
        val encrypted = CryptoUtils.aesEncryptEcb("", key)
        assertNotNull(encrypted)
    }

    @Test(expected = Exception::class)
    fun `aesEncryptCbc throws with invalid key length`() {
        CryptoUtils.aesEncryptCbc("data", "short")
    }

    @Test(expected = Exception::class)
    fun `aesEncryptEcb throws with invalid key length`() {
        CryptoUtils.aesEncryptEcb("data", "short")
    }
}

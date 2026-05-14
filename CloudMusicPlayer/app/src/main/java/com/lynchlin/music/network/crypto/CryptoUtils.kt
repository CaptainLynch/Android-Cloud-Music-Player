package com.lynchlin.music.network.crypto

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val AES_KEY_CHARS = "0123456789abcdeffedcba9876543210"
    private const val IV_STRING = "0102030405060708"

    private val secureRandom = SecureRandom()

    fun generateAesKey(): String {
        val sb = StringBuilder(16)
        repeat(16) {
            sb.append(AES_KEY_CHARS[secureRandom.nextInt(AES_KEY_CHARS.length)])
        }
        return sb.toString()
    }

    fun aesEncryptCbc(text: String, key: String): ByteArray {
        val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        val ivSpec = IvParameterSpec(IV_STRING.toByteArray(Charsets.UTF_8))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(text.toByteArray(Charsets.UTF_8))
    }

    fun aesEncryptEcb(text: String, key: String): ByteArray {
        val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        return cipher.doFinal(text.toByteArray(Charsets.UTF_8))
    }

    fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun base64Encode(data: ByteArray): String {
        return android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP)
    }

    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

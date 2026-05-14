package com.lynchlin.music.network.crypto

import java.math.BigInteger

object WeApiCrypto {

    private const val MODULUS = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725" +
            "152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870" +
            "114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d" +
            "546b8e289dc6935b3ece0462db0a22b8e7"

    private const val EXPONENT = "010001"

    fun encrypt(jsonData: String): Pair<String, String> {
        val aesKey = CryptoUtils.generateAesKey()

        val encBytes = CryptoUtils.aesEncryptCbc(jsonData, aesKey)
        val encText = CryptoUtils.base64Encode(encBytes)

        val encSecKey = rsaEncrypt(aesKey)

        return Pair(encText, encSecKey)
    }

    private fun rsaEncrypt(text: String): String {
        val reversed = text.reversed()

        val hexStr = StringBuilder()
        for (c in reversed) {
            hexStr.append(c.code.toString(16).padStart(2, '0'))
        }

        val plainBigInt = BigInteger(hexStr.toString(), 16)
        val modulusBigInt = BigInteger(MODULUS, 16)
        val exponentBigInt = BigInteger(EXPONENT, 16)

        val cipherBigInt = plainBigInt.modPow(exponentBigInt, modulusBigInt)

        val cipherHex = cipherBigInt.toString(16)
        return cipherHex.padStart(256, '0')
    }
}

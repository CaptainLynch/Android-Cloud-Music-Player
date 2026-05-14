package com.lynchlin.music.network.crypto

object LinuxApiCrypto {

    private const val EAPI_KEY = "rFgB&h#%2?^eDg:Q"

    fun encrypt(jsonData: String): String {
        val encBytes = CryptoUtils.aesEncryptEcb(jsonData, EAPI_KEY)
        return CryptoUtils.bytesToHex(encBytes)
    }
}

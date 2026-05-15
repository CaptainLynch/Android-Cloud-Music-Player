package com.lynchlin.music.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object NeteaseSettings {
    private const val PREFS_NAME = "netease_music_settings"
    private const val KEY_API_URL = "netease_api_url"
    private const val KEY_COOKIE = "netease_cookie"
    private const val KEY_UID = "netease_uid"
    private const val KEY_NICKNAME = "netease_nickname"
    private const val KEY_PHONE = "netease_phone"
    private const val KEY_DIRECT_MODE = "netease_direct_mode"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var apiUrl: String
        get() = prefs?.getString(KEY_API_URL, DEFAULT_API_URL) ?: DEFAULT_API_URL
        set(value) { prefs?.edit { putString(KEY_API_URL, value) } }

    const val DEFAULT_API_URL = "https://your-netease-api.example.com"

    var cookie: String
        get() = prefs?.getString(KEY_COOKIE, "") ?: ""
        set(value) { prefs?.edit { putString(KEY_COOKIE, value) } }

    var uid: Long
        get() = prefs?.getLong(KEY_UID, 0L) ?: 0L
        set(value) { prefs?.edit { putLong(KEY_UID, value) } }

    var nickname: String
        get() = prefs?.getString(KEY_NICKNAME, "") ?: ""
        set(value) { prefs?.edit { putString(KEY_NICKNAME, value) } }

    var phone: String
        get() = prefs?.getString(KEY_PHONE, "") ?: ""
        set(value) { prefs?.edit { putString(KEY_PHONE, value) } }

    var directMode: Boolean
        get() = prefs?.getBoolean(KEY_DIRECT_MODE, false) ?: false
        set(value) { prefs?.edit { putBoolean(KEY_DIRECT_MODE, value) } }

    fun isLoggedIn(): Boolean = cookie.isNotBlank() && uid > 0

    fun logout() {
        cookie = ""
        uid = 0L
        nickname = ""
    }
}

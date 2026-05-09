package com.lynchlin.music.data.settings

import android.content.Context
import android.content.SharedPreferences

object NeteaseSettings {
    private const val PREFS_NAME = "netease_music_settings"
    private const val KEY_API_URL = "netease_api_url"
    private const val KEY_COOKIE = "netease_cookie"
    private const val KEY_UID = "netease_uid"
    private const val KEY_NICKNAME = "netease_nickname"
    private const val KEY_PHONE = "netease_phone"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var apiUrl: String
        get() = prefs?.getString(KEY_API_URL, "http://127.0.0.1:3000") ?: "http://127.0.0.1:3000"
        set(value) = prefs?.edit()?.putString(KEY_API_URL, value)?.apply()

    var cookie: String
        get() = prefs?.getString(KEY_COOKIE, "") ?: ""
        set(value) = prefs?.edit()?.putString(KEY_COOKIE, value)?.apply()

    var uid: Long
        get() = prefs?.getLong(KEY_UID, 0L) ?: 0L
        set(value) = prefs?.edit()?.putLong(KEY_UID, value)?.apply()

    var nickname: String
        get() = prefs?.getString(KEY_NICKNAME, "") ?: ""
        set(value) = prefs?.edit()?.putString(KEY_NICKNAME, value)?.apply()

    var phone: String
        get() = prefs?.getString(KEY_PHONE, "") ?: ""
        set(value) = prefs?.edit()?.putString(KEY_PHONE, value)?.apply()

    fun isLoggedIn(): Boolean = cookie.isNotBlank() && uid > 0

    fun logout() {
        cookie = ""
        uid = 0L
        nickname = ""
    }
}

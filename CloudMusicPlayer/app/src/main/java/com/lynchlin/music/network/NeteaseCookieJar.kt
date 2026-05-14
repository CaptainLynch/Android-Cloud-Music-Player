package com.lynchlin.music.network

import com.lynchlin.music.data.settings.NeteaseSettings
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class NeteaseCookieJar : CookieJar {

    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    init {
        loadFromSettings()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies.toMutableList()
        persistCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: cookieStore.values.flatten()
    }

    fun clear() {
        cookieStore.clear()
    }

    private fun persistCookies() {
        val allCookies = cookieStore.values.flatten()
        val cookieStr = allCookies.joinToString("; ") { "${it.name}=${it.value}" }
        if (cookieStr.isNotBlank()) {
            NeteaseSettings.cookie = cookieStr
        }
    }

    private fun loadFromSettings() {
        val savedCookie = NeteaseSettings.cookie
        if (savedCookie.isNotBlank()) {
            val pairs = savedCookie.split(";").map { it.trim() }
            val cookies = pairs.mapNotNull { pair ->
                val eqIdx = pair.indexOf('=')
                if (eqIdx > 0) {
                    val name = pair.substring(0, eqIdx)
                    val value = pair.substring(eqIdx + 1)
                    Cookie.Builder()
                        .name(name)
                        .value(value)
                        .domain("music.163.com")
                        .path("/")
                        .build()
                } else null
            }
            if (cookies.isNotEmpty()) {
                cookieStore["music.163.com"] = cookies.toMutableList()
            }
        }
    }
}

package com.lynchlin.music.network

import okhttp3.Cookie
import okhttp3.HttpUrl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NeteaseCookieJarTest {

    private lateinit var cookieJar: NeteaseCookieJar

    @Before
    fun setUp() {
        cookieJar = NeteaseCookieJar()
    }

    @Test
    fun `loadForRequest returns empty when no cookies saved`() {
        val url = HttpUrl.Builder().scheme("https").host("music.163.com").build()
        val cookies = cookieJar.loadForRequest(url)
        assertTrue(cookies.isEmpty())
    }

    @Test
    fun `saveFromResponse and loadForRequest round-trip`() {
        val url = HttpUrl.Builder().scheme("https").host("music.163.com").build()
        val cookie = Cookie.Builder()
            .name("MUSIC_U")
            .value("test_cookie_value")
            .domain("music.163.com")
            .path("/")
            .build()
        cookieJar.saveFromResponse(url, listOf(cookie))
        val loaded = cookieJar.loadForRequest(url)
        assertEquals(1, loaded.size)
        assertEquals("MUSIC_U", loaded[0].name)
        assertEquals("test_cookie_value", loaded[0].value)
    }

    @Test
    fun `saveFromResponse replaces previous cookies for same host`() {
        val url = HttpUrl.Builder().scheme("https").host("music.163.com").build()
        val c1 = Cookie.Builder().name("OLD").value("old_value").domain("music.163.com").build()
        val c2 = Cookie.Builder().name("NEW").value("new_value").domain("music.163.com").build()

        cookieJar.saveFromResponse(url, listOf(c1))
        cookieJar.saveFromResponse(url, listOf(c2))

        val loaded = cookieJar.loadForRequest(url)
        assertEquals(1, loaded.size)
        assertEquals("NEW", loaded[0].name)
    }

    @Test
    fun `clear removes all cookies`() {
        val url = HttpUrl.Builder().scheme("https").host("music.163.com").build()
        val cookie = Cookie.Builder().name("TEST").value("val").domain("music.163.com").build()
        cookieJar.saveFromResponse(url, listOf(cookie))
        cookieJar.clear()
        val loaded = cookieJar.loadForRequest(url)
        assertTrue(loaded.isEmpty())
    }

    @Test
    fun `multiple cookies saved for same host`() {
        val url = HttpUrl.Builder().scheme("https").host("music.163.com").build()
        val c1 = Cookie.Builder().name("A").value("1").domain("music.163.com").build()
        val c2 = Cookie.Builder().name("B").value("2").domain("music.163.com").build()
        cookieJar.saveFromResponse(url, listOf(c1, c2))
        val loaded = cookieJar.loadForRequest(url)
        assertEquals(2, loaded.size)
        val names = loaded.map { it.name }.toSet()
        assertTrue(names.containsAll(listOf("A", "B")))
    }
}

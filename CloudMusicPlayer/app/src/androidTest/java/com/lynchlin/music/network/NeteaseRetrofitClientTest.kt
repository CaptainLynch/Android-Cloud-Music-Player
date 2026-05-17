package com.lynchlin.music.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.lynchlin.music.data.settings.NeteaseSettings

@RunWith(AndroidJUnit4::class)
class NeteaseRetrofitClientTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NeteaseSettings.init(context)
    }

    @After
    fun tearDown() {
        NeteaseRetrofitClient.invalidate()
        NeteaseSettings.cookie = ""
        NeteaseSettings.uid = 0L
    }

    @Test
    fun `getProxyService returns non-null service`() {
        val service = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        assertNotNull(service)
    }

    @Test
    fun `getProxyService caches for same URL`() {
        val s1 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        val s2 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        assertSame(s1, s2)
    }

    @Test
    fun `getProxyService creates new service for different URL`() {
        val s1 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        val s2 = NeteaseRetrofitClient.getProxyService("http://192.168.1.1:3000")
        assertNotSame(s1, s2)
    }

    @Test
    fun `invalidate clears proxy service cache`() {
        val s1 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        NeteaseRetrofitClient.invalidate()
        val s2 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        assertNotSame(s1, s2)
    }

    @Test
    fun `proxy service URL with trailing slash is normalized`() {
        val s1 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000/")
        val s2 = NeteaseRetrofitClient.getProxyService("http://127.0.0.1:3000")
        assertSame(s1, s2)
    }
}

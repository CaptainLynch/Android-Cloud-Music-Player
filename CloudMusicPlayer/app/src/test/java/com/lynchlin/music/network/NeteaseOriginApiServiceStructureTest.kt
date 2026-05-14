package com.lynchlin.music.network

import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Method

class NeteaseOriginApiServiceStructureTest {

    @Test
    fun `interface defines loginCellphone endpoint`() {
        val method = findMethod("loginCellphone")
        assertNotNull("loginCellphone method should exist", method)
        val annotation = method!!.getAnnotation(retrofit2.http.POST::class.java)
        assertEquals("/weapi/login/cellphone", annotation.value)
    }

    @Test
    fun `interface defines loginRefresh endpoint`() {
        val method = findMethod("loginRefresh")
        assertNotNull("loginRefresh method should exist", method)
        assertEquals("/weapi/login/refresh", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `interface defines userPlaylist endpoint`() {
        val method = findMethod("userPlaylist")
        assertNotNull("userPlaylist method should exist", method)
        assertEquals("/weapi/user/playlist", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `interface defines playlistDetail endpoint`() {
        val method = findMethod("playlistDetail")
        assertNotNull("playlistDetail method should exist", method)
        assertEquals("/weapi/v6/playlist/detail", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `interface defines dailyRecommendSongs endpoint`() {
        val method = findMethod("dailyRecommendSongs")
        assertNotNull("dailyRecommendSongs method should exist", method)
        assertEquals("/weapi/v1/discovery/recommend/songs", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `interface defines personalizedPrivateContent endpoint`() {
        val method = findMethod("personalizedPrivateContent")
        assertNotNull("personalizedPrivateContent method should exist", method)
        assertEquals("/weapi/personalized/privatecontent", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `interface defines songUrl endpoint`() {
        val method = findMethod("songUrl")
        assertNotNull("songUrl method should exist", method)
        assertEquals("/weapi/song/enhance/player/url/v1", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `interface defines lyric endpoint`() {
        val method = findMethod("lyric")
        assertNotNull("lyric method should exist", method)
        assertEquals("/weapi/song/lyric", method!!.getAnnotation(retrofit2.http.POST::class.java).value)
    }

    @Test
    fun `all methods have POST annotation`() {
        val methods = NeteaseOriginApiService::class.java.declaredMethods
        methods.forEach { method ->
            assertNotNull("${method.name} should have @POST annotation", method.getAnnotation(retrofit2.http.POST::class.java))
        }
    }

    private fun findMethod(name: String): Method? {
        return NeteaseOriginApiService::class.java.declaredMethods.find { it.name == name }
    }
}

package com.lynchlin.music.data.model

import org.junit.Assert.*
import org.junit.Test

class SongTest {

    @Test
    fun `create song with required fields`() {
        val song = Song(
            id = 1L,
            name = "Test Song",
            artist = null,
            album = null,
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        assertEquals(1L, song.id)
        assertEquals("Test Song", song.name)
    }

    @Test
    fun `create song with all fields`() {
        val song = Song(
            id = 100L,
            name = "Full Song",
            artist = listOf("Artist1", "Artist2"),
            album = "Album Name",
            picId = "pic_001",
            urlId = "url_001",
            lyricId = "lyr_001",
            source = "netease"
        )
        assertEquals(100L, song.id)
        assertEquals("Full Song", song.name)
        assertEquals(listOf("Artist1", "Artist2"), song.artist)
        assertEquals("Album Name", song.album)
        assertEquals("pic_001", song.picId)
        assertEquals("url_001", song.urlId)
        assertEquals("lyr_001", song.lyricId)
        assertEquals("netease", song.source)
    }

    @Test
    fun `song copy produces new instance`() {
        val original = Song(1L, "Original", listOf("A"), "Album", null, null, null, null)
        val copy = original.copy(name = "Copy")
        assertEquals(1L, copy.id)
        assertEquals("Copy", copy.name)
        assertNotEquals(original, copy)
    }

    @Test
    fun `duplicate songs are equal`() {
        val s1 = Song(1L, "Song", listOf("Artist"), null, null, null, null, null)
        val s2 = Song(1L, "Song", listOf("Artist"), null, null, null, null, null)
        assertEquals(s1, s2)
        assertEquals(s1.hashCode(), s2.hashCode())
    }
}

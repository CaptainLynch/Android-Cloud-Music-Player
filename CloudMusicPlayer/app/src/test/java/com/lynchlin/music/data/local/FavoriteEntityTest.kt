package com.lynchlin.music.data.local

import org.junit.Assert.*
import org.junit.Test

class FavoriteEntityTest {

    @Test
    fun `create entity with required fields`() {
        val entity = FavoriteEntity(
            songId = 123L,
            name = "Test Song",
            artistJson = null,
            album = null,
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        assertEquals(123L, entity.songId)
        assertEquals("Test Song", entity.name)
        assertNull(entity.artistJson)
        assertNull(entity.album)
    }

    @Test
    fun `create entity with all fields`() {
        val entity = FavoriteEntity(
            songId = 456L,
            name = "Full Song",
            artistJson = "[\"Artist1\",\"Artist2\"]",
            album = "Test Album",
            picId = "pic123",
            urlId = "url456",
            lyricId = "lyr789",
            source = "netease"
        )
        assertEquals(456L, entity.songId)
        assertEquals("Full Song", entity.name)
        assertEquals("[\"Artist1\",\"Artist2\"]", entity.artistJson)
        assertEquals("Test Album", entity.album)
        assertEquals("pic123", entity.picId)
        assertEquals("url456", entity.urlId)
        assertEquals("lyr789", entity.lyricId)
        assertEquals("netease", entity.source)
    }

    @Test
    fun `addedAt defaults to current time`() {
        val before = System.currentTimeMillis()
        val entity = FavoriteEntity(
            songId = 1L,
            name = "Test",
            artistJson = null,
            album = null,
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        val after = System.currentTimeMillis()
        assertTrue(entity.addedAt in before..after)
    }

    @Test
    fun `entities with different songId are not equal`() {
        val e1 = FavoriteEntity(
            songId = 1L,
            name = "A",
            artistJson = null,
            album = null,
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        val e2 = FavoriteEntity(
            songId = 2L,
            name = "A",
            artistJson = null,
            album = null,
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        assertNotEquals(e1, e2)
    }

    @Test
    fun `entities with same songId and fields are equal`() {
        val e1 = FavoriteEntity(
            songId = 1L,
            name = "A",
            artistJson = "[]",
            album = "B",
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        val e2 = FavoriteEntity(
            songId = 1L,
            name = "A",
            artistJson = "[]",
            album = "B",
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        assertEquals(e1, e2)
        assertEquals(e1.hashCode(), e2.hashCode())
    }

    @Test
    fun `artistJson stores JSON array of strings`() {
        val json = "[\"Singer A\",\"Singer B\"]"
        val entity = FavoriteEntity(
            songId = 1L,
            name = "Song",
            artistJson = json,
            album = null,
            picId = null,
            urlId = null,
            lyricId = null,
            source = null
        )
        assertEquals(json, entity.artistJson)
    }
}

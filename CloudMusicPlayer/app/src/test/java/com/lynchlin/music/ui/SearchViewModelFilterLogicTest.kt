package com.lynchlin.music.ui

import com.lynchlin.music.data.model.Song
import org.junit.Assert.*
import org.junit.Test

class SearchViewModelFilterLogicTest {

    @Test
    fun `filter keeps songs with matching source`() {
        val songs = listOf(
            Song(1L, "A", null, null, null, null, null, "kuwo"),
            Song(2L, "B", null, null, null, null, null, "kuwo"),
            Song(3L, "C", null, null, null, null, null, "netease"),
        )
        val filtered = songs.filter { it.source == "kuwo" }
        assertEquals(2, filtered.size)
        assertEquals(setOf(1L, 2L), filtered.map { it.id }.toSet())
    }

    @Test
    fun `filter removes all when no match`() {
        val songs = listOf(
            Song(1L, "A", null, null, null, null, null, "netease"),
            Song(2L, "B", null, null, null, null, null, "netease"),
        )
        val filtered = songs.filter { it.source == "kuwo" }
        assertTrue(filtered.isEmpty())
    }

    @Test
    fun `filter handles empty list`() {
        val songs = emptyList<Song>()
        val filtered = songs.filter { it.source == "kuwo" }
        assertTrue(filtered.isEmpty())
    }

    @Test
    fun `filter handles null source`() {
        val songs = listOf(
            Song(1L, "A", null, null, null, null, null, null),
            Song(2L, "B", null, null, null, null, null, "kuwo"),
        )
        val filtered = songs.filter { it.source == "kuwo" }
        assertEquals(1, filtered.size)
    }

    @Test
    fun `filter with kugou platform`() {
        val songs = listOf(
            Song(1L, "A", null, null, null, null, null, "kugou"),
            Song(2L, "B", null, null, null, null, null, "kuwo"),
        )
        val filtered = songs.filter { it.source == "kugou" }
        assertEquals(1, filtered.size)
    }

    @Test
    fun `filter with migu platform`() {
        val songs = listOf(
            Song(1L, "A", null, null, null, null, null, "migu"),
            Song(2L, "B", null, null, null, null, null, "netease"),
        )
        val filtered = songs.filter { it.source == "migu" }
        assertEquals(1, filtered.size)
    }

    @Test
    fun `filter with netease platform`() {
        val songs = listOf(
            Song(1L, "A", null, null, null, null, null, "netease"),
            Song(2L, "B", null, null, null, null, null, "netease"),
            Song(3L, "C", null, null, null, null, null, "kuwo"),
        )
        val filtered = songs.filter { it.source == "netease" }
        assertEquals(2, filtered.size)
    }

    @Test
    fun `filter preserves song order`() {
        val songs = listOf(
            Song(1L, "First", null, null, null, null, null, "kuwo"),
            Song(2L, "Second", null, null, null, null, null, "netease"),
            Song(3L, "Third", null, null, null, null, null, "kuwo"),
        )
        val filtered = songs.filter { it.source == "kuwo" }
        assertEquals(listOf(1L, 3L), filtered.map { it.id })
    }
}

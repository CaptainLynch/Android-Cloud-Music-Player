package com.lynchlin.music.data.local

import org.junit.Assert.*
import org.junit.Test

class ConvertersTest {

    @Test
    fun `fromStringList converts list to JSON`() {
        val input = listOf("Artist1", "Artist2", "Artist3")
        val result = MusicTypeConverters().fromStringList(input)
        assertEquals("[\"Artist1\",\"Artist2\",\"Artist3\"]", result)
    }

    @Test
    fun `toStringList converts JSON to list`() {
        val json = "[\"Artist1\",\"Artist2\"]"
        val result = MusicTypeConverters().toStringList(json)
        assertEquals(listOf("Artist1", "Artist2"), result)
    }

    @Test
    fun `fromStringList handles null`() {
        val result = MusicTypeConverters().fromStringList(null)
        assertNull(result)
    }

    @Test
    fun `toStringList handles null`() {
        val result = MusicTypeConverters().toStringList(null)
        assertNull(result)
    }

    @Test
    fun `fromStringList handles empty list`() {
        val result = MusicTypeConverters().fromStringList(emptyList())
        assertEquals("[]", result)
    }

    @Test
    fun `toStringList handles empty JSON array`() {
        val result = MusicTypeConverters().toStringList("[]")
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `round-trip preserves data`() {
        val input = listOf("A", "B", "C", "D")
        val converter = MusicTypeConverters()
        val json = converter.fromStringList(input)
        val output = converter.toStringList(json)
        assertEquals(input, output)
    }
}

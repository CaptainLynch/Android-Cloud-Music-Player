package com.lynchlin.music.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lynchlin.music.data.model.Song
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesRepositoryTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FavoritesRepository.init(context)
        clearAllFavorites()
    }

    private fun clearAllFavorites() {
        val songs = FavoritesRepository.favoriteSongs.value.toList()
        songs.forEach { FavoritesRepository.toggleFavorite(it) }
    }

    @Test
    fun `init sets up empty favorites`() {
        assertTrue(FavoritesRepository.favoriteSongs.value.isEmpty())
        assertTrue(FavoritesRepository.favoriteIds.value.isEmpty())
    }

    @Test
    fun `toggleFavorite adds song when not present`() {
        val song = Song(1L, "Test Song", listOf("Artist"), null, null, null, null, "netease")
        val result = FavoritesRepository.toggleFavorite(song)
        assertTrue(result)
        assertTrue(FavoritesRepository.isFavorite(1L))
        assertEquals(1, FavoritesRepository.favoriteSongs.value.size)
    }

    @Test
    fun `toggleFavorite removes song when present`() {
        val song = Song(1L, "Test Song", listOf("Artist"), null, null, null, null, "netease")
        FavoritesRepository.toggleFavorite(song)
        val result = FavoritesRepository.toggleFavorite(song)
        assertFalse(result)
        assertFalse(FavoritesRepository.isFavorite(1L))
        assertTrue(FavoritesRepository.favoriteSongs.value.isEmpty())
    }

    @Test
    fun `isFavorite returns false for unknown song`() {
        assertFalse(FavoritesRepository.isFavorite(999L))
    }

    @Test
    fun `favoriteIds reflects toggles`() {
        val s1 = Song(1L, "S1", null, null, null, null, null, "netease")
        val s2 = Song(2L, "S2", null, null, null, null, null, "netease")
        FavoritesRepository.toggleFavorite(s1)
        FavoritesRepository.toggleFavorite(s2)
        assertEquals(setOf(1L, 2L), FavoritesRepository.favoriteIds.value)
        FavoritesRepository.toggleFavorite(s1)
        assertEquals(setOf(2L), FavoritesRepository.favoriteIds.value)
    }

    @Test
    fun `favoriteSongs maintains insertion order`() {
        val s1 = Song(1L, "First", null, null, null, null, null, "netease")
        val s2 = Song(2L, "Second", null, null, null, null, null, "netease")
        FavoritesRepository.toggleFavorite(s1)
        FavoritesRepository.toggleFavorite(s2)
        assertEquals(listOf("Second", "First"), FavoritesRepository.favoriteSongs.value.map { it.name })
    }

    @Test
    fun `multiple toggle cycles are idempotent`() {
        val song = Song(1L, "Test", null, null, null, null, null, null)
        FavoritesRepository.toggleFavorite(song)
        FavoritesRepository.toggleFavorite(song)
        FavoritesRepository.toggleFavorite(song)
        assertTrue(FavoritesRepository.isFavorite(1L))
        assertEquals(1, FavoritesRepository.favoriteSongs.value.size)
    }

    @Test
    fun `favorites survive multiple toggles`() {
        val songs = (1L..10L).map { Song(it, "Song $it", null, null, null, null, null, null) }
        songs.forEach { FavoritesRepository.toggleFavorite(it) }
        assertEquals(10, FavoritesRepository.favoriteSongs.value.size)

        songs.take(5).forEach { FavoritesRepository.toggleFavorite(it) }
        assertEquals(5, FavoritesRepository.favoriteSongs.value.size)

        val remainingIds = FavoritesRepository.favoriteIds.value
        songs.drop(5).forEach { assertTrue(it.id in remainingIds) }
    }
}

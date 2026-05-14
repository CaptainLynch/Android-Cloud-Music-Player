package com.lynchlin.music.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.favoriteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insert and get by id`() = runBlocking {
        val entity = FavoriteEntity(songId = 1L, name = "Song 1")
        dao.insert(entity)
        val result = dao.getBySongId(1L)
        assertNotNull(result)
        assertEquals("Song 1", result?.name)
    }

    @Test
    fun `getAll returns empty initially`() = runBlocking {
        val all = dao.getAll().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `getAll returns inserted entities ordered by addedAt DESC`() = runBlocking {
        val e1 = FavoriteEntity(songId = 1L, name = "First", addedAt = 1000L)
        val e2 = FavoriteEntity(songId = 2L, name = "Second", addedAt = 2000L)
        dao.insert(e1)
        dao.insert(e2)
        val all = dao.getAll().first()
        assertEquals(2, all.size)
        assertEquals("Second", all[0].name)
        assertEquals("First", all[1].name)
    }

    @Test
    fun `insert with REPLACE strategy overwrites existing`() = runBlocking {
        val e1 = FavoriteEntity(songId = 1L, name = "Original")
        dao.insert(e1)
        val e2 = FavoriteEntity(songId = 1L, name = "Updated")
        dao.insert(e2)
        val result = dao.getBySongId(1L)
        assertEquals("Updated", result?.name)
        val all = dao.getAll().first()
        assertEquals(1, all.size)
    }

    @Test
    fun `delete by entity removes record`() = runBlocking {
        val entity = FavoriteEntity(songId = 1L, name = "To Delete")
        dao.insert(entity)
        dao.delete(entity)
        val result = dao.getBySongId(1L)
        assertNull(result)
    }

    @Test
    fun `deleteBySongId removes record`() = runBlocking {
        val entity = FavoriteEntity(songId = 1L, name = "To Delete")
        dao.insert(entity)
        dao.deleteBySongId(1L)
        val result = dao.getBySongId(1L)
        assertNull(result)
    }

    @Test
    fun `isFavorite returns true when inserted`() = runBlocking {
        val entity = FavoriteEntity(songId = 1L, name = "Fav")
        dao.insert(entity)
        val isFav = dao.isFavorite(1L).first()
        assertTrue(isFav)
    }

    @Test
    fun `isFavorite returns false for non-existent`() = runBlocking {
        val isFav = dao.isFavorite(999L).first()
        assertFalse(isFav)
    }

    @Test
    fun `isFavorite emits false after delete`() = runBlocking {
        val entity = FavoriteEntity(songId = 1L, name = "Fav")
        dao.insert(entity)
        assertTrue(dao.isFavorite(1L).first())
        dao.deleteBySongId(1L)
        assertFalse(dao.isFavorite(1L).first())
    }

    @Test
    fun `insert multiple and verify count`() = runBlocking {
        (1L..5L).forEach { id ->
            dao.insert(FavoriteEntity(songId = id, name = "Song $id"))
        }
        val all = dao.getAll().first()
        assertEquals(5, all.size)
    }

    @Test
    fun `getBySongId returns correct entity from multiple`() = runBlocking {
        dao.insert(FavoriteEntity(songId = 1L, name = "A"))
        dao.insert(FavoriteEntity(songId = 2L, name = "B"))
        dao.insert(FavoriteEntity(songId = 3L, name = "C"))
        val result = dao.getBySongId(2L)
        assertEquals("B", result?.name)
    }

    @Test
    fun `getBySongId returns null for non-existent`() = runBlocking {
        val result = dao.getBySongId(999L)
        assertNull(result)
    }

    @Test
    fun `deleteBySongId only deletes target`() = runBlocking {
        dao.insert(FavoriteEntity(songId = 1L, name = "Keep"))
        dao.insert(FavoriteEntity(songId = 2L, name = "Delete"))
        dao.deleteBySongId(2L)
        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("Keep", all[0].name)
    }
}

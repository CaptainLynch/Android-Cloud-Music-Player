package com.lynchlin.music.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `database builds successfully`() {
        assertNotNull(db)
    }

    @Test
    fun `favoriteDao is not null`() {
        assertNotNull(db.favoriteDao())
    }

    @Test
    fun `singleton getInstance returns same instance`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Note: AppDatabase uses applicationContext internally
        val db1 = AppDatabase.getInstance(context)
        val db2 = AppDatabase.getInstance(context)
        assertSame(db1, db2)
    }
}

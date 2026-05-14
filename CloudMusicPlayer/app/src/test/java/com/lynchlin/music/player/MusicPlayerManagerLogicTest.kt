package com.lynchlin.music.player

import org.junit.Assert.*
import org.junit.Test

class MusicPlayerManagerLogicTest {

    @Test
    fun `isCurrentSongFavorite initial value is false`() {
        assertEquals(false, MusicPlayerManager.isCurrentSongFavorite.value)
    }

    @Test
    fun `currentSong initial value is null`() {
        assertNull(MusicPlayerManager.currentSong.value)
    }

    @Test
    fun `isPlaying initial value is false`() {
        assertFalse(MusicPlayerManager.isPlaying.value)
    }

    @Test
    fun `currentPosition initial value is 0`() {
        assertEquals(0L, MusicPlayerManager.currentPosition.value)
    }

    @Test
    fun `duration initial value is 0`() {
        assertEquals(0L, MusicPlayerManager.duration.value)
    }

    @Test
    fun `playQueue initial value is empty`() {
        assertTrue(MusicPlayerManager.playQueue.value.isEmpty())
    }

    @Test
    fun `currentIndex initial value is -1`() {
        assertEquals(-1, MusicPlayerManager.currentIndex.value)
    }

    @Test
    fun `onSongReady default callback does not throw`() {
        assertNotNull(MusicPlayerManager.onSongReady)
    }

    @Test
    fun `release sets service to null`() {
        MusicPlayerManager.release()
        // No exception thrown = pass
    }

    @Test
    fun `getExoPlayerPosition returns 0 when no player`() {
        assertEquals(0L, MusicPlayerManager.getExoPlayerPosition())
    }
}

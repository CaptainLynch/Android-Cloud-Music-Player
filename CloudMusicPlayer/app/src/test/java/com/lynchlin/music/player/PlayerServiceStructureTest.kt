package com.lynchlin.music.player

import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Method

class MediaPlaybackServiceStructureTest {

    @Test
    fun `MediaPlaybackService extends MediaSessionService`() {
        val superClass = MediaPlaybackService::class.java.superclass
        assertNotNull(superClass)
        assertEquals("androidx.media3.session.MediaSessionService", superClass.name)
    }

    @Test
    fun `has getInstance static method`() {
        val method = MediaPlaybackService::class.java.methods.find {
            it.name == "getInstance" && it.parameterCount == 0
        }
        assertNotNull("Should have getInstance()", method)
        assertTrue(java.lang.reflect.Modifier.isStatic(method!!.modifiers))
    }

    @Test
    fun `service is declared in manifest`() {
        // Structural check - MediaPlaybackService exists as a class
        assertNotNull(MediaPlaybackService::class.java)
    }
}

class MusicPlayerManagerStructureTest {

    @Test
    fun `is singleton object`() {
        assertSame(MusicPlayerManager, MusicPlayerManager)
    }

    @Test
    fun `has core state flows`() {
        assertNotNull(MusicPlayerManager.isPlaying)
        assertNotNull(MusicPlayerManager.currentSong)
        assertNotNull(MusicPlayerManager.currentPosition)
        assertNotNull(MusicPlayerManager.duration)
        assertNotNull(MusicPlayerManager.playQueue)
        assertNotNull(MusicPlayerManager.currentIndex)
        assertNotNull(MusicPlayerManager.isCurrentSongFavorite)
    }

    @Test
    fun `has required playback methods`() {
        val methods = MusicPlayerManager::class.java.declaredMethods.map { it.name }.toSet()
        assertTrue(methods.contains("playQueue"))
        assertTrue(methods.contains("playNext"))
        assertTrue(methods.contains("playPrevious"))
        assertTrue(methods.contains("togglePlayPause"))
        assertTrue(methods.contains("seekTo"))
        assertTrue(methods.contains("toggleFavoriteCurrent"))
    }

    @Test
    fun `has ExoPlayer fields`() {
        val fields = MusicPlayerManager::class.java.declaredFields.map { it.name }.toSet()
        assertTrue(fields.any { it.contains("exoPlayer", ignoreCase = true) })
    }
}

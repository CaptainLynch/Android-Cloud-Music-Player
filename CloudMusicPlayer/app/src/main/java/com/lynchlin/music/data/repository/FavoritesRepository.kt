package com.lynchlin.music.data.repository

import android.content.Context
import com.lynchlin.music.data.local.AppDatabase
import com.lynchlin.music.data.local.FavoriteDao
import com.lynchlin.music.data.local.FavoriteEntity
import com.lynchlin.music.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object FavoritesRepository {

    private var dao: FavoriteDao? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteIds: StateFlow<Set<Long>> = _favoriteIds.asStateFlow()

    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs: StateFlow<List<Song>> = _favoriteSongs.asStateFlow()

    fun init(context: Context) {
        if (dao != null) return
        val db = AppDatabase.getInstance(context)
        dao = db.favoriteDao()
        scope.launch {
            dao!!.getAll().collect { entities ->
                _favoriteIds.value = entities.map { it.songId }.toSet()
                _favoriteSongs.value = entities.map { it.toSong() }
            }
        }
    }

    fun isFavorite(songId: Long): Boolean = _favoriteIds.value.contains(songId)

    suspend fun toggleFavorite(song: Song) {
        val d = dao ?: return
        val existing = d.getBySongId(song.id)
        if (existing != null) {
            d.deleteBySongId(song.id)
        } else {
            d.insert(FavoriteEntity.fromSong(song))
        }
    }
}

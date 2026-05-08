package com.lynchlin.music.network

import com.lynchlin.music.data.model.AlbumArtResponse
import com.lynchlin.music.data.model.LyricResponse
import com.lynchlin.music.data.model.Song
import com.lynchlin.music.data.model.SongUrlResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {

    @GET("api.php")
    suspend fun searchMusic(
        @Query("types") types: String = "search",
        @Query("type") type: String = "netease",
        @Query("name") keyword: String
    ): List<Song>

    @GET("api.php")
    suspend fun getSongUrl(
        @Query("types") types: String = "url",
        @Query("id") id: String,
        @Query("source") source: String
    ): SongUrlResponse

    @GET("api.php")
    suspend fun getAlbumArt(
        @Query("types") types: String = "pic",
        @Query("id") id: String
    ): AlbumArtResponse

    @GET("api.php")
    suspend fun getLyric(
        @Query("types") types: String = "lyric",
        @Query("id") id: String
    ): LyricResponse
}

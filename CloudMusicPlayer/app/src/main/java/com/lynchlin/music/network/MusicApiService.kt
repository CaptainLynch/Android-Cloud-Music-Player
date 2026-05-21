package com.lynchlin.music.network

import com.lynchlin.music.data.model.AlbumArtResponse
import com.lynchlin.music.data.model.LyricResponse
import com.lynchlin.music.data.model.NeteaseSearchResponse
import com.lynchlin.music.data.model.NeteaseSongDetailResponse
import com.lynchlin.music.data.model.NeteaseSongUrlResponse
import com.lynchlin.music.data.model.Song
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {

    @GET("search")
    suspend fun searchMusic(
        @Query("keywords") keyword: String
    ): NeteaseSearchResponse

    @GET("song/url")
    suspend fun getSongUrl(
        @Query("id") id: String
    ): NeteaseSongUrlResponse

    @GET("song/detail")
    suspend fun getSongDetail(
        @Query("ids") id: String
    ): NeteaseSongDetailResponse

    @GET("lyric")
    suspend fun getLyric(
        @Query("id") id: String
    ): LyricResponse
}

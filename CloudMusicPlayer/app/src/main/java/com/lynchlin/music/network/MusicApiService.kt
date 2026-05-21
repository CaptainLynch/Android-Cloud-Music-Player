package com.lynchlin.music.network

import com.lynchlin.music.data.model.MetingSong
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {

    @GET("meting/api")
    suspend fun searchMusic(
        @Query("server") server: String = "netease",
        @Query("type") type: String = "search",
        @Query("id") keyword: String
    ): List<MetingSong>
}
package com.lynchlin.music.network

import com.lynchlin.music.data.model.Song
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {

    @GET("api.php")
    suspend fun searchMusic(
        @Query("types") types: String = "search",
        @Query("type") type: String = "netease",
        @Query("name") keyword: String
    ): List<Song>
}

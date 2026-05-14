package com.lynchlin.music.network

import com.lynchlin.music.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface NeteaseOriginApiService {

    @POST("/weapi/login/cellphone")
    suspend fun loginCellphone(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): LoginResponse

    @POST("/weapi/login/refresh")
    suspend fun loginRefresh(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): LoginResponse

    @POST("/weapi/user/playlist")
    suspend fun userPlaylist(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): UserPlaylistResponse

    @POST("/weapi/v6/playlist/detail")
    suspend fun playlistDetail(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): PlaylistDetailResponse

    @POST("/weapi/v1/discovery/recommend/songs")
    suspend fun dailyRecommendSongs(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): DailyRecommendResponse

    @POST("/weapi/personalized/privatecontent")
    suspend fun personalizedPrivateContent(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): PersonalizedResponse

    @POST("/weapi/song/enhance/player/url/v1")
    suspend fun songUrl(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): NeteaseSongUrlResponse

    @POST("/weapi/song/lyric")
    suspend fun lyric(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): NeteaseLyricResponse
}

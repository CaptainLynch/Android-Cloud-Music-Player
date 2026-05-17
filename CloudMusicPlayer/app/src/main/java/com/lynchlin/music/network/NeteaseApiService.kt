package com.lynchlin.music.network

import com.lynchlin.music.data.model.*
import com.lynchlin.music.data.model.IntelligenceResponse
import retrofit2.http.*

interface NeteaseApiService {

    // --- Auth ---

    @POST("login/cellphone")
    suspend fun loginCellphone(
        @Query("phone") phone: String,
        @Query("password") password: String,
        @Query("countrycode") countryCode: String = "86"
    ): LoginResponse

    @GET("login/status")
    suspend fun loginStatus(
        @Query("cookie") cookie: String
    ): LoginStatusResponse

    @GET("login/refresh")
    suspend fun refreshLogin(
        @Query("cookie") cookie: String
    ): LoginResponse

    // --- Playlists ---

    @GET("user/playlist")
    suspend fun userPlaylist(
        @Query("uid") uid: Long,
        @Query("cookie") cookie: String
    ): UserPlaylistResponse

    @GET("playlist/detail")
    suspend fun playlistDetail(
        @Query("id") id: Long,
        @Query("cookie") cookie: String
    ): PlaylistDetailResponse

    @GET("playlist/track/all")
    suspend fun playlistAllTracks(
        @Query("id") id: Long,
        @Query("cookie") cookie: String,
        @Query("limit") limit: Int = 2000,
        @Query("offset") offset: Int = 0
    ): PlaylistDetailResponse

    @GET("likelist")
    suspend fun likeList(
        @Query("uid") uid: Long,
        @Query("cookie") cookie: String
    ): LikeListResponse

    // --- Daily Recommendations ---

    @GET("recommend/songs")
    suspend fun dailyRecommendSongs(
        @Query("cookie") cookie: String
    ): DailyRecommendResponse

    // --- Personalized ---

    @GET("personalized/privatecontent/list")
    suspend fun personalizedPrivateContentList(
        @Query("cookie") cookie: String
    ): PersonalizedResponse

    // --- Song URL & Lyric ---

    @GET("song/url/v1")
    suspend fun songUrl(
        @Query("id") id: Long,
        @Query("level") level: String = "standard",
        @Query("cookie") cookie: String
    ): NeteaseSongUrlResponse

    @GET("lyric")
    suspend fun lyric(
        @Query("id") id: Long,
        @Query("cookie") cookie: String
    ): NeteaseLyricResponse

    // --- Heartbeat / Intelligence Mode ---
    @GET("playmode/intelligence/list")
    suspend fun intelligenceList(
        @Query("id") id: Long,
        @Query("pid") pid: Long,
        @Query("sid") sid: Long? = null,
        @Query("count") count: Int? = null,
        @Query("cookie") cookie: String
    ): IntelligenceResponse
}

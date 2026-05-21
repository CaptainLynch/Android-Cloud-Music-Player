package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class DailyRecommendResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: DailyRecommendData?
)

data class DailyRecommendData(
    @SerializedName("dailySongs") val dailySongs: List<NeteasePlaylistTrack>?
)

data class PersonalizedResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("result") val result: List<PersonalizedItem>?
)

data class PersonalizedItem(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("picUrl") val picUrl: String?
)

data class NeteaseLyricResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("lrc") val lrc: NeteaseLrc?,
    @SerializedName("tlyric") val tlyric: NeteaseLrc?
)

data class NeteaseLrc(
    @SerializedName("lyric") val lyric: String?
)

data class IntelligenceResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: IntelligenceData?
)

data class IntelligenceData(
    @SerializedName("songs") val songs: List<NeteasePlaylistTrack>?
)

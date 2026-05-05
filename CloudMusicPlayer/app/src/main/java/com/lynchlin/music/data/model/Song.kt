package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("artist") val artist: String?,
    @SerializedName("album") val album: String?,
    @SerializedName("pic_id") val picId: String?,
    @SerializedName("url_id") val urlId: String?,
    @SerializedName("lyric_id") val lyricId: String?,
    @SerializedName("source") val source: String?
)

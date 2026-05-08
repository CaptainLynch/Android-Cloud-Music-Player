package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class LyricResponse(
    @SerializedName("lyric") val lyric: String?,
    @SerializedName("tlyric") val tlyric: String?
)

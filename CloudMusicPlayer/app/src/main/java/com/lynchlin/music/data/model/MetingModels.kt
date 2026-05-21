package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class MetingSong(
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("url") val url: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("lrc") val lrc: String
)
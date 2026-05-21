package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class NeteaseSearchResponse(
    @SerializedName("result") val result: NeteaseSearchResult?,
    @SerializedName("code") val code: Int
)

data class NeteaseSearchResult(
    @SerializedName("songs") val songs: List<NeteaseSong>?,
    @SerializedName("songCount") val songCount: Int
)

data class NeteaseSong(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<NeteaseArtist>?,
    @SerializedName("album") val album: NeteaseAlbum?,
    @SerializedName("duration") val duration: Long
)

data class NeteaseArtist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

data class NeteaseAlbum(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("picId") val picId: Long,
    @SerializedName("picUrl") val picUrl: String?
)

data class NeteaseSongUrlResponse(
    @SerializedName("data") val data: List<NeteaseSongUrl>?,
    @SerializedName("code") val code: Int
)

data class NeteaseSongUrl(
    @SerializedName("id") val id: Long,
    @SerializedName("url") val url: String?,
    @SerializedName("br") val br: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("code") val code: Int
)

data class NeteaseSongDetailResponse(
    @SerializedName("songs") val songs: List<NeteaseSongDetail>?,
    @SerializedName("code") val code: Int
)

data class NeteaseSongDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("al") val album: NeteaseAlbumDetail?
)

data class NeteaseAlbumDetail(
    @SerializedName("picUrl") val picUrl: String?
)
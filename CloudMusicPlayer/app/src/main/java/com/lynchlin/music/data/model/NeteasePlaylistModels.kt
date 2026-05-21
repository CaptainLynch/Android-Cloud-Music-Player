package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class UserPlaylistResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("playlist") val playlist: List<NeteasePlaylist>?
)

data class NeteasePlaylist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("coverImgUrl") val coverImgUrl: String?,
    @SerializedName("trackCount") val trackCount: Int
)

data class PlaylistDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("songs") val songs: List<NeteasePlaylistTrack>?
)

data class NeteasePlaylistTrack(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("ar") val artists: List<NeteaseTrackArtist>?,
    @SerializedName("al") val album: NeteaseTrackAlbum?
)

data class NeteaseTrackArtist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

data class NeteaseTrackAlbum(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("picUrl") val picUrl: String?
)

data class LikeListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("ids") val ids: List<Long>?
)

package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

// --- Login ---

data class LoginResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("cookie") val cookie: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("account") val account: AccountInfo?,
    @SerializedName("profile") val profile: ProfileInfo?
)

data class AccountInfo(
    @SerializedName("id") val id: Long,
    @SerializedName("userName") val userName: String?
)

data class ProfileInfo(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

// --- Login Status ---

data class LoginStatusResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: LoginStatusData? = null,
    @SerializedName("account") val account: AccountInfo? = null,
    @SerializedName("profile") val profile: ProfileInfo? = null
)

data class LoginStatusData(
    @SerializedName("code") val code: Int,
    @SerializedName("profile") val profile: ProfileInfo? = null,
    @SerializedName("account") val account: AccountInfo? = null
)

// --- User Playlists ---

data class UserPlaylistResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("playlist") val playlist: List<NeteasePlaylist>? = null
)

data class NeteasePlaylist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("coverImgUrl") val coverImgUrl: String?,
    @SerializedName("trackCount") val trackCount: Int = 0,
    @SerializedName("playCount") val playCount: Long = 0,
    @SerializedName("creator") val creator: PlaylistCreator? = null,
    @SerializedName("subscribedCount") val subscribedCount: Long = 0,
    @SerializedName("description") val description: String? = null,
    @SerializedName("specialType") val specialType: Int = 0
)

data class PlaylistCreator(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

// --- Playlist Detail ---

data class PlaylistDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("playlist") val playlist: PlaylistDetail? = null
)

data class PlaylistDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("coverImgUrl") val coverImgUrl: String?,
    @SerializedName("trackCount") val trackCount: Int = 0,
    @SerializedName("tracks") val tracks: List<NeteaseTrack>? = null,
    @SerializedName("trackIds") val trackIds: List<TrackIdItem>? = null
)

data class TrackIdItem(
    @SerializedName("id") val id: Long
)

// --- Track ---

data class NeteaseTrack(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("ar") val artists: List<NeteaseArtist>? = null,
    @SerializedName("al") val album: NeteaseAlbum? = null,
    @SerializedName("dt") val duration: Long = 0
) {
    fun toSong(): Song = Song(
        id = id,
        name = name,
        artist = artists?.map { it.name },
        album = album?.name,
        picId = null,
        urlId = null,
        lyricId = null,
        source = "netease_playlist"
    )

    fun albumPicUrl(): String? = album?.picUrl

    fun durationMs(): Long = duration
}

data class NeteaseArtist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

data class NeteaseAlbum(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("picUrl") val picUrl: String?
)

// --- Daily Recommendations ---

data class DailyRecommendResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: DailyRecommendData? = null
)

data class DailyRecommendData(
    @SerializedName("dailySongs") val dailySongs: List<NeteaseTrack>? = null
)

// --- Song URL ---

data class NeteaseSongUrlResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: List<NeteaseSongUrlItem>? = null
)

data class NeteaseSongUrlItem(
    @SerializedName("id") val id: Long,
    @SerializedName("url") val url: String?,
    @SerializedName("br") val br: Int = 0,
    @SerializedName("size") val size: Long = 0,
    @SerializedName("type") val type: String? = null
)

// --- Lyric ---

data class NeteaseLyricResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("lrc") val lrc: NeteaseLyricData? = null,
    @SerializedName("tlyric") val tlyric: NeteaseLyricData? = null
)

data class NeteaseLyricData(
    @SerializedName("lyric") val lyric: String? = null
)

// --- Like List ---

data class LikeListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("ids") val ids: List<Long>? = null
)

// --- Personalized / Private Radar ---

data class PersonalizedResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("result") val result: List<PersonalizedPlaylist>? = null
)

data class PersonalizedPlaylist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("picUrl") val picUrl: String?,
    @SerializedName("playCount") val playCount: Long = 0,
    @SerializedName("trackCount") val trackCount: Int = 0,
    @SerializedName("creator") val creator: PlaylistCreator? = null
)

// --- Heartbeat / Intelligence Mode ---
data class IntelligenceResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: IntelligenceData? = null
)

data class IntelligenceData(
    @SerializedName("songs") val songs: List<NeteaseTrack>? = null
)

// --- Search (new API) ---

data class NeteaseSearchResponse(
    @SerializedName("result") val result: NeteaseSearchResult?,
    @SerializedName("code") val code: Int
)

data class NeteaseSearchResult(
    @SerializedName("songs") val songs: List<NeteaseSearchSong>?,
    @SerializedName("songCount") val songCount: Int
)

data class NeteaseSearchSong(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<NeteaseSearchArtist>?,
    @SerializedName("album") val album: NeteaseSearchAlbum?,
    @SerializedName("duration") val duration: Long
)

data class NeteaseSearchArtist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)

data class NeteaseSearchAlbum(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("picId") val picId: Long,
    @SerializedName("picUrl") val picUrl: String?
)

data class NeteaseSongDetailResponse(
    @SerializedName("songs") val songs: List<NeteaseSongDetail>?,
    @SerializedName("code") val code: Int
)

data class NeteaseSongDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("al") val album: NeteaseSongDetailAlbum?
)

data class NeteaseSongDetailAlbum(
    @SerializedName("picUrl") val picUrl: String?
)
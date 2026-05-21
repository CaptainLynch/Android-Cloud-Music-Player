package com.lynchlin.music.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("cookie") val cookie: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("account") val account: NeteaseAccount?,
    @SerializedName("profile") val profile: NeteaseProfile?
)

data class LoginStatusResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: LoginStatusData?
)

data class LoginStatusData(
    @SerializedName("account") val account: NeteaseAccount?,
    @SerializedName("profile") val profile: NeteaseProfile?
)

data class NeteaseAccount(
    @SerializedName("id") val id: Long,
    @SerializedName("userName") val userName: String?
)

data class NeteaseProfile(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class UserInfo(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("birthday")
    val birthday: String? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("joined_at")
    val joinedAt: String? = null,

    @field:SerializedName("picture")
    val picture: String? = null,

    @field:SerializedName("time_zone")
    val timeZone: String? = null,

    @field:SerializedName("anime_statistics")
    val userAnimeStatistics: UserAnimeStatistics? = null,

    @field:SerializedName("is_supporter")
    val isSupporter: Boolean? = null,
)
package com.animegatari.hayanime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StartSeason(

    @field:SerializedName("year")
    val year: Int? = null,

    @field:SerializedName("season")
    val season: String? = null,
)
package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Picture(

    @field:SerializedName("large")
    val large: String? = null,

    @field:SerializedName("medium")
    val medium: String? = null,
)
package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class MainPicture(

    @field:SerializedName("medium")
    val medium: String? = null,

    @field:SerializedName("large")
    val large: String? = null,
)
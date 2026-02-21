package com.animegatari.hayanime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Studio(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,
)
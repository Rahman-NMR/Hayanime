package com.animegatari.hayanime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Node(

    @field:SerializedName("main_picture")
    val mainPicture: Picture? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,
)
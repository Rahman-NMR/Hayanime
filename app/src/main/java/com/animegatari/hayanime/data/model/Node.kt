package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Node(

    @field:SerializedName("main_picture")
    val mainPicture: MainPicture? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,
)
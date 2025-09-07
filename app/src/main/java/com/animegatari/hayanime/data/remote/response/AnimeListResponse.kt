package com.animegatari.hayanime.data.remote.response

import com.animegatari.hayanime.data.model.Anime
import com.google.gson.annotations.SerializedName

data class AnimeListResponse(

    @field:SerializedName("data")
    val data: List<AnimeList?>? = null,
)

data class AnimeList(

    @field:SerializedName("node")
    val node: Anime? = null,
)
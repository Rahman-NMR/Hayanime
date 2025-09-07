package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class AnimeMinimum(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("num_episodes")
    val numEpisodes: Int? = null,

    @field:SerializedName("start_season")
    val startSeason: StartSeason? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("end_date")
    val endDate: String? = null,

    @field:SerializedName("my_list_status")
    val myListStatus: MyListStatus? = null,
)
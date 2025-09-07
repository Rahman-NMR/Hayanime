package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Anime(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("main_picture")
    val mainPicture: MainPicture? = null,

    @field:SerializedName("media_type")
    val mediaType: String? = null,

    @field:SerializedName("mean")
    val mean: Float? = null,

    @field:SerializedName("num_scoring_users")
    val numScoringUsers: Int? = null,

    @field:SerializedName("start_season")
    val startSeason: StartSeason? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("nsfw")
    val nsfw: String? = null,

    @field:SerializedName("rating")
    val rating: String? = null,

    @field:SerializedName("genres")
    val genres: List<Genre?>? = null,

    @field:SerializedName("num_episodes")
    val numEpisodes: Int? = null,

    @field:SerializedName("average_episode_duration")
    val averageEpisodeDuration: Int? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("studios")
    val studios: List<Studio?>? = null,

    @field:SerializedName("my_list_status")
    val myListStatus: MyListStatus? = null,
)
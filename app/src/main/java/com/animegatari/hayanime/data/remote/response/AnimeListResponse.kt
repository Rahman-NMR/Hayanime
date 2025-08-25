package com.animegatari.hayanime.data.remote.response

import com.google.gson.annotations.SerializedName

data class AnimeListResponse(

    @field:SerializedName("data")
    val data: List<AnimeList?>? = null,
)

data class AnimeList(

    @field:SerializedName("node")
    val node: AnimeNode? = null,
)

data class AnimeNode(

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
    val genres: List<GenresItem?>? = null,

    @field:SerializedName("num_episodes")
    val numEpisodes: Int? = null,

    @field:SerializedName("average_episode_duration")
    val averageEpisodeDuration: Int? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("studios")
    val studios: List<StudiosItem?>? = null,

    @field:SerializedName("my_list_status")
    val myListStatus: MyListStatus? = null,
)

data class MainPicture(

    @field:SerializedName("medium")
    val medium: String? = null,

    @field:SerializedName("large")
    val large: String? = null,
)

data class StartSeason(

    @field:SerializedName("year")
    val year: Int? = null,

    @field:SerializedName("season")
    val season: String? = null,
)

data class GenresItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,
)

data class StudiosItem(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,
)

data class MyListStatus(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("num_episodes_watched")
    val numEpisodesWatched: Int? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("finish_date")
    val finishDate: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("score")
    val score: Int? = null,

    @field:SerializedName("is_rewatching")
    val isRewatching: Boolean? = null,
)
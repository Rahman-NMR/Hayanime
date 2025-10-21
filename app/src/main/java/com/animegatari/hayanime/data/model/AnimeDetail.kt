package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class AnimeDetail(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("main_picture")
    val mainPicture: Picture? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("alternative_titles")
    val alternativeTitles: AlternativeTitles? = null,

    @field:SerializedName("media_type")
    val mediaType: String? = null,

    @field:SerializedName("synopsis")
    val synopsis: String? = null,

    @field:SerializedName("background")
    val background: String? = null,

    @field:SerializedName("start_season")
    val startSeason: StartSeason? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("end_date")
    val endDate: String? = null,

    @field:SerializedName("mean")
    val mean: Float? = null,

    @field:SerializedName("num_scoring_users")
    val numScoringUsers: Int? = null,

    @field:SerializedName("rank")
    val rank: Int? = null,

    @field:SerializedName("popularity")
    val popularity: Int? = null,

    @field:SerializedName("broadcast")
    val broadcast: Broadcast? = null,

    @field:SerializedName("nsfw")
    val nsfw: String? = null,

    @field:SerializedName("rating")
    val rating: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("num_episodes")
    val numEpisodes: Int? = null,

    @field:SerializedName("average_episode_duration")
    val averageEpisodeDuration: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("genres")
    val genres: List<Genre?>? = null,

    @field:SerializedName("studios")
    val studios: List<Studio?>? = null,

    @field:SerializedName("related_anime")
    val relatedAnime: List<RelatedAnime?>? = null,

    @field:SerializedName("recommendations")
    val recommendations: List<Recommendation?>? = null,

    @field:SerializedName("num_list_users")
    val numListUsers: Int? = null,

    @field:SerializedName("statistics")
    val statistics: Statistics? = null,

    @field:SerializedName("pictures")
    val pictures: List<Picture?>? = null,

    @field:SerializedName("my_list_status")
    val myListStatus: MyListStatus? = null,
)
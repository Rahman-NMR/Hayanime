package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class MyListStatus(

    @field:SerializedName("priority")
    val priority: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    // Used in GET requests for fetching the current status.
    @field:SerializedName("num_episodes_watched")
    val numEpisodesWatched: Int? = null,

    // Used in PATCH requests for updating the number of watched episodes.
    @field:SerializedName("num_watched_episodes")
    val numWatchedEpisodes: Int? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("finish_date")
    val finishDate: String? = null,

    @field:SerializedName("score")
    val score: Int? = null,

    @field:SerializedName("is_rewatching")
    val isRewatching: Boolean? = null,

    @field:SerializedName("num_times_rewatched")
    val numTimesRewatched: Int? = null,

    @field:SerializedName("rewatch_value")
    val rewatchValue: Int? = null,

    @field:SerializedName("tags")
    val tags: List<String?>? = null,

    @field:SerializedName("comments")
    val comments: String? = null,
)
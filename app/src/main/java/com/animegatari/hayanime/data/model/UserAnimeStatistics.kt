package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class UserAnimeStatistics(

    @field:SerializedName("num_items_watching")
    val numItemsWatching: Int? = null,

    @field:SerializedName("num_items_completed")
    val numItemsCompleted: Int? = null,

    @field:SerializedName("num_items_plan_to_watch")
    val numItemsPlanToWatch: Int? = null,

    @field:SerializedName("num_items_on_hold")
    val numItemsOnHold: Int? = null,

    @field:SerializedName("num_items_dropped")
    val numItemsDropped: Int? = null,

    @field:SerializedName("num_items")
    val numItems: Int? = null,

    @field:SerializedName("num_days_watched")
    val numDaysWatched: Double? = null,

    @field:SerializedName("num_days_watching")
    val numDaysWatching: Double? = null,

    @field:SerializedName("num_days_completed")
    val numDaysCompleted: Double? = null,

    @field:SerializedName("num_days_on_hold")
    val numDaysOnHold: Double? = null,

    @field:SerializedName("num_days_dropped")
    val numDaysDropped: Double? = null,

    @field:SerializedName("num_days")
    val numDays: Double? = null,

    @field:SerializedName("num_episodes")
    val numEpisodes: Int? = null,

    @field:SerializedName("num_times_rewatched")
    val numTimesRewatched: Int? = null,

    @field:SerializedName("mean_score")
    val meanScore: Double? = null,
)
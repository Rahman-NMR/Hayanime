package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Broadcast(

    @field:SerializedName("day_of_the_week")
    val dayOfTheWeek: String? = null,

    @field:SerializedName("start_time")
    val startTime: String? = null,
)
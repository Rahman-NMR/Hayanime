package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Status(

    @field:SerializedName("plan_to_watch")
    val planToWatch: String? = null,

    @field:SerializedName("dropped")
    val dropped: String? = null,

    @field:SerializedName("completed")
    val completed: String? = null,

    @field:SerializedName("on_hold")
    val onHold: String? = null,

    @field:SerializedName("watching")
    val watching: String? = null,
)
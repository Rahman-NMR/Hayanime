package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Statistics(

    @field:SerializedName("num_list_users")
    val numListUsers: Int? = null,

    @field:SerializedName("status")
    val status: Status? = null,
)
package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class Recommendation(

    @field:SerializedName("node")
    val node: Node? = null,

    @field:SerializedName("num_recommendations")
    val numRecommendations: Int? = null,
)
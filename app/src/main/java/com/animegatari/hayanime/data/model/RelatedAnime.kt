package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class RelatedAnime(

    @field:SerializedName("node")
    val node: Node? = null,

    @field:SerializedName("relation_type_formatted")
    val relationTypeFormatted: String? = null,

    @field:SerializedName("relation_type")
    val relationType: String? = null,
)
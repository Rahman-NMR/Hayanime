package com.animegatari.hayanime.data.model

import com.google.gson.annotations.SerializedName

data class AlternativeTitles(

    @field:SerializedName("synonyms")
    val synonyms: List<String?>? = null,

    @field:SerializedName("ja")
    val ja: String? = null,

    @field:SerializedName("en")
    val en: String? = null,
)
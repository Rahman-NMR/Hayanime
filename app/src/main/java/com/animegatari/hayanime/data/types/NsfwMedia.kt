package com.animegatari.hayanime.data.types

enum class NsfwMedia(val apiValue: String? = null) {
    WHITE("white"),
    GRAY("gray"),
    BLACK("black"),
    UNKNOWN();

    companion object {
        fun fromApiValue(apiValue: String?): NsfwMedia {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
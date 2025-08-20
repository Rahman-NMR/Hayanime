package com.animegatari.hayanime.data.types

enum class NsfwMedia() {
    WHITE(),
    GRAY(),
    BLACK(),
    UNKNOWN();

    companion object {
        fun fromApiValue(apiValue: String?): NsfwMedia {
            return when (apiValue?.lowercase()) {
                "white" -> WHITE
                "gray" -> GRAY
                "black" -> BLACK
                else -> UNKNOWN
            }
        }
    }
}
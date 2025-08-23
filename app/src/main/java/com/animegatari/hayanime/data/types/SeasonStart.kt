package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class SeasonStart(@StringRes val stringResId: Int, val apiValue: String) {
    WINTER(R.string.season_winter, "winter"),
    SPRING(R.string.season_spring, "spring"),
    SUMMER(R.string.season_summer, "summer"),
    FALL(R.string.season_fall, "fall"),
    UNKNOWN(R.string.label_unknown, "");

    companion object {
        fun fromApiValue(apiValue: String?): SeasonStart {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
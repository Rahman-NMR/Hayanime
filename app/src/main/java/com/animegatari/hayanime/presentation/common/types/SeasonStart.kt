package com.animegatari.hayanime.presentation.common.types

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.animegatari.hayanime.R
import com.animegatari.hayanime.presentation.common.base.ChipInfoProvider

enum class SeasonStart(
    @StringRes override val stringResId: Int,
    val apiValue: String,
    @DrawableRes override val iconResId: Int,
) : ChipInfoProvider {
    WINTER(R.string.season_winter, "winter", R.drawable.ic_mode_cool_24px_rounded),
    SPRING(R.string.season_spring, "spring", R.drawable.ic_nest_eco_leaf_24px_rounded),
    SUMMER(R.string.season_summer, "summer", R.drawable.ic_sunny_24px_rounded),
    FALL(R.string.season_fall, "fall", R.drawable.ic_rainy_24px_rounded),
    UNKNOWN(R.string.label_unknown, "", R.drawable.ic_question_mark_24px_rounded);

    override val uniqueValue: String
        get() = apiValue

    companion object {
        fun fromApiValue(apiValue: String?): SeasonStart {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
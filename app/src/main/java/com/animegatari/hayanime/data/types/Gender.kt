package com.animegatari.hayanime.data.types

import androidx.annotation.DrawableRes
import com.animegatari.hayanime.R

enum class Gender(val apiValue: String? = null, @DrawableRes val iconResId: Int) {
    MALE("male", R.drawable.ic_male_24px_rounded),
    FEMALE("female", R.drawable.ic_female_24px_rounded),
    UNKNOWN(null, 0);

    companion object {
        fun fromApiValue(apiValue: String?): Gender {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
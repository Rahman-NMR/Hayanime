package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class RatingCategory(@StringRes val stringResId: Int, val apiValue: String? = null) {
    G(R.string.rating_g, "g"),
    PG(R.string.rating_pg, "pg"),
    PG_13(R.string.rating_pg_13, "pg_13"),
    R_17(R.string.rating_r, "r"),
    R_PLUS(R.string.rating_r_plus, "r+"),
    RX(R.string.rating_rx, "rx"),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): RatingCategory {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
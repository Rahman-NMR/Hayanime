package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class RatingCategory(@StringRes val stringResId: Int) {
    G(R.string.rating_g),
    PG(R.string.rating_pg),
    PG_13(R.string.rating_pg_13),
    R_17(R.string.rating_r),
    R_PLUS(R.string.rating_r_plus),
    RX(R.string.rating_rx),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): RatingCategory {
            return when (apiValue?.lowercase()) {
                "g" -> G
                "pg" -> PG
                "pg_13" -> PG_13
                "r" -> R_17
                "r+" -> R_PLUS
                "rx" -> RX
                else -> UNKNOWN
            }
        }
    }
}
package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class MediaType(@StringRes val stringResId: Int) {
    TV(R.string.media_type_tv),
    OVA(R.string.media_type_ova),
    MOVIE(R.string.media_type_movie),
    SPECIAL(R.string.media_type_special),
    ONA(R.string.media_type_ona),
    MUSIC(R.string.media_type_music),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): MediaType {
            return when (apiValue?.lowercase()) {
                "tv" -> TV
                "ova" -> OVA
                "movie" -> MOVIE
                "special" -> SPECIAL
                "ona" -> ONA
                "music" -> MUSIC
                else -> UNKNOWN
            }
        }
    }
}
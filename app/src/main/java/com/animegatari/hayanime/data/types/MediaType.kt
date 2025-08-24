package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class MediaType(@StringRes val stringResId: Int, val apiValue: String? = null) {
    TV(R.string.media_type_tv, "tv"),
    OVA(R.string.media_type_ova, "ova"),
    MOVIE(R.string.media_type_movie, "movie"),
    SPECIAL(R.string.media_type_special, "special"),
    ONA(R.string.media_type_ona, "ona"),
    MUSIC(R.string.media_type_music, "music"),
    TV_SPECIAL(R.string.media_type_tv_special, "tv_special"),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): MediaType {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
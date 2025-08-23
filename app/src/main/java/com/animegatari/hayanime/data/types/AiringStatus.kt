package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class AiringStatus(@StringRes val stringResId: Int, val apiValue: String? = null) {
    FINISHED_AIRING(R.string.airing_status_finished_airing, "finished_airing"),
    CURRENTLY_AIRING(R.string.airing_status_currently_airing, "currently_airing"),
    NOT_YET_AIRED(R.string.airing_status_not_yet_aired, "not_yet_aired"),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): AiringStatus {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
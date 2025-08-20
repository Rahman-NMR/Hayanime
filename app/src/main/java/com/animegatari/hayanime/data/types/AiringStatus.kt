package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class AiringStatus(@StringRes val stringResId: Int) {
    FINISHED_AIRING(R.string.airing_status_finished_airing),
    CURRENTLY_AIRING(R.string.airing_status_currently_airing),
    NOT_YET_AIRED(R.string.airing_status_not_yet_aired),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): AiringStatus {
            return when (apiValue?.lowercase()) {
                "finished_airing" -> FINISHED_AIRING
                "currently_airing" -> CURRENTLY_AIRING
                "not_yet_aired" -> NOT_YET_AIRED
                else -> UNKNOWN
            }
        }
    }
}
package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class WatchingStatus(@StringRes val stringResId: Int) {
    WATCHING(R.string.watching_status_watching),
    COMPLETED(R.string.watching_status_completed),
    PLAN_TO_WATCH(R.string.watching_status_plan_to_watch),
    ON_HOLD(R.string.watching_status_on_hold),
    DROPPED(R.string.watching_status_dropped),
    ALL_ANIME(R.string.watching_status_all_anime),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): WatchingStatus {
            return when (apiValue?.lowercase()) {
                "watching" -> WATCHING
                "completed" -> COMPLETED
                "plan_to_watch" -> PLAN_TO_WATCH
                "on_hold" -> ON_HOLD
                "dropped" -> DROPPED
                else -> UNKNOWN
            }
        }
    }
}
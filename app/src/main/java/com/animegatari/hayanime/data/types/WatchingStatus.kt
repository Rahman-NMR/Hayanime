package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class WatchingStatus(@StringRes val stringResId: Int, val apiValue: String) {
    WATCHING(R.string.watching_status_watching, "watching"),
    COMPLETED(R.string.watching_status_completed, "completed"),
    PLAN_TO_WATCH(R.string.watching_status_plan_to_watch, "plan_to_watch"),
    ON_HOLD(R.string.watching_status_on_hold, "on_hold"),
    DROPPED(R.string.watching_status_dropped, "dropped"),
    ALL_ANIME(R.string.watching_status_all_anime, "all_anime"),
    UNKNOWN(R.string.label_no_status, "unknown");

    companion object {
        fun fromApiValue(apiValue: String?): WatchingStatus {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
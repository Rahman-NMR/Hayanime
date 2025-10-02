package com.animegatari.hayanime.data.types

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class WatchingStatus(
    @StringRes val stringResId: Int,
    val apiValue: String,
    @ColorRes val colorResId: Int,
    @ColorRes val onColorResId: Int,
) {
    WATCHING(R.string.watching_status_watching, "watching", R.color.colorWatchingColor, R.color.colorOnWatchingColor),
    COMPLETED(R.string.watching_status_completed, "completed", R.color.colorCompleteColor, R.color.colorOnCompleteColor),
    PLAN_TO_WATCH(R.string.watching_status_plan_to_watch, "plan_to_watch", R.color.md_theme_secondary, R.color.md_theme_tertiaryContainer),
    ON_HOLD(R.string.watching_status_on_hold, "on_hold", R.color.colorOnHoldColor, R.color.colorOnOnHoldColor),
    DROPPED(R.string.watching_status_dropped, "dropped", R.color.colorDroppedColor, R.color.colorOnDroppedColor),
    UNKNOWN(R.string.label_no_status, "unknown", R.color.md_theme_primary, R.color.md_theme_secondaryContainer);

    companion object {
        fun fromApiValue(apiValue: String?): WatchingStatus {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
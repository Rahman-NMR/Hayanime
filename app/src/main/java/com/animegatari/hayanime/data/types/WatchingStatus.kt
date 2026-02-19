package com.animegatari.hayanime.data.types

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.animegatari.hayanime.R
import com.animegatari.hayanime.ui.base.ChipInfoProvider

enum class WatchingStatus(
    @StringRes override val stringResId: Int,
    val apiValue: String,
    @ColorRes val colorResId: Int,
    @ColorRes val onColorResId: Int,
    @ColorRes val bgColorResId: Int,
) : ChipInfoProvider {
    WATCHING(
        R.string.watching_status_watching,
        "watching",
        R.color.colorWatchingColor,
        R.color.colorOnWatchingColor,
        R.color.colorWatchingColorContainer,
    ),
    COMPLETED(
        R.string.watching_status_completed,
        "completed",
        R.color.colorCompleteColor,
        R.color.colorOnCompleteColor,
        R.color.colorCompleteColorContainer,
    ),
    PLAN_TO_WATCH(
        R.string.watching_status_plan_to_watch,
        "plan_to_watch",
        R.color.md_theme_secondary,
        R.color.md_theme_tertiaryContainer,
        R.color.md_theme_tertiaryContainer,
    ),
    ON_HOLD(R.string.watching_status_on_hold, "on_hold", R.color.colorOnHoldColor, R.color.colorOnOnHoldColor, R.color.colorOnHoldColorContainer),
    DROPPED(R.string.watching_status_dropped, "dropped", R.color.colorDroppedColor, R.color.colorOnDroppedColor, R.color.colorDroppedColorContainer),
    UNKNOWN(R.string.label_no_status, "unknown", R.color.md_theme_primary, R.color.md_theme_secondaryContainer, R.color.md_theme_secondaryContainer);

    override val uniqueValue: String
        get() = apiValue

    override val iconResId: Int
        get() = 0

    companion object {
        fun fromApiValue(apiValue: String?): WatchingStatus {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
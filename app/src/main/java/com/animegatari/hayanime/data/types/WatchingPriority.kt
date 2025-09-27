package com.animegatari.hayanime.data.types

import android.content.Context
import androidx.annotation.StringRes
import com.animegatari.hayanime.R
import com.animegatari.hayanime.ui.utils.extension.DisplayableEnum

enum class WatchingPriority(val apiValue: Int, @StringRes val stringResId: Int) : DisplayableEnum {
    SELECT(-1, R.string.label_select),
    LOW(0, R.string.watch_priority_low),
    MEDIUM(1, R.string.watch_priority_medium),
    HIGH(2, R.string.watch_priority_high);

    override fun getDisplayString(context: Context): String {
        return context.getString(stringResId)
    }

    companion object {
        fun fromApiValue(apiValue: Int?): WatchingPriority {
            return entries.find { it.apiValue == apiValue } ?: SELECT
        }

        fun getDisplayableValues(): List<WatchingPriority> {
            return entries.toList()
        }
    }
}
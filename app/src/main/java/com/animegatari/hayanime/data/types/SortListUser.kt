package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class SortListUser(
    @StringRes val stringResId: Int,
    val apiValue: String,
) {
    LIST_SCORE(R.string.sort_user_list_score, "list_score"),
    LIST_UPDATED_AT(R.string.sort_user_list_updated_at, "list_updated_at"),
    ANIME_TITLE(R.string.sort_user_list_anime_title, "anime_title"),
    ANIME_START_DATE(R.string.sort_user_list_anime_start_date, "anime_start_date");

    companion object {
        fun fromApiValue(apiValue: String?): SortListUser {
            return entries.find { it.apiValue == apiValue } ?: ANIME_TITLE
        }
    }
}
package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class SortingAnime(@StringRes val stringResId: Int, val apiValue: String? = null) {
    SORT_BY_SCORE(R.string.sort_by_anime_score, "anime_score"),
    SORT_BY_MEMBERS(R.string.sort_by_anime_num_list_users, "anime_num_list_users"),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun keyValue(apiValue: String?): SortingAnime {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: UNKNOWN
        }
    }
}
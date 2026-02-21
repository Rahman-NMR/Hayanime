package com.animegatari.hayanime.presentation.common.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class ContinuedAnime(@StringRes val stringResId: Int, val value: Boolean) {
    CONTINUED(R.string.continued_anime, true),
    UNCONTINUED(R.string.uncontinued_anime, false);

    companion object {
        fun fromBoolean(value: Boolean): ContinuedAnime = entries.first { it.value == value }
    }
}
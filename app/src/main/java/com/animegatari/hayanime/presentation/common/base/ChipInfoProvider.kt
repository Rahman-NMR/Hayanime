package com.animegatari.hayanime.presentation.common.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ChipInfoProvider {
    @get:StringRes
    val stringResId: Int

    val uniqueValue: String

    @get:DrawableRes
    val iconResId: Int
}
package com.animegatari.hayanime.ui.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface ChipInfoProvider {
    @get:StringRes
    val stringResId: Int

    val uniqueValue: String

    @get:DrawableRes
    val iconResId: Int
}
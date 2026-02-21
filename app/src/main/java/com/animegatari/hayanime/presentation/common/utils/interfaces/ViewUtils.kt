package com.animegatari.hayanime.presentation.common.utils.interfaces

import androidx.core.content.ContextCompat
import com.animegatari.hayanime.presentation.common.base.ChipInfoProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

object ViewUtils {
    fun <T> ChipGroup.setupDynamicChips(hasIcon: Boolean, chipInfoProvider: List<T>) where T : ChipInfoProvider {
        chipInfoProvider.forEach { chipInfo ->
            val chip = Chip(context).apply {
                text = context.getString(chipInfo.stringResId)
                isCheckable = true
                tag = chipInfo.uniqueValue

                if (hasIcon) {
                    chipIcon = ContextCompat.getDrawable(context, chipInfo.iconResId)
                    isChipIconVisible = true
                }
            }
            this.addView(chip)
        }
    }
}
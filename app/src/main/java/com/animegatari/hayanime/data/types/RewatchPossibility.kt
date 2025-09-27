package com.animegatari.hayanime.data.types

import android.content.Context
import androidx.annotation.StringRes
import com.animegatari.hayanime.R
import com.animegatari.hayanime.ui.utils.extension.DisplayableEnum

enum class RewatchPossibility(val apiValue: Int, @StringRes val stringResId: Int) : DisplayableEnum {
    SELECT(0, R.string.label_select),
    VERY_LOW(1, R.string.rewatch_possibility_very_low),
    LOW(2, R.string.rewatch_possibility_low),
    MEDIUM(3, R.string.rewatch_possibility_medium),
    HIGH(4, R.string.rewatch_possibility_high),
    VERY_HIGH(5, R.string.rewatch_possibility_very_high);

    override fun getDisplayString(context: Context): String {
        return context.getString(stringResId)
    }

    companion object {
        fun fromApiValue(apiValue: Int?): RewatchPossibility {
            return entries.find { it.apiValue == apiValue } ?: SELECT
        }

        fun getDisplayableValues(): List<RewatchPossibility> {
            return entries.toList()
        }
    }
}
package com.animegatari.hayanime.ui.utils.layout

import android.content.Context
import kotlin.math.floor

object SpanCalculator {
    fun calculateSpanCount(context: Context, columnWidthDp: Int): Int {
        if (columnWidthDp <= 0) return 1

        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

        val spanCount = floor(screenWidthDp / columnWidthDp).toInt()

        return if (spanCount > 0) spanCount else 1
    }
}
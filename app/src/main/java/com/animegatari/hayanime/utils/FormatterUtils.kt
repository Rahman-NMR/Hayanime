package com.animegatari.hayanime.utils

import android.icu.text.NumberFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object FormatterUtils {
    fun digitNumberFormatter(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale.getDefault())
        return formatter.format(number)
    }

    fun formatDecimal(number: Float, pattern: String = "0.00"): String {
        val decimalFormat = DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.getDefault()))
        return decimalFormat.format(number)
    }

    fun formatApiDate(dateString: String?, locale: Locale = Locale.getDefault()): String? {
        if (dateString.isNullOrBlank()) {
            return null
        }

        try {
            val parts = dateString.split('-')
            val year = parts.getOrNull(0)?.toIntOrNull()
            val month = parts.getOrNull(1)?.toIntOrNull()
            val day = parts.getOrNull(2)?.toIntOrNull()

            return when {
                year != null && month != null && day != null -> {
                    val date = LocalDate.of(year, month, day)
                    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
                    date.format(formatter)
                }

                year != null && month != null -> {
                    val yearMonth = YearMonth.of(year, month)
                    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
                    yearMonth.format(formatter)
                }

                year != null -> {
                    val yearObj = Year.of(year)
                    val formatter = DateTimeFormatter.ofPattern("yyyy", locale)
                    yearObj.format(formatter)
                }

                else -> dateString
            }
        } catch (_: Exception) {
            return dateString
        }
    }
}
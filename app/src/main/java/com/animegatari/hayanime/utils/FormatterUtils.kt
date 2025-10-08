package com.animegatari.hayanime.utils

import android.icu.text.NumberFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
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
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
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

    fun formattedDateTimeZone(dateStr: String?, timeZoneStr: String?): String? {
        if (dateStr.isNullOrBlank()) {
            return null
        }

        return try {
            val zoneId = if (timeZoneStr.isNullOrBlank()) {
                ZoneId.systemDefault()
            } else {
                ZoneId.of(timeZoneStr)
            }

            val offsetDateTime = OffsetDateTime.parse(dateStr)
            val zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId)
            val formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.LONG)
                .withLocale(Locale.getDefault())

            formatter.format(zonedDateTime)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
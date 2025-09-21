package com.animegatari.hayanime.utils

import com.animegatari.hayanime.data.types.SeasonStart
import java.time.LocalDate

object TimeUtils {
    fun durationString(duration: Int): String? {
        val divide = 60
        val durationInMinutes = duration.div(divide)
        return when {
            durationInMinutes > 59 -> "${durationInMinutes.div(divide)} hr ${durationInMinutes.rem(divide)} min"
            else -> "$durationInMinutes min"
        }
    }

    fun getCurrentYear(): Int {
        return LocalDate.now().year
    }

    fun getCurrentMonth(): Int {
        return LocalDate.now().monthValue
    }

    fun getCurrentSeason(): String {
        val currentMonth = getCurrentMonth()
        val seasonForMonth = when (currentMonth) {
            1, 2, 3 -> SeasonStart.WINTER
            4, 5, 6 -> SeasonStart.SPRING
            7, 8, 9 -> SeasonStart.SUMMER
            10, 11, 12 -> SeasonStart.FALL
            else -> SeasonStart.UNKNOWN
        }
        return seasonForMonth.apiValue
    }
}
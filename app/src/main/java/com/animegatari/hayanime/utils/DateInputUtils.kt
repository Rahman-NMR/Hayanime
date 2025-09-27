package com.animegatari.hayanime.utils

object DateInputUtils {
    /** Month-related utility functions and data */
    private val monthMap: List<Pair<String, String>> = listOf(
        0 to "None",
        1 to "Jan", 2 to "Feb", 3 to "Mar",
        4 to "Apr", 5 to "May", 6 to "Jun",
        7 to "Jul", 8 to "Aug", 9 to "Sep",
        10 to "Oct", 11 to "Nov", 12 to "Dec"
    ).map { (apiValue, displayValue) ->
        val formattedValue = apiValue.toString().padStart(2, '0')
        formattedValue to displayValue
    }

    fun getMonthDisplayValue(monthApiValue: String?): String? {
        return monthMap.find { it.first == monthApiValue }?.second
    }

    fun getMonthApiValue(monthDisplayValue: String?): String? {
        return monthMap.find { it.second.equals(monthDisplayValue, ignoreCase = true) }?.first
    }

    fun getAllMonthDisplayValues(): List<String> = monthMap.map { it.second }

    /** Day-related utility functions and data */
    private val dayMap: List<Pair<String, String>> = (0..31).map { day ->
        val apiValue = day.toString().padStart(2, '0')
        val displayValue = if (day == 0) "None" else day.toString()
        apiValue to displayValue
    }

    fun getDayDisplayValue(dayApiValue: String?): String? {
        return dayMap.find { it.first == dayApiValue }?.second
    }

    fun getDayApiValue(dayDisplayValue: String?): String? {
        return dayMap.find { it.second == dayDisplayValue }?.first
    }

    fun getAllDayDisplayValues(): List<String> = dayMap.map { it.second }
}
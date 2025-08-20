package com.animegatari.hayanime.utils

object TimeUtils {
    fun durationString(duration: Int): String? {
        val divide = 60
        val durationInMinutes = duration.div(divide)
        return when {
            durationInMinutes > 59 -> "${durationInMinutes.div(divide)} hr ${durationInMinutes.rem(divide)} min"
            else -> "$durationInMinutes min"
        }
    }
}
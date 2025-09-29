package com.animegatari.hayanime.data.local.datamodel

data class DateComponents(
    val year: String? = null,
    val month: String? = null,
    val day: String? = null,
) {
    fun toFormattedString(): String? {
        return year?.let { y ->
            month?.let { m ->
                day?.let { d ->
                    "$y-$m-$d"
                } ?: "$y-$m"
            } ?: y
        }
    }

    companion object {
        fun fromFormattedString(dateString: String?): DateComponents? {
            if (dateString == null) return DateComponents()
            val parts = dateString.split("-")
            return DateComponents(
                year = parts.getOrNull(0),
                month = parts.getOrNull(1),
                day = parts.getOrNull(2),
            )
        }
    }
}
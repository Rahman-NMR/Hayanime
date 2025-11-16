package com.animegatari.hayanime.data.local.datamodel

import com.animegatari.hayanime.data.remote.response.AnimeList

data class SeasonModel(
    val year: Int,
    val season: String,
    val sort: String,
    val mediaType: String?,
    val isContinued: Boolean = false,
) {
    fun meetsConditions(animeList: AnimeList?): Boolean {
        val anime = animeList?.node ?: return false

        val mediaTypeMatches = mediaType.isNullOrBlank() || anime.mediaType == mediaType

        val seasonMatches = isContinued || anime.startSeason?.let { startSeason ->
            startSeason.year == year && startSeason.season == season
        } ?: false

        return mediaTypeMatches && seasonMatches
    }
}
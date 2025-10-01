package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.data.local.datamodel.SeasonModel
import com.animegatari.hayanime.data.remote.response.AnimeList
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun searchAnime(query: String, isNsfw: Boolean, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun suggestedAnime(isNsfw: Boolean, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun seasonalAnime(seasonModel: SeasonModel, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
}
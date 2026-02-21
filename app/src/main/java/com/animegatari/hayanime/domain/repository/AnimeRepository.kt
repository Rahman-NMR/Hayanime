package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.domain.model.SeasonModel
import com.animegatari.hayanime.data.remote.dto.AnimeDetail
import com.animegatari.hayanime.data.remote.dto.AnimeList
import com.animegatari.hayanime.core.result.Response
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun searchAnime(query: String, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun suggestedAnime(limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun seasonalAnime(seasonModel: SeasonModel, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun animeDetails(id: Int, commonFields: String): Flow<Response<AnimeDetail>>
}
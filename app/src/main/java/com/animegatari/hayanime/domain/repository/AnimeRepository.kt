package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.data.local.datamodel.SeasonModel
import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun searchAnime(query: String, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun suggestedAnime(limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun seasonalAnime(seasonModel: SeasonModel, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    fun animeDetails(id: Int, commonFields: String): Flow<Response<AnimeDetail>>
}
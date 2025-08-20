package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.data.remote.response.AnimeList
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun searchAnime(query: String, limit: Int, offset: Int): Flow<PagingData<AnimeList>>
}
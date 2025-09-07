package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.data.remote.response.AnimeList
import kotlinx.coroutines.flow.Flow

interface UserAnimeListRepository {
    fun userAnimeList(status: String?, sort: String?, isNsfw: Boolean, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
}
package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.data.model.AnimeMinimum
import com.animegatari.hayanime.data.model.MyListStatus
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface UserAnimeListRepository {
    fun userAnimeList(status: String?, sort: String?, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    suspend fun updateAnimeProgress(animeId: Int, newProgressEpisode: Int, isCompletedWatching: String?, finishDate: String?): Response<Unit>
    suspend fun getMyDetailAnime(animeId: Int, fields: String): Response<AnimeMinimum>
    suspend fun updateMyAnimeListStatus(animeId: Int, myListStatus: MyListStatus?): Response<Unit>
    suspend fun deleteAnime(animeId: Int): Response<Unit>
}
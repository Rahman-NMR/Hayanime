package com.animegatari.hayanime.domain.repository

import androidx.paging.PagingData
import com.animegatari.hayanime.domain.model.MyListModel
import com.animegatari.hayanime.data.remote.dto.AnimeMinimum
import com.animegatari.hayanime.data.remote.dto.MyListStatus
import com.animegatari.hayanime.data.remote.dto.AnimeList
import com.animegatari.hayanime.core.result.Response
import kotlinx.coroutines.flow.Flow

interface UserAnimeListRepository {
    fun userAnimeList(dataModel: MyListModel, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>>
    suspend fun updateAnimeProgress(animeId: Int, newProgressEpisode: Int, isCompletedWatching: String?, finishDate: String?): Response<Unit>
    suspend fun getMyDetailAnime(animeId: Int, fields: String): Response<AnimeMinimum>
    suspend fun updateMyAnimeListStatus(animeId: Int, myListStatus: MyListStatus?): Response<Unit>
    suspend fun deleteAnime(animeId: Int): Response<Unit>
}
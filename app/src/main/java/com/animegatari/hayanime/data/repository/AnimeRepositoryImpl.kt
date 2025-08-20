package com.animegatari.hayanime.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.animegatari.hayanime.data.pagination.AnimePagingSource
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.data.remote.api.AnimeApiService
import com.animegatari.hayanime.data.remote.response.AnimeList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
) : AnimeRepository {
    override fun searchAnime(query: String, limit: Int, offset: Int): Flow<PagingData<AnimeList>> {
        return Pager(
            config = PagingConfig(
                pageSize = limit,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource(apiService, query)
            }
        ).flow
    }
}
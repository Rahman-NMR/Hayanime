package com.animegatari.hayanime.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.animegatari.hayanime.data.pagination.AnimePagingSource
import com.animegatari.hayanime.data.remote.api.AnimeApiService
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
) : AnimeRepository {
    override fun searchAnime(query: String, isNsfw: Boolean, limitConfig: Int, common: String): Flow<PagingData<AnimeList>> {
        return Pager(
            config = PagingConfig(
                pageSize = limitConfig,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource { limit, offset ->
                    apiService.getAnimeList(
                        query = query,
                        nsfw = isNsfw,
                        limit = limit,
                        offset = offset,
                        fields = common
                    )
                }
            }
        ).flow
    }
}
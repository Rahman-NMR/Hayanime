package com.animegatari.hayanime.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.animegatari.hayanime.data.pagination.AnimePagingSource
import com.animegatari.hayanime.data.remote.api.UserAnimeListApiService
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAnimeListRepositoryImpl @Inject constructor(
    private val userAnimeService: UserAnimeListApiService
) : UserAnimeListRepository {
    override fun userAnimeList(
        status: String?,
        sort: String?,
        isNsfw: Boolean,
        limitConfig: Int,
        commonFields: String,
    ): Flow<PagingData<AnimeList>> {
        return Pager(
            config = PagingConfig(
                pageSize = limitConfig,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource { limit, offset ->
                    userAnimeService.getUserAnimeList(
                        status = status,
                        sort = sort,
                        limit = limit,
                        offset = offset,
                        nsfw = isNsfw,
                        fields = commonFields
                    )
                }
            }
        ).flow
    }
}
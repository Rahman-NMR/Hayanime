package com.animegatari.hayanime.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.animegatari.hayanime.data.model.AnimeMinimum
import com.animegatari.hayanime.data.model.MyListStatus
import com.animegatari.hayanime.data.pagination.AnimePagingSource
import com.animegatari.hayanime.data.remote.api.AnimeApiService
import com.animegatari.hayanime.data.remote.api.UserAnimeListApiService
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAnimeListRepositoryImpl @Inject constructor(
    private val userAnimeService: UserAnimeListApiService,
    private val animeService: AnimeApiService,
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

    override suspend fun getMyDetailAnime(animeID: Int, fields: String): AnimeMinimum {
        return animeService.getShortAnimeDetail(animeID, fields)
    }

    override suspend fun updateMyAnimeListStatus(animeId: Int, myListStatus: MyListStatus?) {
        userAnimeService.updateAnimeListStatus(
            animeId = animeId,
            status = myListStatus?.status,
            numWatchedEpisodes = myListStatus?.numWatchedEpisodes,
            startDate = myListStatus?.startDate,
            finishDate = myListStatus?.finishDate,
            score = myListStatus?.score,
            isRewatching = myListStatus?.isRewatching,
            numTimesRewatched = myListStatus?.numTimesRewatched,
            priority = myListStatus?.priority,
            rewatchValue = myListStatus?.rewatchValue,
            tags = myListStatus?.tags?.joinToString(","),
            comments = myListStatus?.comments
        )
    }

    override suspend fun deleteAnime(animeId: Int) {
        userAnimeService.deleteAnimeFromList(animeId)
    }
}
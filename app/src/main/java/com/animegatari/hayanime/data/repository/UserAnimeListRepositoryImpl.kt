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
import com.animegatari.hayanime.data.remote.response.ErrorResponse
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import com.animegatari.hayanime.domain.utils.Response
import com.google.gson.Gson
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

    override suspend fun getMyDetailAnime(animeId: Int, fields: String): Response<AnimeMinimum> {
        return try {
            val response = animeService.getShortAnimeDetail(animeId, fields)

            if (response != AnimeMinimum()) {
                Response.Success(response)
            } else {
                Response.Error()
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage)
        }
    }

    override suspend fun updateMyAnimeListStatus(animeId: Int, myListStatus: MyListStatus?): Response<Unit> {
        return try {
            val response = userAnimeService.updateAnimeListStatus(
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

            if (response.isSuccessful) {
                Response.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Response.Error(errorResponse.message)
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage)
        }
    }

    override suspend fun updateAnimeProgress(
        animeId: Int,
        newProgressEpisode: Int,
        isCompletedWatching: String?,
        finishDate: String?,
    ): Response<Unit> {
        return try {
            val response = userAnimeService.updateProgressWatching(
                animeId = animeId,
                numWatchedEpisodes = newProgressEpisode,
                status = isCompletedWatching,
                finishDate = finishDate
            )

            if (response.isSuccessful) {
                Response.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Response.Error(errorResponse.message)
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAnime(animeId: Int): Response<Unit> {
        return try {
            val response = userAnimeService.deleteAnimeFromList(animeId)

            if (response.isSuccessful) {
                Response.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Response.Error(errorResponse.message)
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage)
        }
    }
}
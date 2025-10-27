package com.animegatari.hayanime.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.animegatari.hayanime.data.local.datamodel.SeasonModel
import com.animegatari.hayanime.data.local.datastore.SettingsPreferences
import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.data.pagination.AnimePagingSource
import com.animegatari.hayanime.data.remote.api.AnimeApiService
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApiService,
    private val settingsDataStore: SettingsPreferences,
) : AnimeRepository {
    override fun searchAnime(query: String, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>> {
        return Pager(
            config = PagingConfig(
                pageSize = limitConfig,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource { limit, offset ->
                    val isNsfw = runBlocking { settingsDataStore.searchNsfw.first() }
                    apiService.getAnimeList(
                        query = query,
                        nsfw = isNsfw,
                        limit = limit,
                        offset = offset,
                        fields = commonFields
                    )
                }
            }
        ).flow
    }

    override fun suggestedAnime(limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>> {
        return Pager(
            config = PagingConfig(
                pageSize = limitConfig,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource { limit, offset ->
                    val isNsfw = runBlocking { settingsDataStore.suggestionsNsfw.first() }
                    apiService.getSuggestedAnime(
                        nsfw = isNsfw,
                        limit = limit,
                        offset = offset,
                        fields = commonFields
                    )
                }
            }
        ).flow
    }

    override fun seasonalAnime(seasonModel: SeasonModel, limitConfig: Int, commonFields: String): Flow<PagingData<AnimeList>> {
        return Pager(
            config = PagingConfig(
                pageSize = limitConfig,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AnimePagingSource { limit, offset ->
                    val isNsfw = runBlocking { settingsDataStore.suggestionsNsfw.first() }
                    apiService.getAnimeSeason(
                        year = seasonModel.year,
                        season = seasonModel.season,
                        sort = seasonModel.sort,
                        nsfw = isNsfw,
                        limit = limit,
                        offset = offset,
                        fields = commonFields
                    )
                }
            }
        ).flow
    }

    override fun animeDetails(id: Int, commonFields: String): Flow<Response<AnimeDetail>> = flow {
        emit(Response.Loading)
        try {
            val response = apiService.getAnimeDetail(id, commonFields)

            if (response != AnimeDetail()) {
                emit(Response.Success(response))
            } else {
                emit(Response.Error())
            }
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage))
        }
    }
}
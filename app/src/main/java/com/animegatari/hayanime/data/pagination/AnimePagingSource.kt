package com.animegatari.hayanime.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.animegatari.hayanime.data.remote.api.AnimeApiService
import com.animegatari.hayanime.data.remote.response.AnimeList
import retrofit2.HttpException

class AnimePagingSource(
    private val apiService: AnimeApiService,
    private val query: String,
) : PagingSource<Int, AnimeList>() {
    override fun getRefreshKey(state: PagingState<Int, AnimeList>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeList> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize

            val response = apiService.getAnimeList(
                query = query,
                limit = limit,
                offset = offset,
                fields = "nsfw,mean,num_scoring_users,media_type,status,num_episodes,start_season,rating,average_episode_duration,my_list_status{status},studios,genres"
            )

            val animeData = response.data?.filterNotNull() ?: emptyList()

            LoadResult.Page(
                data = animeData,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (animeData.isEmpty()) null else offset + limit
            )
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
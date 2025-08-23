package com.animegatari.hayanime.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.data.remote.response.AnimeListResponse
import retrofit2.HttpException

class AnimePagingSource(
    private val apiCallExecutor: suspend (limit: Int, offset: Int) -> AnimeListResponse,
) : PagingSource<Int, AnimeList>() {
    override fun getRefreshKey(state: PagingState<Int, AnimeList>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeList> {
        val currentPosition = params.key ?: STARTING_PAGE_INDEX
        val pageSize = params.loadSize

        return try {
            val response = apiCallExecutor(pageSize, currentPosition)
            val animeData = response.data?.filterNotNull() ?: emptyList()

            LoadResult.Page(
                data = animeData,
                prevKey = if (currentPosition == STARTING_PAGE_INDEX) null else (currentPosition - pageSize).coerceAtLeast(0),
                nextKey = if (animeData.isEmpty()) null else currentPosition + pageSize
            )
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 0
    }
}
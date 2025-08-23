package com.animegatari.hayanime.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegatari.hayanime.core.Config.COMMON_ANIME_FIELDS
import com.animegatari.hayanime.core.Config.DEFAULT_PAGE_LIMIT
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val animeList: Flow<PagingData<AnimeList>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                animeRepository.searchAnime(
                    query = query,
                    isNsfw = true,
                    limitConfig = DEFAULT_PAGE_LIMIT,
                    commonFields = COMMON_ANIME_FIELDS
                )
            }
        }.cachedIn(viewModelScope)

    fun getAnimeList(searchQuery: String) {
        _searchQuery.value = searchQuery
    }
}
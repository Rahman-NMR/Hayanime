package com.animegatari.hayanime.ui.main.season

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.local.datamodel.SeasonModel
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {
    private val _selectedYear = MutableStateFlow(TimeUtils.getCurrentYear())
    val selectedYear: StateFlow<Int> = _selectedYear

    private val _selectedSeason = MutableStateFlow(TimeUtils.getCurrentSeason())
    val selectedSeason: StateFlow<String> = _selectedSeason

    private val _sortKey = MutableStateFlow(BY_POPULARITY)
    val sortKey: StateFlow<String> = _sortKey

    @OptIn(ExperimentalCoroutinesApi::class)
    val animeList: Flow<PagingData<AnimeList>> = combine(
        _selectedYear,
        _selectedSeason,
        _sortKey
    ) { year, season, sort ->
        SeasonModel(year, season, sort)
    }.flatMapLatest { dataModel ->
        animeRepository.seasonalAnime(
            seasonModel = dataModel,
            limitConfig = Config.DEFAULT_PAGE_LIMIT,
            commonFields = Config.ANIME_LIST_FIELDS
        )
    }.cachedIn(viewModelScope)

    fun changeSeason(season: String) {
        _selectedSeason.value = season
    }

    fun changeYear(year: Int) {
        _selectedYear.value = year
    }

    fun toggleSortKey() {
        val currentSort = _sortKey.value
        _sortKey.value =
            if (currentSort == BY_POPULARITY) BY_SCORE
            else BY_POPULARITY
    }

    companion object SortKeys {
        const val BY_POPULARITY = "anime_num_list_users"
        const val BY_SCORE = "anime_score"
    }
}
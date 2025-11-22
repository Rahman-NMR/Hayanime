package com.animegatari.hayanime.ui.main.season

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.local.datamodel.SeasonModel
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.domain.utils.UiEvent
import com.animegatari.hayanime.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
) : ViewModel() {
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)
    val events = _eventChannel.receiveAsFlow()

    val currentSeason get() = TimeUtils.getCurrentSeason()
    val currentYear get() = TimeUtils.getCurrentYear()

    private val _seasonFilter = MutableStateFlow(
        value = SeasonModel(
            year = currentYear,
            season = currentSeason,
            sort = BY_POPULARITY,
            mediaType = null,
            isContinued = false
        )
    )
    val seasonalFilterState: StateFlow<SeasonModel> = _seasonFilter
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _seasonFilter.value
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val animeList: Flow<PagingData<AnimeList>> = seasonalFilterState.flatMapLatest { dataModel ->
        animeRepository.seasonalAnime(
            seasonModel = dataModel,
            limitConfig = Config.DEFAULT_PAGE_LIMIT,
            commonFields = Config.ANIME_LIST_FIELDS
        )
    }.cachedIn(viewModelScope)

    fun notifyDataModified() = viewModelScope.launch {
        _eventChannel.send(UiEvent.DataModified)
    }

    fun notifyDataUpdated() = viewModelScope.launch {
        _eventChannel.send(UiEvent.DataUpdated)
    }

    fun changeSeason(season: String) {
        if (season == _seasonFilter.value.season) {
            return
        }

        _seasonFilter.value = _seasonFilter.value.copy(season = season)
        notifyDataModified()
    }

    fun changeYear(year: Int) {
        if (year == _seasonFilter.value.year) {
            return
        }

        _seasonFilter.value = _seasonFilter.value.copy(year = year)
        notifyDataModified()
    }

    fun setToCurrentSeason() {
        _seasonFilter.value = _seasonFilter.value.copy(
            year = currentYear,
            season = currentSeason,
            mediaType = null,
            isContinued = false
        )
        notifyDataModified()
    }

    fun filterByMediaType(mediaType: String?) {
        if (mediaType == _seasonFilter.value.mediaType) {
            return
        }

        _seasonFilter.value = _seasonFilter.value.copy(mediaType = mediaType)
        notifyDataModified()
    }

    fun toggleContinuedAnime() {
        _seasonFilter.value = _seasonFilter.value.copy(isContinued = !_seasonFilter.value.isContinued)
        notifyDataModified()
    }

    fun toggleSortKey() {
        val newSort = if (_seasonFilter.value.sort == BY_POPULARITY) {
            BY_SCORE
        } else {
            BY_POPULARITY
        }

        _seasonFilter.value = _seasonFilter.value.copy(sort = newSort)
        notifyDataModified()
    }

    companion object SortKeys {
        const val BY_POPULARITY = "anime_num_list_users"
        const val BY_SCORE = "anime_score"
    }
}
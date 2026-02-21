package com.animegatari.hayanime.presentation.feature.main.myList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegatari.hayanime.R
import com.animegatari.hayanime.core.config.Config
import com.animegatari.hayanime.presentation.common.model.DateComponents
import com.animegatari.hayanime.domain.model.MyListModel
import com.animegatari.hayanime.data.remote.dto.AnimeList
import com.animegatari.hayanime.presentation.common.types.SortListUser
import com.animegatari.hayanime.presentation.common.types.WatchingStatus
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import com.animegatari.hayanime.domain.utils.UiEvent
import com.animegatari.hayanime.core.result.onError
import com.animegatari.hayanime.core.result.onSuccess
import com.animegatari.hayanime.presentation.common.utils.TimeUtils.getCurrentDay
import com.animegatari.hayanime.presentation.common.utils.TimeUtils.getCurrentMonth
import com.animegatari.hayanime.presentation.common.utils.TimeUtils.getCurrentYear
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
class MyListViewModel @Inject constructor(
    private val userAnimeListRepository: UserAnimeListRepository,
    private val application: Application,
) : AndroidViewModel(application) {
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)
    val events = _eventChannel.receiveAsFlow()

    private val _myAnimeList = MutableStateFlow(
        value = MyListModel(
            sort = SortListUser.LIST_UPDATED_AT.apiValue,
            watchingStatus = null
        )
    )
    val myAnimeListState: StateFlow<MyListModel> = _myAnimeList
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _myAnimeList.value
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val myAnimeList: Flow<PagingData<AnimeList>> = _myAnimeList
        .flatMapLatest { watchingStatus ->
            userAnimeListRepository.userAnimeList(
                dataModel = watchingStatus,
                limitConfig = Config.DEFAULT_PAGE_LIMIT,
                commonFields = Config.MYLIST_ANIME_FIELDS
            )
        }.cachedIn(viewModelScope)

    fun notifyDataModified() = viewModelScope.launch {
        _eventChannel.send(UiEvent.DataModified)
    }

    fun notifyDataUpdated() = viewModelScope.launch {
        _eventChannel.send(UiEvent.DataUpdated)
    }

    fun getAnimeList(watchingStatusValue: String? = null) {
        if (watchingStatusValue == _myAnimeList.value.watchingStatus) {
            return
        }

        _myAnimeList.value = _myAnimeList.value.copy(watchingStatus = watchingStatusValue)
        notifyDataModified()
    }

    fun sortByValue(sortValue: String) {
        if (sortValue == _myAnimeList.value.sort) {
            return
        }

        _myAnimeList.value = _myAnimeList.value.copy(sort = sortValue)
        notifyDataModified()
    }

    fun updateAnimeProgress(
        animeId: Int?,
        currentEpisodeProgress: Int?,
        numEpisode: Int?,
    ) = viewModelScope.launch {
        if (animeId == null || currentEpisodeProgress == null) {
            val errorMessage = application.getString(R.string.message_missing_anime_id_or_current_episode)
            _eventChannel.send(UiEvent.UpdateProgressError(errorMessage))
            return@launch
        }

        if (currentEpisodeProgress == numEpisode) {
            return@launch
        }

        val newProgressEpisode = currentEpisodeProgress.plus(1)
        var isCompletedWatching: String? = null
        var finishDate: String? = null

        if (numEpisode != null && numEpisode > 0 && newProgressEpisode == numEpisode) {
            isCompletedWatching = WatchingStatus.COMPLETED.apiValue
            finishDate = DateComponents(
                year = getCurrentYear().toString(),
                month = getCurrentMonth().toString().padStart(2, '0'),
                day = getCurrentDay().toString().padStart(2, '0')
            ).toFormattedString()
        }

        userAnimeListRepository.updateAnimeProgress(animeId, newProgressEpisode, isCompletedWatching, finishDate)
            .onSuccess { notifyDataUpdated() }
            .onError { message ->
                _eventChannel.send(UiEvent.UpdateProgressError(message))
            }
    }
}
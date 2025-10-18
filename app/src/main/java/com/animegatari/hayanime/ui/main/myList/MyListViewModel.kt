package com.animegatari.hayanime.ui.main.myList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegatari.hayanime.R
import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.local.datamodel.DateComponents
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import com.animegatari.hayanime.domain.utils.onError
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.utils.TimeUtils.getCurrentDay
import com.animegatari.hayanime.utils.TimeUtils.getCurrentMonth
import com.animegatari.hayanime.utils.TimeUtils.getCurrentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val userAnimeListRepository: UserAnimeListRepository,
    private val application: Application,
) : AndroidViewModel(application) {
    private val _eventChannel = Channel<MyListEvent>(Channel.BUFFERED)
    val events = _eventChannel.receiveAsFlow()

    private val _myAnimeList = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val myAnimeList: Flow<PagingData<AnimeList>> = _myAnimeList
        .flatMapLatest { watchingStatus ->
            userAnimeListRepository.userAnimeList(
                status = watchingStatus,
                sort = "list_updated_at",
                limitConfig = Config.DEFAULT_PAGE_LIMIT,
                commonFields = Config.MYLIST_ANIME_FIELDS
            )
        }.cachedIn(viewModelScope)

    fun getAnimeList(watchingStatusValue: String? = null) {
        _myAnimeList.value = watchingStatusValue
    }

    fun updateAnimeProgress(
        animeId: Int?,
        currentEpisodeProgress: Int?,
        numEpisode: Int?,
    ) = viewModelScope.launch {
        if (animeId == null || currentEpisodeProgress == null) {
            val errorMessage = application.getString(R.string.message_missing_anime_id_or_current_episode)
            _eventChannel.send(MyListEvent.UpdateProgressError(errorMessage))
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
            .onSuccess {
                _eventChannel.send(MyListEvent.DataModified)
            }
            .onError { message ->
                _eventChannel.send(MyListEvent.UpdateProgressError(message))
            }
    }
}

sealed class MyListEvent {
    object DataModified : MyListEvent()
    data class UpdateProgressError(val message: String?) : MyListEvent()
}
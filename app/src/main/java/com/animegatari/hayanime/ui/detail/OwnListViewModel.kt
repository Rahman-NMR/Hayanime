package com.animegatari.hayanime.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.model.AnimeMinimum
import com.animegatari.hayanime.data.local.datamodel.DateComponents
import com.animegatari.hayanime.data.model.MyListStatus
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import com.animegatari.hayanime.domain.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnListViewModel @Inject constructor(
    private val userAnimeListRepository: UserAnimeListRepository,
) : ViewModel() {
    private val missingAnimeIdError = "Missing anime ID. Please re-open this page"

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    private val _maxEpisodes = MutableStateFlow<Int?>(null)
    val maxEpisodes: StateFlow<Int?> get() = _maxEpisodes.asStateFlow()

    private val _newestAnimeData = MutableStateFlow<AnimeMinimum?>(null)
    private val _originalAnimeData = MutableStateFlow<AnimeMinimum?>(null)

    val animeUIState: StateFlow<AnimeMinimum?> get() = _newestAnimeData.asStateFlow()

    private suspend fun fetchMyAnimeData(animeId: Int): AnimeMinimum? {
        val extendedFields = Config.OWN_ANIME_LIST_EXTENDED_FIELDS
        val mainFields = Config.OWN_ANIME_LIST_MAIN_FIELDS
        val smollFields = Config.SHORT_ANIME_FIELDS

        return userAnimeListRepository.getMyDetailAnime(
            animeId,
            "$smollFields{$mainFields,$extendedFields}"
        )
    }

    fun loadMyAnimeDetail(animeId: Int, onResponse: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val animeDetails = fetchMyAnimeData(animeId)
            _newestAnimeData.value = animeDetails
            _originalAnimeData.value = animeDetails

            _startDateComponents.value = DateComponents.fromFormattedString(animeDetails?.myListStatus?.startDate)
            _finishDateComponents.value = DateComponents.fromFormattedString(animeDetails?.myListStatus?.finishDate)
            _maxEpisodes.value = animeDetails?.numEpisodes

            onResponse(Response.Success(true))
        } catch (_: Exception) {
            onResponse(Response.Error())
        } finally {
            _isLoading.value = false
        }
    }

    fun saveChanges(animeId: Int?, onResponse: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        if (animeId == null) {
            onResponse(Response.Error(missingAnimeIdError))
            return@launch
        }

        val currentStatus = _newestAnimeData.value?.myListStatus
        val originalStatus = _originalAnimeData.value?.myListStatus

        if (currentStatus == originalStatus) {
            onResponse(Response.Success(false))
            return@launch
        }

        _isLoading.value = true
        try {
            userAnimeListRepository.updateMyAnimeListStatus(animeId = animeId, myListStatus = currentStatus)
            onResponse(Response.Success(true))
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        } finally {
            _isLoading.value = false
            Log.i(
                "Hayanime update", "title: ${_originalAnimeData.value?.title}\n" +
                        "ori: ${_originalAnimeData.value?.myListStatus}\n" +
                        "new: ${_newestAnimeData.value?.myListStatus}"
            )
        }
    }

    fun deleteThisSeries(animeId: Int?, onResponse: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        if (animeId == null) {
            onResponse(Response.Error(missingAnimeIdError))
            return@launch
        }

        _isLoading.value = true
        try {
            userAnimeListRepository.deleteAnime(animeId)
            onResponse(Response.Success(true))
        } catch (e: Exception) {
            onResponse(Response.Error(e.localizedMessage))
        } finally {
            _isLoading.value = false
        }
    }

    private fun updateMyListStatus(newListStatus: MyListStatus.() -> MyListStatus) {
        val currentData = _newestAnimeData.value
        val currentStatus = currentData?.myListStatus ?: MyListStatus()
        _newestAnimeData.value = currentData?.copy(
            myListStatus = currentStatus.newListStatus()
        ) ?: AnimeMinimum(myListStatus = MyListStatus().newListStatus())
    }

    fun updateWatchingStatus(selectedChip: String?) = updateMyListStatus {
        copy(status = selectedChip.orEmpty())
    }

    fun updateSelectedEpisode(selectedEpisode: Int) = updateMyListStatus {
        copy(numWatchedEpisodes = selectedEpisode)
    }

    fun updateSelectedScore(selectedScore: Int) = updateMyListStatus {
        copy(score = selectedScore)
    }

    fun updateWatchingPriority(selectedPriority: Int) = updateMyListStatus {
        copy(priority = selectedPriority)
    }

    fun updateRewatchPossibility(selectedRewatchValue: Int) = updateMyListStatus {
        copy(rewatchValue = selectedRewatchValue)
    }

    fun updateIsRewatching(isRewatching: Boolean) = updateMyListStatus {
        copy(isRewatching = isRewatching)
    }

    fun updateRewatchedCount(rewatchedCount: String?) = updateMyListStatus {
        val totalRewatches = rewatchedCount?.toIntOrNull() ?: 0
        copy(numTimesRewatched = totalRewatches)
    }

    fun updateComments(updatedComments: String?) = updateMyListStatus {
        copy(comments = updatedComments.orEmpty())
    }

    fun updateTags(tagsListString: String?) = updateMyListStatus {
        val tags = if (tagsListString.isNullOrBlank()) {
            emptyList()
        } else {
            tagsListString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
        copy(tags = tags)
    }

    private val _startDateComponents = MutableStateFlow<DateComponents?>(DateComponents())
    val startDateComponents: StateFlow<DateComponents?> get() = _startDateComponents.asStateFlow()

    private val _finishDateComponents = MutableStateFlow<DateComponents?>(DateComponents())
    val finishDateComponents: StateFlow<DateComponents?> get() = _finishDateComponents.asStateFlow()

    /** null update using one by one selection,
     *  not null update using current date */
    fun updateStartDate(newDateString: String? = null) = updateMyListStatus {
        val finalDateString = if (newDateString == null) {
            _startDateComponents.value?.toFormattedString()
        } else {
            val dateComponents = DateComponents.fromFormattedString(newDateString)
            _startDateComponents.value = dateComponents
            newDateString
        }

        copy(startDate = finalDateString)
    }

    fun updateFinishDate(newDateString: String? = null) = updateMyListStatus {
        val finalDateString = if (newDateString == null) {
            _finishDateComponents.value?.toFormattedString()
        } else {
            val dateComponents = DateComponents.fromFormattedString(newDateString)
            _finishDateComponents.value = dateComponents
            newDateString
        }

        copy(finishDate = finalDateString)
    }

    fun updateStartDateUnknown(isUnknown: Boolean) = updateMyListStatus {
        val dateString = _startDateComponents.value?.toFormattedString()
        copy(startDate = if (isUnknown) "" else dateString)
    }

    fun updateFinishDateUnknown(isUnknown: Boolean) = updateMyListStatus {
        val dateString = _finishDateComponents.value?.toFormattedString()
        copy(finishDate = if (isUnknown) "" else dateString)
    }

    fun saveStartDateYear(year: String?) {
        _startDateComponents.value = _startDateComponents.value?.copy(year = year)
        updateStartDate()
    }

    fun saveStartDateMonth(month: String?) {
        _startDateComponents.value = _startDateComponents.value?.copy(month = month)
        updateStartDate()
    }

    fun saveStartDateDay(day: String?) {
        _startDateComponents.value = _startDateComponents.value?.copy(day = day)
        updateStartDate()
    }

    fun saveFinishDateYear(year: String?) {
        _finishDateComponents.value = _finishDateComponents.value?.copy(year = year)
        updateFinishDate()
    }

    fun saveFinishDateMonth(month: String?) {
        _finishDateComponents.value = _finishDateComponents.value?.copy(month = month)
        updateFinishDate()
    }

    fun saveFinishDateDay(day: String?) {
        _finishDateComponents.value = _finishDateComponents.value?.copy(day = day)
        updateFinishDate()
    }
}
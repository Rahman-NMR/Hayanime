package com.animegatari.hayanime.ui.main.myList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val useranimeListRepository: UserAnimeListRepository,
) : ViewModel() {
    private val _myAnimeList = MutableStateFlow(null as String?)

    @OptIn(ExperimentalCoroutinesApi::class)
    val myAnimeList: Flow<PagingData<AnimeList>> = _myAnimeList
        .flatMapLatest { watchingStatus ->
            useranimeListRepository.userAnimeList(
                status = watchingStatus,
                sort = "list_updated_at",
                isNsfw = true,
                limitConfig = Config.DEFAULT_PAGE_LIMIT,
                commonFields = Config.COMMON_ANIME_FIELDS
            )
        }.cachedIn(viewModelScope)

    fun getAnimeList(watchingStatusValue: String? = null) {
        _myAnimeList.value = watchingStatusValue
    }
}
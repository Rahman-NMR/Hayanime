package com.animegatari.hayanime.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.domain.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    repository: AnimeRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val animeId = savedStateHandle.get<Int>("animeId") ?: 0
    val animeDetail: StateFlow<Response<AnimeDetail>> = repository.animeDetails(animeId, Config.ANIME_DETAIL_FIELDS)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Response.Loading
        )

    fun getAnimeUrl(): String = "${BuildConfig.BASE_URL}anime/$animeId"
}
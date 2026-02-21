package com.animegatari.hayanime.presentation.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.core.config.Config
import com.animegatari.hayanime.data.remote.dto.AnimeDetail
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.core.result.Response
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
    val animeId = savedStateHandle.get<Int>("animeId") ?: 0
    val animeDetail: StateFlow<Response<AnimeDetail>> = repository.animeDetails(animeId, Config.ANIME_DETAIL_FIELDS)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Response.Loading
        )

    fun getAnimeUrl(): String = "${BuildConfig.BASE_URL}anime/$animeId"
}
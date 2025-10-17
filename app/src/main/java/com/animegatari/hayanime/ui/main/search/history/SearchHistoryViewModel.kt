package com.animegatari.hayanime.ui.main.search.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.domain.model.SearchHistoryItem
import com.animegatari.hayanime.domain.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val historyRepository: SearchHistoryRepository,
) : ViewModel() {
    val historyState: StateFlow<List<SearchHistoryItem>> = historyRepository.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveSearchQuery(query: String) = viewModelScope.launch {
        historyRepository.saveSearchQuery(query)
    }

    fun removeSelectedHistory(id: Long) = viewModelScope.launch {
        historyRepository.removeSelectedHistory(id)
    }
}
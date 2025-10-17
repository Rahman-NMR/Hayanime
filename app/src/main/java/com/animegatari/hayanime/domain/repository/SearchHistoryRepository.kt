package com.animegatari.hayanime.domain.repository

import com.animegatari.hayanime.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getHistory(): Flow<List<SearchHistoryItem>>
    suspend fun saveSearchQuery(query: String)
    suspend fun removeSelectedHistory(id: Long)
    suspend fun clearHistory()
}
package com.animegatari.hayanime.data.repository

import com.animegatari.hayanime.data.local.dao.SearchHistoryDao
import com.animegatari.hayanime.data.local.datamodel.toDomain
import com.animegatari.hayanime.domain.model.SearchHistoryItem
import com.animegatari.hayanime.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: SearchHistoryDao,
) : SearchHistoryRepository {
    override fun getHistory(): Flow<List<SearchHistoryItem>> {
        return dao.getRecentSearchHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        dao.insertOrUpdate(query)
    }

    override suspend fun removeSelectedHistory(id: Long) {
        dao.deleteHistoryById(id)
    }

    override suspend fun clearHistory() {
        dao.clearHistory()
    }
}
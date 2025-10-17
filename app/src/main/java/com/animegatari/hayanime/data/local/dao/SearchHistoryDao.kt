package com.animegatari.hayanime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.animegatari.hayanime.data.local.datamodel.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Transaction
    suspend fun insertOrUpdate(query: String) {
        val existingEntity = findByQueryText(query)
        val currentTime = System.currentTimeMillis()

        if (existingEntity != null) {
            updateTimestamp(query, currentTime)
        } else {
            val newEntity = SearchHistoryEntity(queryText = query, timestamp = currentTime)
            insert(newEntity)
        }
    }

    @Query("SELECT * FROM search_history WHERE queryText = :query COLLATE NOCASE LIMIT 1")
    suspend fun findByQueryText(query: String): SearchHistoryEntity?

    @Query("UPDATE search_history SET timestamp = :timestamp WHERE queryText = :query")
    suspend fun updateTimestamp(query: String, timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
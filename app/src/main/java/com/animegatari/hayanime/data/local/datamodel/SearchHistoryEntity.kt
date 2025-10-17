package com.animegatari.hayanime.data.local.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.animegatari.hayanime.domain.model.SearchHistoryItem

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val queryText: String,
    val timestamp: Long,
)

fun SearchHistoryEntity.toDomain() = SearchHistoryItem(
    id = this.id,
    queryText = this.queryText,
    timestamp = this.timestamp
)
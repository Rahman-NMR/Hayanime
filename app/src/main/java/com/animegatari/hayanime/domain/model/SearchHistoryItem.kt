package com.animegatari.hayanime.domain.model

data class SearchHistoryItem(
    val id: Long = 0,
    val queryText: String,
    val timestamp: Long,
)
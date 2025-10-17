package com.animegatari.hayanime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.animegatari.hayanime.data.local.dao.SearchHistoryDao
import com.animegatari.hayanime.data.local.datamodel.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}
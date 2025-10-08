package com.animegatari.hayanime.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsPreferences @Inject constructor(context: Context) {
    val searchNsfw: Flow<Boolean> = context.datastore.data.map { it[SHOW_NSFW_CONTENT] ?: false }
    val suggestionsNsfw: Flow<Boolean> = context.datastore.data.map { it[SHOW_NSFW_SUGGESTIONS] ?: false }

    companion object {
        val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        private val SHOW_NSFW_CONTENT = booleanPreferencesKey("show_nsfw_content")
        private val SHOW_NSFW_SUGGESTIONS = booleanPreferencesKey("show_nsfw_suggestions")
    }
}
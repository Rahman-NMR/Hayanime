package com.animegatari.hayanime.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataStorePreference(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : PreferenceDataStore() {

    override fun putBoolean(key: String?, value: Boolean) {
        scope.launch {
            dataStore.edit { preferences ->
                key?.let { preferences[booleanPreferencesKey(it)] = value }
            }
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return runBlocking {
            dataStore.data.first()[booleanPreferencesKey(key ?: "")] ?: defValue
        }
    }
}
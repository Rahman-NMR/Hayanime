package com.animegatari.hayanime.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

class DataStorePreference(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : PreferenceDataStore() {

    override fun putString(key: String?, value: String?) {
        if (key == null) return
        scope.launch {
            dataStore.edit { preferences ->
                if (value == null) {
                    preferences.remove(stringPreferencesKey(key))
                } else {
                    preferences[stringPreferencesKey(key)] = value
                }
            }
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        if (key == null) return defValue

        return runBlocking {
            dataStore.data
                .catch {
                    if (it is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw it
                    }
                }.first()[stringPreferencesKey(key)]
        } ?: defValue
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null) return

        scope.launch {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        if (key == null) return defValue

        return runBlocking {
            dataStore.data.first()[booleanPreferencesKey(key)]
        } ?: defValue
    }
}
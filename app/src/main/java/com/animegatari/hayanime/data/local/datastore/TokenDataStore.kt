package com.animegatari.hayanime.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStore @Inject constructor(private val context: Context) {
    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }
    val codeVerifier: Flow<String?> = context.dataStore.data.map { it[CODE_VERIFIER_KEY] }

    suspend fun saveAuthToken(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveCodeVerifier(codeVerifier: String) {
        context.dataStore.edit { preferences ->
            preferences[CODE_VERIFIER_KEY] = codeVerifier
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val CODE_VERIFIER_KEY = stringPreferencesKey("code_verifier")
    }
}
package com.animegatari.hayanime.domain.repository

import android.net.Uri
import com.animegatari.hayanime.data.remote.response.AccessTokenResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    val codeVerifier: Flow<String?>

    fun getAuthorizationUrl(codeChallenge: String): Uri
    suspend fun getAccessToken(code: String, codeVerifier: String): AccessTokenResponse
    suspend fun saveAuthTokens(accessToken: String, refreshToken: String)
    suspend fun clearAuthTokens()
    suspend fun saveCodeVerifier(codeVerifier: String)
}
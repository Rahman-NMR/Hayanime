package com.animegatari.hayanime.domain.repository

import android.net.Uri
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.data.local.datastore.TokenDataStore
import com.animegatari.hayanime.data.remote.api.AuthApiService
import com.animegatari.hayanime.data.remote.response.AccessTokenResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {
    override val accessToken: Flow<String?> = tokenDataStore.accessToken
    override val refreshToken: Flow<String?> = tokenDataStore.refreshToken
    override val codeVerifier: Flow<String?> = tokenDataStore.codeVerifier

    override fun getAuthorizationUrl(codeChallenge: String): Uri {
        return Uri.Builder().scheme("https")
            .authority("myanimelist.net")
            .appendPath("v1").appendPath("oauth2").appendPath("authorize")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", BuildConfig.MAL_CLIENT_ID)
            .appendQueryParameter("client_secret", BuildConfig.MAL_CLIENT_SECRET)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("code_challenge_method", "plain")
            .appendQueryParameter("redirect_uri", "${BuildConfig.APPLICATION_ID}://callback")
            .appendQueryParameter("scope", "write:users")
            .build()
    }

    override suspend fun getAccessToken(code: String, codeVerifier: String): AccessTokenResponse {
        return authApiService.getAccessToken(
            clientId = BuildConfig.MAL_CLIENT_ID,
            clientSecret = BuildConfig.MAL_CLIENT_SECRET,
            code = code,
            codeVerifier = codeVerifier,
            redirectUri = "${BuildConfig.APPLICATION_ID}://callback"
        )
    }

    override suspend fun saveAuthTokens(accessToken: String, refreshToken: String) {
        tokenDataStore.saveAuthToken(accessToken, refreshToken)
    }

    override suspend fun clearAuthTokens() {
        tokenDataStore.clearTokens()
    }

    override suspend fun saveCodeVerifier(codeVerifier: String) {
        tokenDataStore.saveCodeVerifier(codeVerifier)
    }
}
package com.animegatari.hayanime.data.remote.okhttp

import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.data.local.datastore.TokenDataStore
import com.animegatari.hayanime.data.remote.api.AuthApiService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authApiService: Provider<AuthApiService>,
) : Authenticator {
    private val refreshTokenMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            refreshTokenMutex.withLock { // Protect the refresh token block
                // Check if the token has been refreshed by another request
                val currentAccessToken = tokenDataStore.accessToken.firstOrNull()
                if (response.request.header("Authorization") != "Bearer $currentAccessToken") {
                    // If the token is different (already refreshed), retry with the new token
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                // Continue with the refresh token logic if the token is still the same as the one that caused the 401
                val localRefreshToken = tokenDataStore.refreshToken.firstOrNull()

                if (localRefreshToken.isNullOrEmpty()) {
                    // No refresh token, cannot refresh
                    // Consider logging out the user or other actions
                    tokenDataStore.clearTokens()
                    return@runBlocking null
                }

                try {
                    val newTokens = authApiService.get().refreshAccessToken(
                        clientId = BuildConfig.MAL_CLIENT_ID,
                        clientSecret = BuildConfig.MAL_CLIENT_SECRET,
                        refreshToken = localRefreshToken
                    )
                    tokenDataStore.saveAuthToken(newTokens.accessToken, newTokens.refreshToken)

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                } catch (_: Exception) {
                    tokenDataStore.clearTokens()
                    null
                }
            }
        }
    }
}
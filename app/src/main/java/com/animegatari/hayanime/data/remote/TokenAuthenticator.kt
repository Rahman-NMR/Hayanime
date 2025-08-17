package com.animegatari.hayanime.data.remote

import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.data.local.datastore.TokenDataStore
import com.animegatari.hayanime.data.remote.api.AuthApiService
import com.animegatari.hayanime.data.remote.response.AccessTokenResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authApiService: AuthApiService,
) : Authenticator { //TODO: used?
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking { tokenDataStore.refreshToken.firstOrNull() }

        if (!refreshToken.isNullOrEmpty() || response.request.header("Authorization") != "Bearer $refreshToken") {
            return null
        }

        val newToken = runBlocking { tokenDataStore.accessToken.firstOrNull() }
        if (response.request.header("Authorization") != "Bearer $newToken") {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        }
        return runBlocking {
            try {
                val newTokens: AccessTokenResponse = authApiService.refreshAccessToken(
                    clientId = BuildConfig.MAL_CLIENT_ID,
                    clientSecret = BuildConfig.MAL_CLIENT_SECRET,
                    refreshToken = refreshToken
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
package com.animegatari.hayanime.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.R
import com.animegatari.hayanime.domain.repository.AuthRepository
import com.animegatari.hayanime.domain.repository.SearchHistoryRepository
import com.animegatari.hayanime.domain.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val historyRepository: SearchHistoryRepository,
    private val application: Application,
) : AndroidViewModel(application) {
    val isLoggedIn: StateFlow<Boolean?> =
        authRepository.accessToken.combine(authRepository.refreshToken) { accessToken, refreshToken ->
            !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun saveCodeVerifier(codeVerifier: String) = viewModelScope.launch {
        authRepository.saveCodeVerifier(codeVerifier)
    }

    fun handleAuthCode(code: String?, onResult: (Response<Boolean>) -> Unit) {
        if (code.isNullOrEmpty()) {
            onResult(Response.Error(application.getString(R.string.message_invalid_code)))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val codeVerifier = authRepository.codeVerifier.firstOrNull()
                if (codeVerifier.isNullOrEmpty()) {
                    _isLoading.value = false
                    onResult(Response.Error(application.getString(R.string.message_invalid_code_verifier)))
                    return@launch
                }

                val response = authRepository.getAccessToken(code, codeVerifier)
                authRepository.saveAuthTokens(response.accessToken, response.refreshToken)
            } catch (e: Exception) {
                authRepository.clearAuthTokens()
                onResult(Response.Error(e.localizedMessage))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() = viewModelScope.launch {
        historyRepository.clearHistory()
        authRepository.clearAuthTokens()
    }

    fun authUrl(codeChallenge: String) = authRepository.getAuthorizationUrl(codeChallenge)

    fun validateToken() = viewModelScope.launch {
        val token = authRepository.accessToken.firstOrNull()
        if (token.isNullOrEmpty()) {
            authRepository.clearAuthTokens()
        }
    }
}
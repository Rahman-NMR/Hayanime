package com.animegatari.hayanime.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() = viewModelScope.launch {
        authRepository.accessToken.collect { token ->
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }

    fun saveCodeVerifier(codeVerifier: String) = viewModelScope.launch {
        authRepository.saveCodeVerifier(codeVerifier)
    }

    fun handleAuthCode(code: String?) = viewModelScope.launch {
        _isLoading.value = true

        try {
            val codeVerifier = authRepository.codeVerifier.firstOrNull()
            if (!code.isNullOrEmpty() && !codeVerifier.isNullOrEmpty()) {
                val response = authRepository.getAccessToken(code, codeVerifier)
                authRepository.saveAuthTokens(response.accessToken, response.refreshToken)
            } else {
                _isLoggedIn.value = false
            }
        } catch (_: Exception) {
            _isLoggedIn.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() = viewModelScope.launch {
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
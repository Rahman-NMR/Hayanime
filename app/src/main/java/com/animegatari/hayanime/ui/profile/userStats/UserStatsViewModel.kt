package com.animegatari.hayanime.ui.profile.userStats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.domain.repository.UserInfoRepository
import com.animegatari.hayanime.domain.utils.onError
import com.animegatari.hayanime.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val userRepository: UserInfoRepository,
) : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getUserStats()
    }

    fun getUserStats() = viewModelScope.launch {
        _isLoading.value = true

        userRepository.getProfileInfo()
            .onSuccess { data ->
                _userInfo.value = data
                _isLoading.value = false
            }
            .onError {
                _isLoading.value = false
            }
    }
}
package com.animegatari.hayanime.presentation.feature.profile.userStats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.data.remote.dto.UserInfo
import com.animegatari.hayanime.domain.repository.UserInfoRepository
import com.animegatari.hayanime.core.result.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserStatsViewModel @Inject constructor(
    userRepository: UserInfoRepository,
) : ViewModel() {
    val userInfo: StateFlow<Response<UserInfo>> = userRepository.getProfileInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Response.Loading
        )
}
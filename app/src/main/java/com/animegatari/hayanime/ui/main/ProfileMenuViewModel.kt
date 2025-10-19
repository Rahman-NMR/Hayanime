package com.animegatari.hayanime.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.domain.repository.UserInfoRepository
import com.animegatari.hayanime.domain.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProfileMenuViewModel @Inject constructor(
    private val profileRepository: UserInfoRepository,
) : ViewModel() {
    private val refreshTrigger = MutableStateFlow(Unit)

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileImageUri: StateFlow<Response<UserInfo>> = refreshTrigger.flatMapLatest {
        profileRepository.getProfileImage()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Response.Loading,
    )

    fun getProfileImage() {
        refreshTrigger.value = Unit
    }
}
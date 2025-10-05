package com.animegatari.hayanime.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegatari.hayanime.domain.repository.UserInfoRepository
import com.animegatari.hayanime.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileMenuViewModel @Inject constructor(
    private val profileRepository: UserInfoRepository,
) : ViewModel() {
    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri = _profileImageUri.asStateFlow()

    init {
        getProfileImage()
    }

    fun getProfileImage() = viewModelScope.launch {
        profileRepository.getProfileImage().onSuccess { data ->
            _profileImageUri.value = data?.picture
        }
    }
}
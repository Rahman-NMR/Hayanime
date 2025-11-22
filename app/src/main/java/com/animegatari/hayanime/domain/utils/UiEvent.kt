package com.animegatari.hayanime.domain.utils

sealed class UiEvent {
    object DataModified : UiEvent()
    object DataUpdated : UiEvent()
    data class UpdateProgressError(val message: String?) : UiEvent()
}
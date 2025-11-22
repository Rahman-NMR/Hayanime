package com.animegatari.hayanime.domain.utils

inline fun UiEvent.onDataModified(action: () -> Unit): UiEvent {
    if (this is UiEvent.DataModified) action()
    return this
}

inline fun UiEvent.onDataUpdated(action: () -> Unit): UiEvent {
    if (this is UiEvent.DataUpdated) action()
    return this
}

inline fun UiEvent.onUpdateProgressError(action: (String?) -> Unit): UiEvent {
    if (this is UiEvent.UpdateProgressError) action(message)
    return this
}
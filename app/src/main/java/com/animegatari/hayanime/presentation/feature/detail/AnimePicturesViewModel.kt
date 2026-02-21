package com.animegatari.hayanime.presentation.feature.detail

import androidx.lifecycle.ViewModel
import com.animegatari.hayanime.data.remote.dto.Picture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AnimePicturesViewModel @Inject constructor() : ViewModel() {
    private val _pictures = MutableStateFlow<List<Picture?>>(emptyList())
    val pictures = _pictures.asStateFlow()

    fun setPictures(newPictures: List<Picture?>) {
        _pictures.value = newPictures
    }

    fun clearPictures() {
        _pictures.value = emptyList()
    }
}
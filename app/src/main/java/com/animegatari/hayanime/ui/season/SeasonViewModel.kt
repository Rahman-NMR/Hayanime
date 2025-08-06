package com.animegatari.hayanime.ui.season

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeasonViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Season Fragment"
    }

    val text: LiveData<String> = _text
}
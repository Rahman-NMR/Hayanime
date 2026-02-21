package com.animegatari.hayanime

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.animegatari.hayanime.data.local.datastore.SettingsPreferences
import com.animegatari.hayanime.core.config.Theme
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate() {
        super.onCreate()
        observeTheme()
    }

    private fun observeTheme() {
        settingsPreferences.themeSettings
            .onEach { theme ->
                val mode = when (theme) {
                    Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    Theme.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }.launchIn(CoroutineScope(Dispatchers.Main))
    }
}
package com.animegatari.hayanime.ui.profile.settings

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.local.datastore.DataStorePreference
import com.animegatari.hayanime.data.local.datastore.SettingsPreferences.Companion.datastore

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = DataStorePreference(requireContext().datastore, lifecycleScope)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
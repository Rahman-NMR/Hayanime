package com.animegatari.hayanime.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.animegatari.hayanime.databinding.BottomSheetEditOwnListBinding
import com.animegatari.hayanime.ui.dialog.YearPickerDialogFragment
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditOwnListBottomSheet : Fragment() {
    private var _binding: BottomSheetEditOwnListBinding? = null
    private val binding get() = _binding!!

    private var initialAnimeId: Int? = INVALID_ANIME_ID
    private var requestKey: String = DETAIL_REQUEST_KEY
    private val args: EditOwnListBottomSheetArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialAnimeId = args.animeId
        requestKey = args.requestKey

        setupYearPickerListener()
    }

    private fun setupYearPickerListener() {
        setYearPickerListener(START_DATE_YEAR_REQUEST_KEY) { selectedYear ->
        }

        setYearPickerListener(FINISH_DATE_YEAR_REQUEST_KEY) { selectedYear ->
        }
    }

    private fun setYearPickerListener(requestKey: String, onYearSelected: (Int) -> Unit) {
        childFragmentManager.setFragmentResultListener(requestKey, this) { _, bundle ->
            val selectedYear = bundle.getInt(YearPickerDialogFragment.BUNDLE_KEY_SELECTED_YEAR)
            onYearSelected(selectedYear)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetEditOwnListBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val INVALID_ANIME_ID = 0

        const val DETAIL_REQUEST_KEY = "DETAIL_REQUEST_KEY"
        const val BUNDLE_KEY_DELETED = "BUNDLE_KEY_DELETED"
        const val BUNDLE_KEY_UPDATED = "BUNDLE_KEY_UPDATED"

        const val START_DATE_YEAR_REQUEST_KEY = "START_DATE_YEAR_REQUEST"
        const val FINISH_DATE_YEAR_REQUEST_KEY = "FINISH_DATE_YEAR_REQUEST"
    }
}
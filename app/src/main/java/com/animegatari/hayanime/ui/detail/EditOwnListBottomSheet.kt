package com.animegatari.hayanime.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animegatari.hayanime.databinding.BottomSheetEditOwnListBinding
import com.animegatari.hayanime.ui.dialog.YearPickerDialogFragment
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditOwnListBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetEditOwnListBinding? = null
    private val binding get() = _binding!!
    private var initialAnimeId: Int? = INVALID_ANIME_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            initialAnimeId = it.getInt(ARG_ANIME_ID, INVALID_ANIME_ID)
        }

        childFragmentManager.setFragmentResultListener(YearPickerDialogFragment.REQUEST_KEY, this) { requestKey, bundle ->
            if (requestKey == YearPickerDialogFragment.REQUEST_KEY) {
                val selectedYear = bundle.getInt(YearPickerDialogFragment.BUNDLE_KEY_SELECTED_YEAR)
                toastShort(requireContext(), "Selected year: $selectedYear")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetEditOwnListBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val ARG_ANIME_ID = "anime_id"
        private const val INVALID_ANIME_ID = 0

        fun newInstance(animeId: Int) = EditOwnListBottomSheet().apply {
            arguments = Bundle().apply { putInt(ARG_ANIME_ID, animeId) }
        }
    }
}
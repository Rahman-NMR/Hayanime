package com.animegatari.hayanime.ui.main.season

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.types.ContinuedAnime
import com.animegatari.hayanime.data.types.MediaType
import com.animegatari.hayanime.data.types.SeasonStart
import com.animegatari.hayanime.data.types.SortingAnime
import com.animegatari.hayanime.databinding.BottomsheetSeasonFilterBinding
import com.animegatari.hayanime.ui.dialog.YearPickerDialogFragment
import com.animegatari.hayanime.ui.utils.interfaces.ViewUtils.setupDynamicChips
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeasonFilterBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomsheetSeasonFilterBinding? = null
    private val binding get() = _binding!!

    private val seasonViewModel: SeasonViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetSeasonFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipgSeason.setupDynamicChips(
            hasIcon = true,
            chipInfoProvider = SeasonStart.entries.filter { it != SeasonStart.UNKNOWN }
        )
        binding.chipgMediaType.setupDynamicChips(
            hasIcon = false,
            chipInfoProvider = MediaType.entries.filter { it != MediaType.UNKNOWN }
        )
        setupYearPickerListener()
        setupInteractionsListeners()
        observeViewModelStates()
    }

    private fun setupYearPickerListener() {
        childFragmentManager.setFragmentResultListener(
            YearPickerDialogFragment.YEAR_PICKER_REQUEST_KEY,
            this
        ) { _, bundle ->
            val selectedYear = bundle.getInt(YearPickerDialogFragment.BUNDLE_KEY_SELECTED_YEAR)
            seasonViewModel.changeYear(selectedYear)
        }
    }

    private fun setupInteractionsListeners() = with(binding) {
        btnChangeYear.setOnClickListener { displayYearPickerDialog() }
        chipgSeason.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val checkedChip = group.findViewById<Chip>(checkedIds.first())
            val selectedSeasonValue = checkedChip.tag as String
            seasonViewModel.changeSeason(selectedSeasonValue)
        }

        chipgMediaType.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedMediaTypeValue = if (checkedIds.isNotEmpty()) {
                val checkedChip = group.findViewById<Chip>(checkedIds.first())
                checkedChip.tag as String
            } else {
                null
            }
            seasonViewModel.filterByMediaType(selectedMediaTypeValue)
        }

        btnSortBy.setOnClickListener { seasonViewModel.toggleSortKey() }
        btnContinuedAnime.setOnClickListener { seasonViewModel.toggleContinuedAnime() }
    }

    private fun displayYearPickerDialog() {
        val selectedYear = seasonViewModel.seasonalFilterState.value
        val dialog = YearPickerDialogFragment.newInstance(
            initialYear = selectedYear.year,
            requestKey = YearPickerDialogFragment.YEAR_PICKER_REQUEST_KEY,
            dialogTitle = getString(R.string.title_choose_season_year)
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun setupFilterState(year: Int, sort: String, isContinued: Boolean) = with(binding) {
        val continuedAnime = ContinuedAnime.fromBoolean(isContinued)
        btnContinuedAnime.text = getString(continuedAnime.stringResId)
        btnSortBy.text = getString(SortingAnime.keyValue(sort).stringResId)
        btnChangeYear.text = year.toString()
    }

    private fun setChipSelectionState(seasonValue: String, mediaTypeValue: String?) = with(binding) {
        val seasonChip = chipgSeason.findViewWithTag<Chip>(seasonValue)
        seasonChip?.takeIf { !it.isChecked }?.let {
            chipgSeason.check(seasonChip.id)
        }

        val mediaTypeChip = chipgMediaType.findViewWithTag<Chip>(mediaTypeValue)
        mediaTypeChip?.takeIf { !it.isChecked }?.let {
            chipgMediaType.check(mediaTypeChip.id)
        }
    }

    private fun observeViewModelStates() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    seasonViewModel.seasonalFilterState.collectLatest { (year, season, sort, mediaType, isContinued) ->
                        setupFilterState(year, sort, isContinued)
                        setChipSelectionState(season, mediaType)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
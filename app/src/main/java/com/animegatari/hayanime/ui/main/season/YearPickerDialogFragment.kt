package com.animegatari.hayanime.ui.main.season

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.DialogYearPickerBinding
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.animegatari.hayanime.utils.TimeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class YearPickerDialogFragment : DialogFragment() {
    private var _binding: DialogYearPickerBinding? = null
    private val binding get() = _binding!!

    private val seasonViewModel: SeasonViewModel by activityViewModels()
    private var initialYear: Int = TimeUtils.getCurrentYear()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initialYear = it.getInt(ARG_CURRENT_YEAR, TimeUtils.getCurrentYear())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogYearPickerBinding.inflate(layoutInflater)

        setupRecyclerView()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_choose_season_year))
            .setView(binding.root)
            .setCancelable(false)
            .setNegativeButton(getString(R.string.label_cancel)) { _, _ -> dismiss() }
            .create()
        return dialog
    }

    private fun setupRecyclerView() {
        val systemCurrentYear = TimeUtils.getCurrentYear()
        val startYear = START_YEAR
        val endYear = systemCurrentYear
        val displayedYears = (startYear..endYear).toList()

        val yearPickerAdapter = YearPickerAdapter(displayedYears, initialYear) { selectedYear ->
            seasonViewModel.changeYear(selectedYear)
            dismiss()
        }

        binding.yearsDialogRecyclerview.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                calculateSpanCount(requireContext(), 120)
            )
            adapter = yearPickerAdapter

            val selectedIndex = displayedYears.indexOf(initialYear)
            if (selectedIndex != -1) smoothScrollToPosition(selectedIndex)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_CURRENT_YEAR = "current_year"
        private const val START_YEAR = 1960
    }
}
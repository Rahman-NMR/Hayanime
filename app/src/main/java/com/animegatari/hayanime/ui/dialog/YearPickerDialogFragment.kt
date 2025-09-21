package com.animegatari.hayanime.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.DialogYearPickerBinding
import com.animegatari.hayanime.ui.adapter.YearPickerAdapter
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator
import com.animegatari.hayanime.utils.TimeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class YearPickerDialogFragment : DialogFragment() {
    private var _binding: DialogYearPickerBinding? = null
    private val binding get() = _binding!!

    private var initialYear: Int = TimeUtils.getCurrentYear()
    private var dialogTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            initialYear = it.getInt(ARG_INITIAL_YEAR, TimeUtils.getCurrentYear())
            dialogTitle = it.getString(ARG_DIALOG_TITLE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogYearPickerBinding.inflate(layoutInflater)

        setupRecyclerView()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
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
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_SELECTED_YEAR to selectedYear))
            dismiss()
        }

        binding.yearsDialogRecyclerview.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                SpanCalculator.calculateSpanCount(requireContext(), 120)
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
        const val REQUEST_KEY = "YEAR_PICKER_REQUEST"
        const val BUNDLE_KEY_SELECTED_YEAR = "SELECTED_YEAR"

        const val ARG_INITIAL_YEAR = "initial_year"
        const val ARG_DIALOG_TITLE = "dialog_title"
        private const val START_YEAR = 1960

        fun newInstance(initialYear: Int, dialogTitle: String) = YearPickerDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_INITIAL_YEAR, initialYear)
                putString(ARG_DIALOG_TITLE, dialogTitle)
            }
        }
    }
}
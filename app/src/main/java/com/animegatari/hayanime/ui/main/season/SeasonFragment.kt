package com.animegatari.hayanime.ui.main.season

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.types.SeasonStart
import com.animegatari.hayanime.data.types.SortingAnime
import com.animegatari.hayanime.databinding.FragmentSeasonBinding
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.utils.PopupMessage.toastShort
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.layout.FabUtils.fabScrollBehavior
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeasonFragment : Fragment() {
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    private val seasonViewModel: SeasonViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            loadingIndicator.hide()
            fabScrollToTop.hide()

            val animeAdapter = animeAdapter()

            setupInteractions(animeAdapter)
            observeSelectedSeason()
            observeSelectedYear()
            observeSortButton()
            setupRecyclerView(animeAdapter)
            adapterDataHandler(animeAdapter)
            handleLoadState(animeAdapter)
        }
    }

    private fun FragmentSeasonBinding.setupInteractions(animeAdapter: AnimeGridAdapter) {
        recyclerView.addOnScrollListener(fabScrollBehavior(fabScrollToTop))
        fabScrollToTop.setOnClickListener { recyclerView.smoothScrollToPosition(0) }
        btnChangeYear.setOnClickListener { displayYearPickerDialog() }
        btnChangeSeason.setOnClickListener { displaySeasonPicker() }
        btnSortBy.setOnClickListener { seasonViewModel.toggleSortKey() }
        swipeRefresh.setOnRefreshListener {
            animeAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }

        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_avatar -> {
                    toastShort(requireContext(), "TODO go to profile")

                    true
                }

                else -> false
            }
        }
    }

    private fun displaySeasonPicker() {
        val anchorView = binding.btnChangeSeason
        val popupMenu = PopupMenu(requireContext(), anchorView)

        val displayableSeasons = SeasonStart.entries.filter { it != SeasonStart.UNKNOWN }
        displayableSeasons.forEachIndexed { index, season ->
            popupMenu.menu.add(0, season.ordinal, index, getString(season.stringResId))
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedSeasonEnum = try {
                displayableSeasons.getOrNull(menuItem.itemId)
            } catch (_: IndexOutOfBoundsException) {
                null
            }

            selectedSeasonEnum?.let {
                seasonViewModel.changeSeason(it.apiValue)
            }
            true
        }

        popupMenu.show()
    }

    private fun displayYearPickerDialog() {
        val currentYear = seasonViewModel.selectedYear.value
        val dialog = YearPickerDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(YearPickerDialogFragment.ARG_CURRENT_YEAR, currentYear)
            }
        }
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun animeAdapter(): AnimeGridAdapter = AnimeGridAdapter({ anime ->
        toastShort(requireContext(), "TODO action ${anime.title}")
    }, { anime ->
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "${BuildConfig.BASE_URL}anime/${anime.id}".toUri()
        }
        startActivity(intent)
    })

    private fun FragmentSeasonBinding.setupRecyclerView(animeAdapter: AnimeGridAdapter) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = animeAdapter
    }

    private fun adapterDataHandler(animeAdapter: AnimeGridAdapter) = lifecycleScope.launch {
        seasonViewModel.animeList.collectLatest { pagingData ->
            animeAdapter.submitData(pagingData)
        }
    }

    private fun handleLoadState(animeAdapter: AnimeGridAdapter) = lifecycleScope.launch {
        animeAdapter.loadStateFlow.collectLatest { loadStates ->
            val refreshState = loadStates.refresh

            val thisSeason = getString(R.string.label_this_season)
            binding.tvInfoMsg.apply {
                text = getString(R.string.info_no_results_found, thisSeason)
                isVisible = refreshState is LoadState.NotLoading && animeAdapter.itemCount == 0
            }
            binding.loadingIndicator.isVisible = when (refreshState) {
                is LoadState.Loading -> true
                is LoadState.Error -> {
                    toastShort(requireContext(), getString(R.string.message_error_occurred))
                    false
                }

                else -> false
            }
        }
    }

    private fun FragmentSeasonBinding.observeSelectedSeason() = lifecycleScope.launch {
        seasonViewModel.selectedSeason.collectLatest { season ->
            btnChangeSeason.text = season.replaceFirstChar { it.titlecase() }
        }
    }

    private fun FragmentSeasonBinding.observeSelectedYear() = lifecycleScope.launch {
        seasonViewModel.selectedYear.collectLatest { year ->
            btnChangeYear.text = year.toString()
        }
    }

    private fun FragmentSeasonBinding.observeSortButton() = lifecycleScope.launch {
        seasonViewModel.sortKey.collectLatest { sort ->
            btnSortBy.text = getString(SortingAnime.keyValue(sort).stringResId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
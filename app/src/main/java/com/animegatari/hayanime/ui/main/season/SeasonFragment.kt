package com.animegatari.hayanime.ui.main.season

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.types.SeasonStart
import com.animegatari.hayanime.data.types.SortingAnime
import com.animegatari.hayanime.databinding.FragmentSeasonBinding
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.detail.EditOwnListFragment
import com.animegatari.hayanime.ui.dialog.YearPickerDialogFragment
import com.animegatari.hayanime.ui.main.MainViewModel
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.ANIMATION_DURATION
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeasonFragment : Fragment(), ReselectableFragment {
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    private val seasonViewModel: SeasonViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupYearPickerListener()
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

    private fun setupAdapterRefreshListener(animeAdapter: AnimeGridAdapter) {
        parentFragmentManager.setFragmentResultListener(
            EditOwnListFragment.DETAIL_REQUEST_KEY,
            this
        ) { _, bundle ->
            val resultUpdate = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_UPDATED)
            val resulDelete = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_DELETED)

            if (resultUpdate || resulDelete) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(ANIMATION_DURATION)
                    animeAdapter.refresh()

                    if (resultUpdate) mainViewModel.showSnackbar(getString(R.string.message_anime_updated_successfully))
                    if (resulDelete) mainViewModel.showSnackbar(getString(R.string.message_anime_deleted_successfully))
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animeAdapter = initializeAnimeAdapter()

        setupAdapterRefreshListener(animeAdapter)
        initializeViews()
        setupInteractions(animeAdapter)
        setupRecyclerView(animeAdapter)

        observeViewModelStates(animeAdapter)
        handleLoadState(animeAdapter)
    }

    private fun initializeViews() = with(binding) {
        val thisSeason = getString(R.string.label_this_season)

        tvInfoMsg.text = getString(R.string.info_no_results_found, thisSeason)
        loadingIndicator.hide()
    }

    private fun setupInteractions(animeAdapter: AnimeGridAdapter) = with(binding) {
        btnChangeYear.setOnClickListener { displayYearPickerDialog() }
        btnChangeSeason.setOnClickListener { displaySeasonPicker() }
        btnSortBy.setOnClickListener { seasonViewModel.toggleSortKey() }
        swipeRefresh.setOnRefreshListener {
            animeAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
        toolBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_avatar -> {
            toastShort(requireContext(), "TODO go to profile")

            true
        }

        else -> false
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
        val selectedYear = seasonViewModel.selectedYear.value
        val dialog = YearPickerDialogFragment.newInstance(
            initialYear = selectedYear,
            requestKey = YearPickerDialogFragment.YEAR_PICKER_REQUEST_KEY,
            dialogTitle = getString(R.string.title_choose_season_year)
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun initializeAnimeAdapter(): AnimeGridAdapter = AnimeGridAdapter(
        onItemClicked = { anime ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "${BuildConfig.BASE_URL}anime/${anime.id}".toUri()
            }
            startActivity(intent)
        },
        onEditMyListClicked = { anime ->
            anime.id?.let { animeId ->
                val action = SeasonFragmentDirections.actionNavigationToNavigationEditAnime(
                    animeId = animeId,
                    requestKey = EditOwnListFragment.DETAIL_REQUEST_KEY
                )
                findNavController().navigate(action)
            } ?: run {
                toastShort(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        }
    )

    private fun setupRecyclerView(animeAdapter: AnimeGridAdapter) = with(binding) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = animeAdapter
    }

    private fun handleLoadState(animeAdapter: AnimeGridAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        animeAdapter.loadStateFlow.collectLatest { loadStates ->
            val refreshState = loadStates.refresh

            binding.tvInfoMsg.isVisible = refreshState is LoadState.NotLoading && animeAdapter.itemCount == 0
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

    private fun observeViewModelStates(animeAdapter: AnimeGridAdapter) = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            seasonViewModel.selectedSeason
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { btnChangeSeason.text = it.replaceFirstChar { s -> s.titlecase() } }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            seasonViewModel.selectedYear
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { btnChangeYear.text = it.toString() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            seasonViewModel.sortKey
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { btnSortBy.text = getString(SortingAnime.keyValue(it).stringResId) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            seasonViewModel.animeList
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { animeAdapter.submitData(it) }
        }
    }

    override fun onReselected() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.animegatari.hayanime.ui.main.season

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.awaitNotLoading
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.data.types.SeasonStart
import com.animegatari.hayanime.data.types.SortingAnime
import com.animegatari.hayanime.databinding.FragmentSeasonBinding
import com.animegatari.hayanime.domain.utils.Response
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.detail.EditOwnListFragment
import com.animegatari.hayanime.ui.dialog.YearPickerDialogFragment
import com.animegatari.hayanime.ui.main.ProfileMenuViewModel
import com.animegatari.hayanime.ui.profile.ProfileActivity
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.ANIMATION_DURATION
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.extension.ProfileImage.loadProfileImage
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showSnackbar
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@AndroidEntryPoint
class SeasonFragment : Fragment(), ReselectableFragment {
    private var _binding: FragmentSeasonBinding? = null
    private val binding get() = _binding!!

    private val seasonViewModel: SeasonViewModel by activityViewModels()
    private val profileViewModel: ProfileMenuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animeAdapter = initializeAnimeAdapter()

        setupYearPickerListener(animeAdapter)
        setupAdapterRefreshListener(animeAdapter)
        initializeViews()
        setupInteractions(animeAdapter)
        setupRecyclerView(animeAdapter)

        observeViewModelStates(animeAdapter)
    }

    private fun setupYearPickerListener(animeAdapter: AnimeGridAdapter) {
        childFragmentManager.setFragmentResultListener(
            YearPickerDialogFragment.YEAR_PICKER_REQUEST_KEY,
            this
        ) { _, bundle ->
            val selectedYear = bundle.getInt(YearPickerDialogFragment.BUNDLE_KEY_SELECTED_YEAR)
            seasonViewModel.changeYear(selectedYear)
            animeAdapter.refresh()
            scrollToTopOnLoad(animeAdapter)
        }
    }

    private fun setupAdapterRefreshListener(animeAdapter: AnimeGridAdapter) {
        parentFragmentManager.setFragmentResultListener(
            EditOwnListFragment.DETAIL_REQUEST_KEY,
            this
        ) { _, bundle ->
            val resultUpdate = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_UPDATED)
            val resultDeleted = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_DELETED)

            if (resultUpdate || resultDeleted) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(ANIMATION_DURATION)
                    animeAdapter.refresh()

                    if (resultUpdate) {
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.message_anime_updated_successfully),
                            anchorView = requireActivity().findViewById(R.id.nav_view)
                        )
                    }
                    if (resultDeleted) {
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.message_anime_deleted_successfully),
                            anchorView = requireActivity().findViewById(R.id.nav_view)
                        )
                    }
                }
            }
        }
    }

    private fun initializeViews() = with(binding) {
        tvInfoMsg.text = getString(R.string.info_no_results_found, getString(R.string.label_this_season))
    }

    private fun setupInteractions(animeAdapter: AnimeGridAdapter) = with(binding) {
        btnChangeYear.setOnClickListener { displayYearPickerDialog() }
        btnChangeSeason.setOnClickListener { displaySeasonPicker(animeAdapter) }
        btnSortBy.setOnClickListener {
            seasonViewModel.toggleSortKey()
            animeAdapter.refresh()
            scrollToTopOnLoad(animeAdapter)
        }
        swipeRefresh.setOnRefreshListener {
            profileViewModel.getProfileImage()
            animeAdapter.refresh()
            scrollToTopOnLoad(animeAdapter)
        }
        toolBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_avatar -> {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
            true
        }

        else -> false
    }

    private fun displaySeasonPicker(animeAdapter: AnimeGridAdapter) {
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
                animeAdapter.refresh()
                scrollToTopOnLoad(animeAdapter)
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
            anime.id?.let {
                val action = SeasonFragmentDirections.actionNavigationToNavigationAnimeDetail(it)
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        },
        onEditMyListClicked = { anime ->
            anime.id?.let { animeId ->
                val action = SeasonFragmentDirections.actionNavigationToNavigationEditAnime(
                    animeId = animeId,
                    requestKey = EditOwnListFragment.DETAIL_REQUEST_KEY
                )
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
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

    private fun loadProfileImage(response: Response<UserInfo>) = with(binding) {
        response.onSuccess { userInfo ->
            toolBar.menu.loadProfileImage(
                glide = Glide.with(requireContext()),
                lifecycle = viewLifecycleOwner.lifecycleScope,
                profilePictureUrl = userInfo?.picture,
                menuItemId = R.id.menu_item_avatar
            )
        }
    }

    private fun scrollToTopOnLoad(animeAdapter: AnimeGridAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        animeAdapter.loadStateFlow.awaitNotLoading()
        binding.recyclerView.scrollToPosition(0)
    }

    private fun observeLoadState(animeAdapter: AnimeGridAdapter, loadStates: CombinedLoadStates) = with(binding) {
        val refreshState = loadStates.refresh

        val isListEmpty = animeAdapter.itemCount == 0
        tvInfoMsg.isVisible = isListEmpty && (refreshState is LoadState.NotLoading || refreshState is LoadState.Error)
        swipeRefresh.isRefreshing = refreshState is LoadState.Loading

        if (refreshState is LoadState.Error) {
            val message = when (refreshState.error) {
                is ConnectException -> getString(R.string.message_failed_to_connect)
                is SocketException -> getString(R.string.message_connection_lost)
                is SocketTimeoutException -> getString(R.string.message_timeout)
                is UnknownHostException -> getString(R.string.message_no_internet)
                else -> getString(R.string.message_error_occurred)
            }

            showSnackbar(
                view = root,
                message = message,
                anchorView = requireActivity().findViewById(R.id.nav_view),
                actionName = getString(R.string.action_retry),
                action = { animeAdapter.retry() }
            )
        }
    }

    private fun observeViewModelStates(animeAdapter: AnimeGridAdapter) = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { seasonViewModel.selectedSeason.collectLatest { btnChangeSeason.text = it.replaceFirstChar { s -> s.titlecase() } } }
                launch { seasonViewModel.selectedYear.collectLatest { btnChangeYear.text = it.toString() } }
                launch { seasonViewModel.sortKey.collectLatest { btnSortBy.text = getString(SortingAnime.keyValue(it).stringResId) } }
                launch { seasonViewModel.animeList.collectLatest(animeAdapter::submitData) }
                launch { profileViewModel.profileImageUri.collectLatest(::loadProfileImage) }
                launch { animeAdapter.loadStateFlow.collectLatest { observeLoadState(animeAdapter, it) } }
            }
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
package com.animegatari.hayanime.ui.main.myList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.local.datamodel.MyListModel
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.FragmentMyListBinding
import com.animegatari.hayanime.domain.utils.Response
import com.animegatari.hayanime.domain.utils.UiEvent
import com.animegatari.hayanime.domain.utils.onDataModified
import com.animegatari.hayanime.domain.utils.onDataUpdated
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.domain.utils.onUpdateProgressError
import com.animegatari.hayanime.ui.adapter.MyListAdapter
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.detail.EditOwnListFragment
import com.animegatari.hayanime.ui.main.ProfileMenuViewModel
import com.animegatari.hayanime.ui.profile.ProfileActivity
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.ANIMATION_DURATION
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.extension.ProfileImage.loadProfileImage
import com.animegatari.hayanime.ui.utils.interfaces.ViewUtils.setupDynamicChips
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showSnackbar
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@AndroidEntryPoint
class MyListFragment : Fragment(), ReselectableFragment {
    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    private val myListViewModel: MyListViewModel by activityViewModels()
    private val profileViewModel: ProfileMenuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myListAdapter = initializeMyListAdapter()

        setupAdapterRefreshListener()
        initializeViews()
        setupInteractions()
        setupRecyclerView(myListAdapter)

        observeViewModelStates(myListAdapter)
    }

    private fun setupAdapterRefreshListener() {
        parentFragmentManager.setFragmentResultListener(
            EditOwnListFragment.DETAIL_REQUEST_KEY,
            this
        ) { _, bundle ->
            val resultUpdate = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_UPDATED)
            val resultDeleted = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_DELETED)

            if (resultUpdate || resultDeleted) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(ANIMATION_DURATION)
                    myListViewModel.notifyDataUpdated()

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
        tvInfoMsg.text = getString(R.string.info_no_results_found, getString(R.string.title_my_list))
        chipGroup.setupDynamicChips(
            hasIcon = false,
            chipInfoProvider = WatchingStatus.entries.filter { it != WatchingStatus.UNKNOWN }
        )
    }

    private fun setupInteractions() = with(binding) {
        btnOpenFilter.setOnClickListener {
            val bottomSheet = MyListFilterBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds -> handleChipGroupSelection(group, checkedIds) }
        swipeRefresh.setOnRefreshListener {
            profileViewModel.getProfileImage()
            myListViewModel.notifyDataModified()
        }
        toolBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleChipGroupSelection(group: ChipGroup, checkedIds: List<Int>) = with(binding) {
        val selectedStatus = checkedIds.firstOrNull()
            ?.let { group.findViewById<Chip>(it)?.tag as? String }

        myListViewModel.getAnimeList(selectedStatus)

        val statusStringResId = WatchingStatus.fromApiValue(selectedStatus).stringResId
        tvInfoMsg.text = getString(R.string.info_no_results_found, getString(statusStringResId))
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_avatar -> {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
            true
        }

        else -> false
    }

    private fun initializeMyListAdapter(): MyListAdapter = MyListAdapter(
        onItemClicked = { anime ->
            anime.id?.let { animeId ->
                val action = MyListFragmentDirections.actionNavigationToNavigationAnimeDetail(animeId)
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        },
        onEditMyListClicked = { anime ->
            anime.id?.let { animeId ->
                val action = MyListFragmentDirections.actionNavigationToNavigationEditAnime(
                    animeId = animeId,
                    requestKey = EditOwnListFragment.DETAIL_REQUEST_KEY
                )
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        },
        onAddProgressEpisode = { anime ->
            myListViewModel.updateAnimeProgress(
                animeId = anime.id,
                currentEpisodeProgress = anime.myListStatus?.numEpisodesWatched,
                numEpisode = anime.numEpisodes,
            )
        }
    )

    private fun setupRecyclerView(myListAdapter: MyListAdapter) = with(binding) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = myListAdapter
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

    private fun setChipSelectionState(value: MyListModel) = with(binding) {
        val watchingStatusChip = chipGroup.findViewWithTag<Chip>(value.watchingStatus)
        watchingStatusChip?.takeIf { !it.isChecked }?.let {
            chipGroup.check(watchingStatusChip.id)
        }
    }

    private fun scrollToTopOnLoad(myListAdapter: MyListAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        myListAdapter.loadStateFlow.awaitNotLoading()
        binding.recyclerView.scrollToPosition(0)
    }

    private fun observeLoadState(myListAdapter: MyListAdapter, loadStates: CombinedLoadStates) = with(binding) {
        val refreshState = loadStates.refresh

        val isListEmpty = myListAdapter.itemCount == 0
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
                action = { myListAdapter.retry() }
            )
        }
    }

    private fun observeViewModelStates(myListAdapter: MyListAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { myListViewModel.myAnimeList.collectLatest(myListAdapter::submitData) }
                launch { profileViewModel.profileImageUri.collectLatest(::loadProfileImage) }
                launch { myListAdapter.loadStateFlow.collectLatest { observeLoadState(myListAdapter, it) } }
                launch { myListViewModel.myAnimeListState.collectLatest(::setChipSelectionState) }
                launch { myListViewModel.events.collectLatest { handleEvent(it, myListAdapter) } }
            }
        }
    }

    private fun handleEvent(event: UiEvent, myListAdapter: MyListAdapter) {
        event.onDataModified {
            myListAdapter.refresh()
            scrollToTopOnLoad(myListAdapter)
        }.onDataUpdated {
            myListAdapter.refresh()
        }.onUpdateProgressError { message ->
            showToast(requireContext(), message ?: getString(R.string.message_error_occurred))
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